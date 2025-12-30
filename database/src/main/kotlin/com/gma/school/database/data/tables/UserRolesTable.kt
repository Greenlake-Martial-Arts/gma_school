// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.tables

import org.jetbrains.exposed.sql.Table

object UserRolesTable : Table("user_roles") {
    val userId = reference("user_id", UsersTable)
    val roleId = reference("role_id", RolesTable)

    override val primaryKey = PrimaryKey(userId, roleId)
}
