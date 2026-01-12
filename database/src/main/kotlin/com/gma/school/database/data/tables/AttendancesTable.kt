// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.tables

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object AttendancesTable : LongIdTable("attendances") {
    val classDate: Column<LocalDate> = date("class_date")
    val notes: Column<String?> = text("notes").nullable()
    val createdAt: Column<LocalDateTime> = datetime("created_at")
}
