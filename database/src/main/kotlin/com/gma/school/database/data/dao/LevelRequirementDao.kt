// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.LevelRequirementsTable
import com.gma.school.database.data.tables.LevelsTable
import com.gma.school.database.data.tables.MovesTable
import com.gma.tsunjo.school.domain.models.Level
import com.gma.tsunjo.school.domain.models.LevelRequirement
import com.gma.tsunjo.school.domain.models.LevelRequirementWithDetails
import com.gma.tsunjo.school.domain.models.Move
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

class LevelRequirementDao {
    fun create(
        levelId: Long,
        moveId: Long,
        sortOrder: Int,
        levelSpecificNotes: String?,
        isRequired: Boolean
    ): LevelRequirement = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val id = LevelRequirementsTable.insertAndGetId {
            it[LevelRequirementsTable.levelId] = levelId
            it[LevelRequirementsTable.moveId] = moveId
            it[LevelRequirementsTable.sortOrder] = sortOrder
            it[LevelRequirementsTable.levelSpecificNotes] = levelSpecificNotes
            it[LevelRequirementsTable.isRequired] = isRequired
            it[LevelRequirementsTable.createdAt] = now
            it[LevelRequirementsTable.updatedAt] = now
        }.value

        LevelRequirement(id, levelId, moveId, sortOrder, levelSpecificNotes, isRequired, now, now)
    }

    fun findById(id: Long): LevelRequirement? = transaction {
        LevelRequirementsTable.select { LevelRequirementsTable.id eq id }
            .singleOrNull()
            ?.let(::toLevelRequirement)
    }

    fun findByIdWithDetails(id: Long): LevelRequirementWithDetails? = transaction {
        (LevelRequirementsTable innerJoin LevelsTable innerJoin MovesTable)
            .select { LevelRequirementsTable.id eq id }
            .singleOrNull()
            ?.let(::toLevelRequirementWithDetails)
    }

    fun findByLevel(levelId: Long): List<LevelRequirement> = transaction {
        LevelRequirementsTable.select { LevelRequirementsTable.levelId eq levelId }
            .map(::toLevelRequirement)
    }

    fun findByLevelWithDetails(levelId: Long): List<LevelRequirementWithDetails> = transaction {
        (LevelRequirementsTable innerJoin LevelsTable innerJoin MovesTable)
            .select { LevelRequirementsTable.levelId eq levelId }
            .map(::toLevelRequirementWithDetails)
    }

    fun update(id: Long, sortOrder: Int, levelSpecificNotes: String?, isRequired: Boolean): Boolean = transaction {
        LevelRequirementsTable.update({ LevelRequirementsTable.id eq id }) {
            it[LevelRequirementsTable.sortOrder] = sortOrder
            it[LevelRequirementsTable.levelSpecificNotes] = levelSpecificNotes
            it[LevelRequirementsTable.isRequired] = isRequired
        } > 0
    }

    fun delete(id: Long): Boolean = transaction {
        LevelRequirementsTable.deleteWhere { LevelRequirementsTable.id eq id } > 0
    }

    private fun toLevelRequirement(row: ResultRow) = LevelRequirement(
        id = row[LevelRequirementsTable.id].value,
        levelId = row[LevelRequirementsTable.levelId],
        moveId = row[LevelRequirementsTable.moveId],
        sortOrder = row[LevelRequirementsTable.sortOrder],
        levelSpecificNotes = row[LevelRequirementsTable.levelSpecificNotes],
        isRequired = row[LevelRequirementsTable.isRequired],
        createdAt = row[LevelRequirementsTable.createdAt],
        updatedAt = row[LevelRequirementsTable.updatedAt]
    )

    private fun toLevelRequirementWithDetails(row: ResultRow) = LevelRequirementWithDetails(
        id = row[LevelRequirementsTable.id].value,
        level = Level(
            id = row[LevelsTable.id].value,
            code = row[LevelsTable.code],
            displayName = row[LevelsTable.displayName],
            orderSeq = row[LevelsTable.orderSeq],
            description = row[LevelsTable.description],
            createdAt = row[LevelsTable.createdAt],
            updatedAt = row[LevelsTable.updatedAt]
        ),
        move = Move(
            id = row[MovesTable.id].value,
            name = row[MovesTable.name],
            description = row[MovesTable.description],
            moveCategoryId = row[MovesTable.moveCategoriesId],
            createdAt = row[MovesTable.createdAt],
            updatedAt = row[MovesTable.updatedAt]
        ),
        sortOrder = row[LevelRequirementsTable.sortOrder],
        levelSpecificNotes = row[LevelRequirementsTable.levelSpecificNotes],
        isRequired = row[LevelRequirementsTable.isRequired],
        createdAt = row[LevelRequirementsTable.createdAt],
        updatedAt = row[LevelRequirementsTable.updatedAt]
    )
}
