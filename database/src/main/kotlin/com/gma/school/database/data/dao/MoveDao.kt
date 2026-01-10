// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.MoveCategoriesTable
import com.gma.school.database.data.tables.MovesTable
import com.gma.tsunjo.school.domain.models.Move
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class MoveDao {
    companion object {
        private const val DEFAULT_CATEGORY_NAME = "General"
    }

    fun create(name: String, description: String?): Move = transaction {
        val categoryId = getDefaultCategoryId()

        val id = MovesTable.insertAndGetId {
            it[MovesTable.name] = name
            it[MovesTable.description] = description
            it[MovesTable.moveCategoriesId] = categoryId
        }.value

        Move(id, name, description, categoryId)
    }

    fun findById(id: Long): Move? = transaction {
        MovesTable.select { MovesTable.id eq id }
            .singleOrNull()
            ?.let(::toMove)
    }

    fun findAll(): List<Move> = transaction {
        MovesTable.selectAll().map(::toMove)
    }

    fun findByCategory(categoryId: Long): List<Move> = transaction {
        MovesTable.select { MovesTable.moveCategoriesId eq categoryId }
            .map(::toMove)
    }

    fun update(id: Long, name: String, description: String?, moveCategoryId: Long): Boolean = transaction {
        MovesTable.update({ MovesTable.id eq id }) {
            it[MovesTable.name] = name
            it[MovesTable.description] = description
            it[MovesTable.moveCategoriesId] = moveCategoryId
        } > 0
    }

    fun delete(id: Long): Boolean = transaction {
        MovesTable.deleteWhere { MovesTable.id eq id } > 0
    }

    private fun getDefaultCategoryId(): Long = transaction {
        MoveCategoriesTable.select { MoveCategoriesTable.name eq DEFAULT_CATEGORY_NAME }
            .singleOrNull()
            ?.get(MoveCategoriesTable.id)?.value
            ?: MoveCategoriesTable.insertAndGetId {
                it[MoveCategoriesTable.name] = DEFAULT_CATEGORY_NAME
                it[MoveCategoriesTable.description] = "Default category for moves"
            }.value
    }

    private fun toMove(row: ResultRow) = Move(
        id = row[MovesTable.id].value,
        name = row[MovesTable.name],
        description = row[MovesTable.description],
        moveCategoryId = row[MovesTable.moveCategoriesId]
    )
}
