// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.RoleDao
import com.gma.school.database.data.dao.UserDao
import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.models.User
import java.util.Base64

class UserRepository(private val userDao: UserDao, private val roleDao: RoleDao) {

    private val defaultStudentRoleId by lazy {
        roleDao.findByName("STUDENT")?.id
            ?: throw AppException.RoleNotFound("STUDENT")
    }

    fun getAllUsers(): List<User> = userDao.findAll()

    fun getActiveUsers(): List<User> = userDao.findAllActive()

    fun getUserById(id: Long): User? = userDao.findById(id)

    fun getUserByEmail(email: String): User? = userDao.findByEmail(email)

    fun createUser(email: String, password: String, fullName: String? = null, roleId: Long? = null): Result<User> {
        return try {
            // Check if user already exists
            if (userDao.findByEmail(email) != null) {
                return Result.failure(AppException.UserAlreadyExists(email))
            }

            // Simple password encodeBase64 (use proper salt in production)
            val passwordHash = Base64.getEncoder().encodeToString(password.toByteArray())

            val user = userDao.insert(email, passwordHash, fullName)
            if (user != null) {
                // Assign role - default to STUDENT if not specified
                val finalRoleId = roleId ?: defaultStudentRoleId
                userDao.assignRole(user.id, finalRoleId)
                Result.success(user)
            } else {
                Result.failure(AppException.DatabaseError("Failed to create user"))
            }
        } catch (e: AppException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(AppException.DatabaseError("Database error during user creation", e))
        }
    }

    fun updateUser(id: Long, email: String? = null, fullName: String? = null, isActive: Boolean? = null): User? {
        return userDao.update(id, email, fullName, isActive)
    }

    fun updateUserWithRoles(
        id: Long,
        email: String? = null,
        fullName: String? = null,
        isActive: Boolean? = null,
        roleIds: List<Long>? = null
    ): User? {
        return try {
            val user = userDao.update(id, email, fullName, isActive)
            roleIds?.let { newRoleIds ->
                userDao.replaceUserRoles(id, newRoleIds)
            }
            user
        } catch (e: Exception) {
            null
        }
    }

    fun addUserRole(userId: Long, roleId: Long): Boolean {
        return userDao.addUserRole(userId, roleId)
    }

    fun removeUserRole(userId: Long, roleId: Long): Boolean {
        return userDao.removeUserRole(userId, roleId)
    }

    fun setUserActiveStatus(id: Long, isActive: Boolean): Boolean {
        val updated = userDao.update(id, isActive = isActive)
        return updated != null
    }

    fun authenticateUser(email: String, password: String): User? {
        return userDao.authenticate(email, password)
    }
}
