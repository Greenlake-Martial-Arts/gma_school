// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.school.database.data.tables

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object StudentLevelsTable : Table("student_levels") {
    val studentId: Column<Long> = long("student_id").references(StudentsTable.id)
    val levelId: Column<Long> = long("level_id").references(LevelsTable.id)
    val assignedAt: Column<LocalDateTime> = datetime("assigned_at")

    override val primaryKey = PrimaryKey(studentId)
}
