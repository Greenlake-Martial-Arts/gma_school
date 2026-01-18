// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.school.database.data.tables

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object StudentProgressTable : LongIdTable("student_progress") {
    val studentId: Column<Long> = long("student_id").references(StudentsTable.id)
    val levelRequirementId: Column<Long> = long("level_requirement_id").references(LevelRequirementsTable.id)
    val status: Column<String> = varchar("status", 20).default("NOT_STARTED")
    val completedAt: Column<LocalDateTime?> = datetime("completed_at").nullable()
    val instructorId: Column<Long?> = long("instructor_id").references(StudentsTable.id).nullable()
    val attempts: Column<Int> = integer("attempts").default(0)
    val notes: Column<String?> = text("notes").nullable()
    val createdAt: Column<LocalDateTime> = datetime("created_at")
    val updatedAt: Column<LocalDateTime> = datetime("updated_at")

    init {
        uniqueIndex(studentId, levelRequirementId)
    }
}
