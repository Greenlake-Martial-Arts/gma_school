// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.school.database.data.tables

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object LevelRequirementsTable : LongIdTable("level_requirements") {
    val levelId: Column<Long> = long("level_id").references(LevelsTable.id)
    val moveId: Column<Long> = long("move_id").references(MovesTable.id)
    val sortOrder: Column<Int> = integer("sort_order")
    val levelSpecificNotes: Column<String?> = text("level_specific_notes").nullable()
    val isRequired: Column<Boolean> = bool("is_required").default(true)
    val createdAt: Column<LocalDateTime> = datetime("created_at")
    val updatedAt: Column<LocalDateTime> = datetime("updated_at")

    init {
        uniqueIndex(levelId, moveId)
    }
}
