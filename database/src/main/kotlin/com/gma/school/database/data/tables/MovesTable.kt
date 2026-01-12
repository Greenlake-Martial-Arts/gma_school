// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.tables

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object MovesTable : LongIdTable("moves") {
    val name: Column<String> = varchar("name", 100).uniqueIndex()
    val description: Column<String?> = text("description").nullable()
    val moveCategoriesId: Column<Long> = long("move_categories_id").references(MoveCategoriesTable.id)
    val createdAt: Column<LocalDateTime> = datetime("created_at")
    val updatedAt: Column<LocalDateTime> = datetime("updated_at")
}
