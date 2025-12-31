// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.UserRolesTable
import com.gma.school.database.data.tables.UsersTable
import com.gma.tsunjo.school.domain.models.User
import java.time.LocalDateTime
import java.util.Base64
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class UserDao {

    fun findAll(): List<User> = transaction {
        UsersTable.selectAll().map { rowToUser(it) }
    }

    fun findAllActive(): List<User> = transaction {
        UsersTable.select { UsersTable.isActive eq true }.map { rowToUser(it) }
    }

    fun findById(id: Long): User? = transaction {
        UsersTable.select { UsersTable.id eq id }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    fun findByEmail(email: String): User? = transaction {
        UsersTable.select { UsersTable.email eq email }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    fun insert(email: String, passwordHash: String, fullName: String? = null): User? = transaction {
        val insertStatement = UsersTable.insert {
            it[UsersTable.email] = email
            it[UsersTable.passwordHash] = passwordHash
            it[UsersTable.fullName] = fullName
            it[createdAt] = LocalDateTime.now()
            it[updatedAt] = LocalDateTime.now()
        }

        val id = insertStatement[UsersTable.id].value
        findById(id)
    }

    fun update(id: Long, email: String? = null, fullName: String? = null, isActive: Boolean? = null): User? =
        transaction {
            val updateCount = UsersTable.update({ UsersTable.id eq id }) {
                email?.let { e -> it[UsersTable.email] = e }
                fullName?.let { fn -> it[UsersTable.fullName] = fn }
                isActive?.let { active -> it[UsersTable.isActive] = active }
                it[updatedAt] = LocalDateTime.now()
            }

            if (updateCount > 0) findById(id) else null
        }

    fun authenticate(email: String, password: String): User? = transaction {
        UsersTable.select {
            (UsersTable.email eq email) and (UsersTable.isActive eq true)
        }
            .map { rowToUser(it) }
            .singleOrNull()
            ?.takeIf {
                // TODO In a real app, you'd verify the hashed password here
                // For now, we'll assume password verification logic exists
                verifyPassword(password, it.id)
            }
    }

    private fun verifyPassword(password: String, userId: Long): Boolean = transaction {
        val storedHash = UsersTable.select { UsersTable.id eq userId }
            .map { it[UsersTable.passwordHash] }
            .singleOrNull()

        // Compare base64 encoded password with stored hash
        storedHash == Base64.getEncoder().encodeToString(password.toByteArray())
    }

    private fun rowToUser(row: ResultRow): User = User(
        id = row[UsersTable.id].value,
        email = row[UsersTable.email],
        fullName = row[UsersTable.fullName],
        studentId = row[UsersTable.studentId]?.value,
        isActive = row[UsersTable.isActive],
        createdAt = row[UsersTable.createdAt].toString(),
        updatedAt = row[UsersTable.updatedAt].toString()
    )

    fun assignRole(userId: Long, roleId: Long): Boolean = transaction {
        try {
            UserRolesTable.insert {
                it[UserRolesTable.userId] = userId
                it[UserRolesTable.roleId] = roleId
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun addUserRole(userId: Long, roleId: Long): Boolean = transaction {
        try {
            UserRolesTable.insert {
                it[UserRolesTable.userId] = userId
                it[UserRolesTable.roleId] = roleId
            }
            true
        } catch (e: Exception) {
            // Role already exists for user - that's ok
            false
        }
    }

    fun removeUserRole(userId: Long, roleId: Long): Boolean = transaction {
        try {
            UserRolesTable.deleteWhere {
                (UserRolesTable.userId eq userId) and (UserRolesTable.roleId eq roleId)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun replaceUserRoles(userId: Long, roleIds: List<Long>): Boolean = transaction {
        try {
            // Remove all existing roles
            UserRolesTable.deleteWhere { UserRolesTable.userId eq userId }
            // Add new roles
            roleIds.forEach { roleId ->
                UserRolesTable.insert {
                    it[UserRolesTable.userId] = userId
                    it[UserRolesTable.roleId] = roleId
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
