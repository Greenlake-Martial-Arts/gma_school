// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.RoleDao
import com.gma.school.database.data.dao.UserDao
import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.models.Role
import com.gma.tsunjo.school.domain.models.User
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Base64
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserRepositoryTest {

    private val userDao = mockk<UserDao>()
    private val roleDao = mockk<RoleDao>()
    private val userRepository = UserRepository(userDao, roleDao)

    private val testUser = User(
        id = 1L,
        username = "test@example.com",
        isActive = true,
        createdAt = "2025-01-01T00:00:00",
        updatedAt = "2025-01-01T00:00:00"
    )

    @Test
    fun `getAllUsers returns all users from dao`() {
        // Given
        val users = listOf(testUser)
        every { userDao.findAll() } returns users

        // When
        val result = userRepository.getAllUsers()

        // Then
        assertEquals(users, result)
        verify { userDao.findAll() }
    }

    @Test
    fun `getActiveUsers returns only active users`() {
        // Given
        val activeUsers = listOf(testUser)
        every { userDao.findAllActive() } returns activeUsers

        // When
        val result = userRepository.getActiveUsers()

        // Then
        assertEquals(activeUsers, result)
        verify { userDao.findAllActive() }
    }

    @Test
    fun `getUserById returns user when found`() {
        // Given
        every { userDao.findById(1L) } returns testUser

        // When
        val result = userRepository.getUserById(1L)

        // Then
        assertEquals(testUser, result)
        verify { userDao.findById(1L) }
    }

    @Test
    fun `getUserById returns null when not found`() {
        // Given
        every { userDao.findById(999L) } returns null

        // When
        val result = userRepository.getUserById(999L)

        // Then
        assertNull(result)
        verify { userDao.findById(999L) }
    }

    @Test
    fun `getUserByusername returns user when found`() {
        // Given
        every { userDao.findByUsername("test@example.com") } returns testUser

        // When
        val result = userRepository.getUserByUsername("test@example.com")

        // Then
        assertEquals(testUser, result)
        verify { userDao.findByUsername("test@example.com") }
    }

    @Test
    fun `createUser succeeds when username is unique`() {
        // Given
        every { userDao.findByUsername("new@example.com") } returns null
        every {
            userDao.insert(
                "new@example.com",
                Base64.getEncoder().encodeToString("password".toByteArray())
            )
        } returns testUser
        every { roleDao.findByName("STUDENT") } returns Role(4L, "STUDENT")
        every { userDao.assignRole(testUser.id, 4L) } returns true

        // When
        val result = userRepository.createUser("new@example.com", "password")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(testUser, result.getOrNull())
        verify { userDao.findByUsername("new@example.com") }
        verify {
            userDao.insert(
                "new@example.com",
                Base64.getEncoder().encodeToString("password".toByteArray())
            )
        }
        verify { userDao.assignRole(testUser.id, 4L) }
    }

    @Test
    fun `createUser fails when username already exists`() {
        // Given
        every { userDao.findByUsername("existing@example.com") } returns testUser

        // When
        val result = userRepository.createUser("existing@example.com", "password")

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is AppException.UserAlreadyExists)
        verify { userDao.findByUsername("existing@example.com") }
        verify(exactly = 0) { userDao.insert(any(), any()) }
    }

    @Test
    fun `createUser fails when dao insert returns null`() {
        // Given
        every { userDao.findByUsername("test@example.com") } returns null
        every {
            userDao.insert(
                "test@example.com",
                Base64.getEncoder().encodeToString("password".toByteArray())
            )
        } returns null

        // When
        val result = userRepository.createUser("test@example.com", "password")

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is AppException.DatabaseError)
        assertEquals("Failed to create user", exception.message)
    }

    @Test
    fun `createUser handles dao exceptions`() {
        // Given
        every { userDao.findByUsername("test@example.com") } throws RuntimeException("Database error")

        // When
        val result = userRepository.createUser("test@example.com", "password")

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is AppException.DatabaseError)
        assertEquals("Database error during user creation", exception.message)
    }

    @Test
    fun `updateUser returns updated user when successful`() {
        // Given
        val updatedUser = testUser.copy(username = "updated@example.com")
        every { userDao.update(1L, "updated@example.com", null) } returns updatedUser

        // When
        val result = userRepository.updateUser(1L, username = "updated@example.com")

        // Then
        assertEquals(updatedUser, result)
        verify { userDao.update(1L, "updated@example.com", null) }
    }

    @Test
    fun `updateUser returns null when user not found`() {
        // Given
        every { userDao.update(999L, "updated@example.com", null) } returns null

        // When
        val result = userRepository.updateUser(999L, username = "updated@example.com")

        // Then
        assertNull(result)
        verify { userDao.update(999L, "updated@example.com", null) }
    }

    @Test
    fun `setUserActiveStatus returns true when successful`() {
        // Given
        val deactivatedUser = testUser.copy(isActive = false)
        every { userDao.update(1L, isActive = false) } returns deactivatedUser

        // When
        val result = userRepository.setUserActiveStatus(1L, false)

        // Then
        assertTrue(result)
        verify { userDao.update(1L, isActive = false) }
    }

    @Test
    fun `setUserActiveStatus returns false when user not found`() {
        // Given
        every { userDao.update(999L, isActive = false) } returns null

        // When
        val result = userRepository.setUserActiveStatus(999L, false)

        // Then
        assertFalse(result)
        verify { userDao.update(999L, isActive = false) }
    }

    @Test
    fun `addUserRole calls dao addUserRole`() {
        // Given
        every { userDao.addUserRole(1L, 2L) } returns true

        // When
        val result = userRepository.addUserRole(1L, 2L)

        // Then
        assertTrue(result)
        verify { userDao.addUserRole(1L, 2L) }
    }

    @Test
    fun `removeUserRole calls dao removeUserRole`() {
        // Given
        every { userDao.removeUserRole(1L, 2L) } returns true

        // When
        val result = userRepository.removeUserRole(1L, 2L)

        // Then
        assertTrue(result)
        verify { userDao.removeUserRole(1L, 2L) }
    }

    @Test
    fun `updateUserWithRoles updates user and replaces roles`() {
        // Given
        val updatedUser = testUser.copy(username = "new@example.com")
        every { userDao.update(1L, "new@example.com", true) } returns updatedUser
        every { userDao.replaceUserRoles(1L, listOf(2L, 3L)) } returns true

        // When
        val result = userRepository.updateUserWithRoles(
            id = 1L,
            username = "new@example.com",
            isActive = true,
            roleIds = listOf(2L, 3L)
        )

        // Then
        assertEquals(updatedUser, result)
        verify { userDao.update(1L, "new@example.com", true) }
        verify { userDao.replaceUserRoles(1L, listOf(2L, 3L)) }
    }

    @Test
    fun `updateUserWithRoles without roleIds only updates user`() {
        // Given
        val updatedUser = testUser.copy(username = "updated@example.com")
        every { userDao.update(1L, "updated@example.com", null) } returns updatedUser

        // When
        val result = userRepository.updateUserWithRoles(
            id = 1L,
            username = "updated@example.com"
        )

        // Then
        assertEquals(updatedUser, result)
        verify { userDao.update(1L, "updated@example.com", null) }
        verify(exactly = 0) { userDao.replaceUserRoles(any(), any()) }
    }

    @Test
    fun `updateUserWithRoles returns null on exception`() {
        // Given
        every { userDao.update(1L, "updated@example.com", null) } throws RuntimeException("Database error")

        // When
        val result = userRepository.updateUserWithRoles(
            id = 1L,
            username = "updated@example.com"
        )

        // Then
        assertNull(result)
    }

    @Test
    fun `createUser uses default STUDENT role when roleId not provided`() {
        // Given
        every { userDao.findByUsername("student@example.com") } returns null
        every {
            userDao.insert(
                "student@example.com",
                Base64.getEncoder().encodeToString("password".toByteArray())
            )
        } returns testUser
        every { roleDao.findByName("STUDENT") } returns Role(4L, "STUDENT")
        every { userDao.assignRole(testUser.id, 4L) } returns true

        // When
        val result = userRepository.createUser("student@example.com", "password", 4L)

        // Then
        assertTrue(result.isSuccess)
        verify { userDao.assignRole(testUser.id, 4L) }
    }

    @Test
    fun `createUser throws exception when STUDENT role not found`() {
        // Given
        every { userDao.findByUsername("student@example.com") } returns null
        every {
            userDao.insert(
                "student@example.com",
                Base64.getEncoder().encodeToString("password".toByteArray())
            )
        } returns testUser
        every { roleDao.findByName("STUDENT") } returns null

        // When
        val result = userRepository.createUser("student@example.com", "password")

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is AppException.RoleNotFound)
        assertEquals("Role 'STUDENT' not found", exception.message)
    }
}
