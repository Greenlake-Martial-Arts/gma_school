// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.school.database.data.tables

import java.time.LocalDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime

object LevelsTable : LongIdTable("levels") {
    val code: Column<String> = varchar("code", 20).uniqueIndex()
    val displayName: Column<String> = varchar("display_name", 50)
    val orderSeq: Column<Int> = integer("order_seq")
    val description: Column<String?> = text("description").nullable()
    val createdAt: Column<LocalDateTime> = datetime("created_at").default(LocalDateTime.now())
    val updatedAt: Column<LocalDateTime> = datetime("updated_at").default(LocalDateTime.now())
}
