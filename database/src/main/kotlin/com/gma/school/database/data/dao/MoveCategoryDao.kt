// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.MoveCategoriesTable
import com.gma.tsunjo.school.domain.models.MoveCategory
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class MoveCategoryDao {
    fun create(name: String, description: String?): MoveCategory = transaction {
        val id = MoveCategoriesTable.insertAndGetId {
            it[MoveCategoriesTable.name] = name
            it[MoveCategoriesTable.description] = description
        }.value

        MoveCategory(id, name, description)
    }

    fun findById(id: Long): MoveCategory? = transaction {
        MoveCategoriesTable.select { MoveCategoriesTable.id eq id }
            .singleOrNull()
            ?.let(::toMoveCategory)
    }

    fun findAll(): List<MoveCategory> = transaction {
        MoveCategoriesTable.selectAll().map(::toMoveCategory)
    }

    fun update(id: Long, name: String, description: String?): Boolean = transaction {
        MoveCategoriesTable.update({ MoveCategoriesTable.id eq id }) {
            it[MoveCategoriesTable.name] = name
            it[MoveCategoriesTable.description] = description
        } > 0
    }

    fun delete(id: Long): Boolean = transaction {
        MoveCategoriesTable.deleteWhere { MoveCategoriesTable.id eq id } > 0
    }

    private fun toMoveCategory(row: ResultRow) = MoveCategory(
        id = row[MoveCategoriesTable.id].value,
        name = row[MoveCategoriesTable.name],
        description = row[MoveCategoriesTable.description]
    )
}
