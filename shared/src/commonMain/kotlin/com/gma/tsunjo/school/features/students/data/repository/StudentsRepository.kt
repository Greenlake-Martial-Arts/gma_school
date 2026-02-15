// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.students.data.repository

import com.gma.tsunjo.school.auth.AuthenticationHandler
import com.gma.tsunjo.school.data.remote.HttpErrorMapper
import com.gma.tsunjo.school.features.students.data.remote.StudentsApi
import com.gma.tsunjo.school.features.students.domain.model.RequirementItem
import com.gma.tsunjo.school.features.students.domain.model.Student
import com.gma.tsunjo.school.features.students.domain.model.StudentProgressDetail
import com.gma.tsunjo.school.features.students.domain.model.StudentWithLevel

class StudentsRepository(
    private val studentsApi: StudentsApi,
    private val authHandler: AuthenticationHandler
) {
    suspend fun getStudents(): Result<List<Student>> {
        return try {
            val students = studentsApi.getStudents()
            Result.success(students)
        } catch (e: Exception) {
            authHandler.handleError(e)
            Result.failure(HttpErrorMapper.mapException(e))
        }
    }

    suspend fun getActiveStudents(): Result<List<StudentWithLevel>> {
        return try {
            val response = studentsApi.getActiveStudents()
            val students = response.map { item ->
                StudentWithLevel(
                    id = item.student.id,
                    userId = item.student.userId,
                    firstName = item.student.firstName,
                    lastName = item.student.lastName,
                    fullName = "${item.student.firstName} ${item.student.lastName}",
                    isActive = item.student.isActive,
                    currentLevel = item.currentLevel.displayName,
                    code = item.currentLevel.code
                )
            }
            Result.success(students)
        } catch (e: Exception) {
            authHandler.handleError(e)
            Result.failure(HttpErrorMapper.mapException(e))
        }
    }

    suspend fun getStudentProgress(studentId: Long, studentName: String): Result<StudentProgressDetail> {
        return try {
            val response = studentsApi.getStudentProgress(studentId)
            val detail = StudentProgressDetail(
                studentId = studentId,
                studentName = studentName,
                levelName = response.level.displayName,
                requirements = response.requirements.map { req ->
                    RequirementItem(
                        levelRequirementId = req.id,
                        moveId = req.move.id,
                        moveName = req.move.name,
                        progressId = req.progress.id,
                        status = req.progress.status,
                        notes = req.progress.notes
                    )
                }
            )
            Result.success(detail)
        } catch (e: Exception) {
            authHandler.handleError(e)
            Result.failure(HttpErrorMapper.mapException(e))
        }
    }

    suspend fun createStudentProgress(
        studentId: Long,
        levelRequirementId: Long,
        status: com.gma.tsunjo.school.domain.models.ProgressState,
        instructorId: Long? = null,
        attempts: Int = 0,
        notes: String? = null
    ): Result<com.gma.tsunjo.school.domain.models.StudentProgress> {
        return try {
            val request = com.gma.tsunjo.school.api.requests.CreateStudentProgressRequest(
                studentId = studentId,
                levelRequirementId = levelRequirementId,
                status = status,
                instructorId = instructorId,
                attempts = attempts,
                notes = notes
            )
            val progress = studentsApi.createStudentProgress(request)
            Result.success(progress)
        } catch (e: Exception) {
            authHandler.handleError(e)
            Result.failure(HttpErrorMapper.mapException(e))
        }
    }

    suspend fun updateStudentProgress(
        progressId: Long,
        status: com.gma.tsunjo.school.domain.models.ProgressState,
        instructorId: Long? = null,
        attempts: Int? = null,
        notes: String? = null
    ): Result<Unit> {
        return try {
            val request = com.gma.tsunjo.school.api.requests.UpdateStudentProgressRequest(
                status = status,
                instructorId = instructorId,
                attempts = attempts,
                notes = notes
            )
            studentsApi.updateStudentProgress(progressId, request)
            Result.success(Unit)
        } catch (e: Exception) {
            authHandler.handleError(e)
            Result.failure(HttpErrorMapper.mapException(e))
        }
    }
}
