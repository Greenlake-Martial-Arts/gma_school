// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.UserDao
import com.gma.tsunjo.school.domain.models.User

class UserRepository(private val userDao: UserDao) {

    fun getAllUsers(): List<User> = userDao.findAll()

    fun getActiveUsers(): List<User> = userDao.findAllActive()

    fun getUserById(id: Long): User? = userDao.findById(id)

    fun getUserByEmail(email: String): User? = userDao.findByEmail(email)

    fun createUser(email: String, password: String, fullName: String? = null): Result<User> {
        return try {
            // Check if user already exists
            if (userDao.findByEmail(email) != null) {
                return Result.failure(Exception("User with email $email already exists"))
            }

            // Simple password hashing (use proper hashing in production)
            val passwordHash = password.hashCode().toString()

            val user = userDao.insert(email, passwordHash, fullName)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun updateUser(id: Long, email: String? = null, fullName: String? = null, isActive: Boolean? = null): User? {
        return userDao.update(id, email, fullName, isActive)
    }

    fun deactivateUser(id: Long): Boolean {
        val updated = userDao.update(id, isActive = false)
        return updated != null
    }

    fun authenticateUser(email: String, password: String): User? {
        return userDao.authenticate(email, password)
    }
}
