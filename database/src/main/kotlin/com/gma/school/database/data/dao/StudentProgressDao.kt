// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.StudentProgressTable
import com.gma.tsunjo.school.domain.models.StudentProgress
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class StudentProgressDao {
    fun create(
        studentId: Long,
        levelRequirementId: Long,
        instructorId: Long?,
        attempts: Int,
        notes: String?
    ): StudentProgress = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val id = StudentProgressTable.insertAndGetId {
            it[StudentProgressTable.studentId] = studentId
            it[StudentProgressTable.levelRequirementId] = levelRequirementId
            it[StudentProgressTable.instructorId] = instructorId
            it[StudentProgressTable.attempts] = attempts
            it[StudentProgressTable.notes] = notes
            it[StudentProgressTable.createdAt] = now
            it[StudentProgressTable.updatedAt] = now
        }.value

        StudentProgress(id, studentId, levelRequirementId, null, instructorId, attempts, notes, now, now)
    }

    fun findById(id: Long): StudentProgress? = transaction {
        StudentProgressTable.select { StudentProgressTable.id eq id }
            .singleOrNull()
            ?.let(::toStudentProgress)
    }

    fun findByStudent(studentId: Long): List<StudentProgress> = transaction {
        StudentProgressTable.select { StudentProgressTable.studentId eq studentId }
            .map(::toStudentProgress)
    }

    fun markCompleted(id: Long, instructorId: Long): Boolean = transaction {
        StudentProgressTable.update({ StudentProgressTable.id eq id }) {
            it[StudentProgressTable.completedAt] = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            it[StudentProgressTable.instructorId] = instructorId
        } > 0
    }

    fun updateAttempts(id: Long, attempts: Int, notes: String?): Boolean = transaction {
        StudentProgressTable.update({ StudentProgressTable.id eq id }) {
            it[StudentProgressTable.attempts] = attempts
            it[StudentProgressTable.notes] = notes
        } > 0
    }

    fun delete(id: Long): Boolean = transaction {
        StudentProgressTable.deleteWhere { StudentProgressTable.id eq id } > 0
    }

    private fun toStudentProgress(row: ResultRow) = StudentProgress(
        id = row[StudentProgressTable.id].value,
        studentId = row[StudentProgressTable.studentId],
        levelRequirementId = row[StudentProgressTable.levelRequirementId],
        completedAt = row[StudentProgressTable.completedAt],
        instructorId = row[StudentProgressTable.instructorId],
        attempts = row[StudentProgressTable.attempts],
        notes = row[StudentProgressTable.notes],
        createdAt = row[StudentProgressTable.createdAt],
        updatedAt = row[StudentProgressTable.updatedAt]
    )
}
