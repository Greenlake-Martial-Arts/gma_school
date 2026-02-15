// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.LevelDao
import com.gma.school.database.data.dao.LevelRequirementDao
import com.gma.school.database.data.dao.MoveDao
import com.gma.school.database.data.dao.StudentDao
import com.gma.school.database.data.dao.StudentLevelDao
import com.gma.school.database.data.dao.StudentProgressDao
import com.gma.tsunjo.school.api.responses.RequirementProgress
import com.gma.tsunjo.school.api.responses.StudentProgressByLevel
import com.gma.tsunjo.school.api.responses.StudentProgressWithDetails
import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.models.ProgressState
import com.gma.tsunjo.school.domain.models.StudentProgress

class StudentProgressRepository(
    private val studentProgressDao: StudentProgressDao,
    private val levelRequirementDao: LevelRequirementDao,
    private val levelDao: LevelDao,
    private val moveDao: MoveDao,
    private val studentDao: StudentDao,
    private val studentLevelDao: StudentLevelDao,
    private val auditLogDao: com.gma.school.database.data.dao.AuditLogDao
) {

    fun getProgressByStudentAndLevel(studentId: Long, levelId: Long): StudentProgressByLevel? {
        val level = levelDao.findById(levelId) ?: return null
        val requirements = levelRequirementDao.findByLevel(levelId)
        val studentProgress = studentProgressDao.findByStudent(studentId)

        val requirementProgressList = requirements.map { req ->
            val move = moveDao.findById(req.moveId)!!
            val progress = studentProgress.find { it.levelRequirementId == req.id }
            val instructor = progress?.instructorId?.let {
                studentDao.findById(it)?.let { student ->
                    com.gma.tsunjo.school.api.responses.InstructorInfo(
                        id = student.id,
                        name = "${student.firstName} ${student.lastName}"
                    )
                }
            }

            RequirementProgress(
                id = req.id,
                sortOrder = req.sortOrder,
                move = move,
                isRequired = req.isRequired,
                levelSpecificNotes = req.levelSpecificNotes,
                progress = com.gma.tsunjo.school.api.responses.ProgressStatus(
                    id = progress?.id,
                    status = progress?.status ?: ProgressState.NOT_STARTED,
                    completedAt = progress?.completedAt,
                    instructor = instructor,
                    attempts = progress?.attempts ?: 0,
                    notes = progress?.notes,
                    lastUpdated = progress?.updatedAt
                )
            )
        }

        return StudentProgressByLevel(level, requirementProgressList)
    }

    fun getAllProgress(): List<StudentProgressWithDetails> {
        val progressList = studentProgressDao.findAll()
        return progressList.map { enrichProgress(it) }
    }

    fun getProgressById(id: Long): StudentProgressWithDetails? {
        val progress = studentProgressDao.findById(id) ?: return null
        return enrichProgress(progress)
    }

    fun getProgressByStudent(studentId: Long): StudentProgressByLevel? {
        // Get student's current level
        val studentLevel = studentLevelDao.findByStudent(studentId) ?: return null
        
        // Use existing method with current level
        return getProgressByStudentAndLevel(studentId, studentLevel.levelId)
    }

    private fun enrichProgress(progress: StudentProgress): StudentProgressWithDetails {
        val requirement = levelRequirementDao.findById(progress.levelRequirementId)
        val level = requirement?.let { levelDao.findById(it.levelId) }
        val move = requirement?.let { moveDao.findById(it.moveId) }
        val instructor = progress.instructorId?.let { studentDao.findById(it) }
        val instructorName = instructor?.let { "${it.firstName} ${it.lastName}" }

        return StudentProgressWithDetails(
            id = progress.id,
            studentId = progress.studentId,
            levelRequirementId = progress.levelRequirementId,
            level = level,
            move = move,
            status = progress.status,
            completedAt = progress.completedAt,
            instructorId = progress.instructorId,
            instructorName = instructorName,
            attempts = progress.attempts,
            notes = progress.notes,
            createdAt = progress.createdAt,
            updatedAt = progress.updatedAt
        )
    }

    fun createProgress(
        studentId: Long,
        levelRequirementId: Long,
        status: ProgressState,
        instructorId: Long?,
        attempts: Int = 0,
        notes: String?,
        userId: Long? = null
    ): Result<StudentProgress> {
        return try {
            val progress = studentProgressDao.create(studentId, levelRequirementId, status, instructorId, attempts, notes)

            userId?.let {
                auditLogDao.create(
                    userId = it,
                    action = "CREATE",
                    entity = "student_progress",
                    entityId = progress.id,
                    description = "Created progress for student $studentId on requirement $levelRequirementId with status $status",
                    userAgent = null
                )
            }

            Result.success(progress)
        } catch (e: Exception) {
            Result.failure(AppException.DatabaseError("Failed to create progress", e))
        }
    }

    fun updateProgress(
        id: Long,
        status: ProgressState?,
        instructorId: Long?,
        attempts: Int?,
        notes: String?,
        userId: Long? = null
    ): Boolean {
        val result = if (status != null) {
            studentProgressDao.updateStatus(id, status, instructorId, notes)
        } else if (attempts != null) {
            studentProgressDao.updateAttempts(id, attempts, notes)
        } else {
            false
        }

        if (result && userId != null) {
            val action = if (status == ProgressState.PASSED) "MARK_COMPLETED" else "UPDATE_STATUS"
            auditLogDao.create(
                userId = userId,
                action = action,
                entity = "student_progress",
                entityId = id,
                description = "Updated progress: status=$status, attempts=$attempts, instructor=$instructorId",
                userAgent = null
            )
        }

        return result
    }

    fun bulkUpdateProgress(
        progressIds: List<Long>,
        status: ProgressState?,
        instructorId: Long?,
        attempts: Int?,
        notes: String?,
        userId: Long? = null
    ): Int {
        var count = 0
        progressIds.forEach { id ->
            val updated = if (status != null) {
                studentProgressDao.updateStatus(id, status, instructorId, notes)
            } else if (attempts != null) {
                studentProgressDao.updateAttempts(id, attempts, notes)
            } else {
                false
            }
            if (updated) count++
        }

        if (count > 0 && userId != null) {
            auditLogDao.create(
                userId = userId,
                action = "BULK_UPDATE",
                entity = "student_progress",
                entityId = null,
                description = "Bulk updated $count progress records: status=$status, attempts=$attempts",
                userAgent = null
            )
        }

        return count
    }

    fun deleteProgress(id: Long, userId: Long? = null): Boolean {
        val result = studentProgressDao.delete(id)

        if (result && userId != null) {
            auditLogDao.create(
                userId = userId,
                action = "DELETE",
                entity = "student_progress",
                entityId = id,
                description = "Deleted progress record",
                userAgent = null
            )
        }

        return result
    }
}
