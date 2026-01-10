// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.tables

import java.time.LocalDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime

object MovesTable : LongIdTable("moves") {
    val name: Column<String> = varchar("name", 100).uniqueIndex()
    val description: Column<String?> = text("description").nullable()
    val moveCategoriesId: Column<Long> = long("move_categories_id").references(MoveCategoriesTable.id)
    val createdAt: Column<LocalDateTime> = datetime("created_at").default(LocalDateTime.now())
    val updatedAt: Column<LocalDateTime> = datetime("updated_at").default(LocalDateTime.now())
}
