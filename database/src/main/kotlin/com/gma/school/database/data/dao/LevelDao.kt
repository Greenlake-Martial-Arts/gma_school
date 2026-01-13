// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.LevelsTable
import com.gma.tsunjo.school.domain.models.Level
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class LevelDao {
    fun create(code: String, displayName: String, orderSeq: Int, description: String?): Level = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val id = LevelsTable.insertAndGetId {
            it[LevelsTable.code] = code
            it[LevelsTable.displayName] = displayName
            it[LevelsTable.orderSeq] = orderSeq
            it[LevelsTable.description] = description
            it[LevelsTable.createdAt] = now
            it[LevelsTable.updatedAt] = now
        }.value

        Level(id, code, displayName, orderSeq, description, now, now)
    }

    fun findById(id: Long): Level? = transaction {
        LevelsTable.select { LevelsTable.id eq id }
            .singleOrNull()
            ?.let(::toLevel)
    }

    fun findAll(): List<Level> = transaction {
        LevelsTable.selectAll().map(::toLevel)
    }

    fun update(id: Long, code: String, displayName: String, orderSeq: Int, description: String?): Boolean =
        transaction {
            LevelsTable.update({ LevelsTable.id eq id }) {
                it[LevelsTable.code] = code
                it[LevelsTable.displayName] = displayName
                it[LevelsTable.orderSeq] = orderSeq
                it[LevelsTable.description] = description
            } > 0
        }

    fun delete(id: Long): Boolean = transaction {
        LevelsTable.deleteWhere { LevelsTable.id eq id } > 0
    }

    private fun toLevel(row: ResultRow) = Level(
        id = row[LevelsTable.id].value,
        code = row[LevelsTable.code],
        displayName = row[LevelsTable.displayName],
        orderSeq = row[LevelsTable.orderSeq],
        description = row[LevelsTable.description],
        createdAt = row[LevelsTable.createdAt],
        updatedAt = row[LevelsTable.updatedAt]
    )
}
