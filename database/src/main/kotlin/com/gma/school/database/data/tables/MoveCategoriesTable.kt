// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object MoveCategoriesTable : LongIdTable("move_categories") {
    val name: Column<String> = varchar("name", 50).uniqueIndex()
    val description: Column<String?> = text("description").nullable()
}
