// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.StudentLevelsTable
import com.gma.tsunjo.school.domain.models.StudentLevel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class StudentLevelDao {
    fun assign(studentId: Long, levelId: Long): StudentLevel = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        
        // Delete existing assignment first
        StudentLevelsTable.deleteWhere { StudentLevelsTable.studentId eq studentId }
        
        // Insert new assignment
        StudentLevelsTable.insert {
            it[StudentLevelsTable.studentId] = studentId
            it[StudentLevelsTable.levelId] = levelId
            it[StudentLevelsTable.assignedAt] = now
        }

        StudentLevel(studentId, levelId, now)
    }

    fun findByStudent(studentId: Long): StudentLevel? = transaction {
        StudentLevelsTable.select { StudentLevelsTable.studentId eq studentId }
            .singleOrNull()
            ?.let(::toStudentLevel)
    }

    fun findByLevel(levelId: Long): List<StudentLevel> = transaction {
        StudentLevelsTable.select { StudentLevelsTable.levelId eq levelId }
            .map(::toStudentLevel)
    }

    fun delete(studentId: Long): Boolean = transaction {
        StudentLevelsTable.deleteWhere { StudentLevelsTable.studentId eq studentId } > 0
    }

    private fun toStudentLevel(row: ResultRow) = StudentLevel(
        studentId = row[StudentLevelsTable.studentId],
        levelId = row[StudentLevelsTable.levelId],
        assignedAt = row[StudentLevelsTable.assignedAt]
    )
}
