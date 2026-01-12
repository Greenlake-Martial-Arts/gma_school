// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.school.database.data.tables

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object AuditLogTable : LongIdTable("audit_log") {
    val userId: Column<Long> = long("user_id").references(UsersTable.id)
    val action: Column<String> = varchar("action", 50)
    val entity: Column<String> = varchar("entity", 50)
    val entityId: Column<Long?> = long("entity_id").nullable()
    val description: Column<String?> = text("description").nullable()
    val userAgent: Column<String?> = varchar("user_agent", 255).nullable()
    val createdAt: Column<LocalDateTime> = datetime("created_at")
}
