// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.AuditLogTable
import com.gma.tsunjo.school.domain.models.AuditLog
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class AuditLogDao {
    fun create(
        userId: Long,
        action: String,
        entity: String,
        entityId: Long?,
        description: String?,
        userAgent: String?
    ): AuditLog = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val id = AuditLogTable.insertAndGetId {
            it[AuditLogTable.userId] = userId
            it[AuditLogTable.action] = action
            it[AuditLogTable.entity] = entity
            it[AuditLogTable.entityId] = entityId
            it[AuditLogTable.description] = description
            it[AuditLogTable.userAgent] = userAgent
            it[AuditLogTable.createdAt] = now
        }.value

        AuditLog(id, userId, action, entity, entityId, description, userAgent, now)
    }

    fun findById(id: Long): AuditLog? = transaction {
        AuditLogTable.select { AuditLogTable.id eq id }
            .singleOrNull()
            ?.let(::toAuditLog)
    }

    fun findByUser(userId: Long, limit: Int = 100): List<AuditLog> = transaction {
        AuditLogTable.select { AuditLogTable.userId eq userId }.map(::toAuditLog)
    }

    fun findByEntity(entity: String, entityId: Long?, limit: Int = 100): List<AuditLog> = transaction {
        val baseCondition = AuditLogTable.entity eq entity
        val finalCondition = if (entityId != null) {
            baseCondition and (AuditLogTable.entityId eq entityId)
        } else baseCondition

        AuditLogTable.select { finalCondition }.map(::toAuditLog)
    }

    private fun toAuditLog(row: ResultRow) = AuditLog(
        id = row[AuditLogTable.id].value,
        userId = row[AuditLogTable.userId],
        action = row[AuditLogTable.action],
        entity = row[AuditLogTable.entity],
        entityId = row[AuditLogTable.entityId],
        description = row[AuditLogTable.description],
        userAgent = row[AuditLogTable.userAgent],
        createdAt = row[AuditLogTable.createdAt]
    )
}
