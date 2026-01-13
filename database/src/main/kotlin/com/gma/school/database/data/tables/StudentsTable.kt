// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.school.database.data.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object StudentsTable : LongIdTable("students") {
    val userId = reference("user_id", UsersTable).uniqueIndex()
    val externalCode = varchar("external_code", 30).nullable().uniqueIndex()
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val email = varchar("email", 255).uniqueIndex()
    val phone = varchar("phone", 30).nullable()
    val address = text("address").nullable()
    val memberTypeId = reference("member_type_id", MemberTypesTable)
    val signupDate = date("signup_date").nullable()
    val isActive = bool("is_active").default(true)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
