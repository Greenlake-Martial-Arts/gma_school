// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.RolesTable
import com.gma.tsunjo.school.domain.models.Role
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class RoleDao {

    fun findAll(): List<Role> = transaction {
        RolesTable.selectAll().map { rowToRole(it) }
    }

    fun findById(id: Long): Role? = transaction {
        RolesTable.select { RolesTable.id eq id }
            .map { rowToRole(it) }
            .singleOrNull()
    }

    fun findByName(name: String): Role? = transaction {
        RolesTable.select { RolesTable.name eq name }
            .map { rowToRole(it) }
            .singleOrNull()
    }

    fun insert(name: String): Role? = transaction {
        val insertStatement = RolesTable.insert {
            it[RolesTable.name] = name
        }

        val id = insertStatement[RolesTable.id].value
        findById(id)
    }

    private fun rowToRole(row: ResultRow): Role = Role(
        id = row[RolesTable.id].value,
        name = row[RolesTable.name]
    )
}
