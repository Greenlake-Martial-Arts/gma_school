// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object RolesTable : LongIdTable("roles") {
    val name = varchar("name", 30).uniqueIndex()
}
