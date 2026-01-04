// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.MemberTypesTable
import com.gma.tsunjo.school.domain.models.MemberType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class MemberTypeDao {

    fun findAll(): List<MemberType> = transaction {
        MemberTypesTable.selectAll().map { rowToMemberType(it) }
    }

    fun findById(id: Long): MemberType? = transaction {
        MemberTypesTable.select { MemberTypesTable.id eq id }
            .map { rowToMemberType(it) }
            .singleOrNull()
    }

    fun findByName(name: String): MemberType? = transaction {
        MemberTypesTable.select { MemberTypesTable.name eq name }
            .map { rowToMemberType(it) }
            .singleOrNull()
    }

    private fun rowToMemberType(row: ResultRow): MemberType = MemberType(
        id = row[MemberTypesTable.id].value,
        name = row[MemberTypesTable.name]
    )
}
