// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.RolesTable
import com.gma.school.database.data.tables.UserRolesTable
import com.gma.school.database.data.tables.UsersTable
import java.util.Base64
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserDaoTest {

    private lateinit var database: Database
    private lateinit var userDao: UserDao
    private lateinit var roleDao: RoleDao

    @Before
    fun setup() {
        // Setup H2 in-memory database for testing
        database = Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver"
        )

        userDao = UserDao()
        roleDao = RoleDao()

        // Create tables
        transaction(database) {
            SchemaUtils.create(UsersTable, RolesTable, UserRolesTable)
        }
    }

    @After
    fun teardown() {
        transaction(database) {
            SchemaUtils.drop(UserRolesTable, RolesTable, UsersTable)
        }
    }

    @Test
    fun `insert creates user successfully`() {
        // When
        val user = userDao.insert("test@example.com", "hashedpassword", "Test User")

        // Then
        assertNotNull(user)
        assertEquals("test@example.com", user.email)
        assertEquals("Test User", user.fullName)
        assertTrue(user.isActive)
        assertTrue(user.id > 0)
    }

    @Test
    fun `insert creates user with minimal data`() {
        // When
        val user = userDao.insert("minimal@example.com", "hashedpassword")

        // Then
        assertNotNull(user)
        assertEquals("minimal@example.com", user.email)
        assertNull(user.fullName)
        assertTrue(user.isActive)
    }

    @Test
    fun `findById returns user when exists`() {
        // Given
        val createdUser = userDao.insert("test@example.com", "hashedpassword", "Test User")!!

        // When
        val foundUser = userDao.findById(createdUser.id)

        // Then
        assertNotNull(foundUser)
        assertEquals(createdUser.id, foundUser.id)
        assertEquals(createdUser.email, foundUser.email)
        assertEquals(createdUser.fullName, foundUser.fullName)
    }

    @Test
    fun `findById returns null when user does not exist`() {
        // When
        val user = userDao.findById(999L)

        // Then
        assertNull(user)
    }

    @Test
    fun `findByEmail returns user when exists`() {
        // Given
        val createdUser = userDao.insert("test@example.com", "hashedpassword", "Test User")!!

        // When
        val foundUser = userDao.findByEmail("test@example.com")

        // Then
        assertNotNull(foundUser)
        assertEquals(createdUser.id, foundUser.id)
        assertEquals("test@example.com", foundUser.email)
    }

    @Test
    fun `findByEmail returns null when user does not exist`() {
        // When
        val user = userDao.findByEmail("nonexistent@example.com")

        // Then
        assertNull(user)
    }

    @Test
    fun `findAll returns all users`() {
        // Given
        userDao.insert("user1@example.com", "hash1", "User 1")
        userDao.insert("user2@example.com", "hash2", "User 2")

        // When
        val users = userDao.findAll()

        // Then
        assertEquals(2, users.size)
        assertTrue(users.any { it.email == "user1@example.com" })
        assertTrue(users.any { it.email == "user2@example.com" })
    }

    @Test
    fun `findAllActive returns only active users`() {
        // Given
        val user1 = userDao.insert("active@example.com", "hash1", "Active User")!!
        val user2 = userDao.insert("inactive@example.com", "hash2", "Inactive User")!!

        // Deactivate user2
        userDao.update(user2.id, isActive = false)

        // When
        val activeUsers = userDao.findAllActive()

        // Then
        assertEquals(1, activeUsers.size)
        assertEquals("active@example.com", activeUsers[0].email)
        assertTrue(activeUsers[0].isActive)
    }

    @Test
    fun `update modifies user fields successfully`() {
        // Given
        val user = userDao.insert("original@example.com", "hash", "Original Name")!!

        // When
        val updatedUser = userDao.update(
            id = user.id,
            email = "updated@example.com",
            fullName = "Updated Name",
            isActive = false
        )

        // Then
        assertNotNull(updatedUser)
        assertEquals("updated@example.com", updatedUser.email)
        assertEquals("Updated Name", updatedUser.fullName)
        assertEquals(false, updatedUser.isActive)
    }

    @Test
    fun `update with partial fields only updates specified fields`() {
        // Given
        val user = userDao.insert("test@example.com", "hash", "Test User")!!

        // When
        val updatedUser = userDao.update(id = user.id, fullName = "New Name")

        // Then
        assertNotNull(updatedUser)
        assertEquals("test@example.com", updatedUser.email) // unchanged
        assertEquals("New Name", updatedUser.fullName) // changed
        assertTrue(updatedUser.isActive) // unchanged
    }

    @Test
    fun `update returns null when user does not exist`() {
        // When
        val result = userDao.update(999L, email = "new@example.com")

        // Then
        assertNull(result)
    }

    @Test
    fun `authenticate returns user with valid credentials for active user`() {
        val passwordHash = Base64.getEncoder().encodeToString("password".toByteArray())
        // Given
        userDao.insert("test@example.com", passwordHash, "Test User")

        // When
        val authenticatedUser = userDao.authenticate("test@example.com", "password")

        // Then
        assertNotNull(authenticatedUser)
        assertEquals("test@example.com", authenticatedUser.email)
        assertTrue(authenticatedUser.isActive)
    }

    @Test
    fun `authenticate returns null for inactive user`() {
        // Given
        val user = userDao.insert("test@example.com", "password", "Test User")!!
        userDao.update(user.id, isActive = false)

        // When
        val authenticatedUser = userDao.authenticate("test@example.com", "password")

        // Then
        assertNull(authenticatedUser)
    }

    @Test
    fun `authenticate returns null with invalid password`() {
        // Given
        userDao.insert("test@example.com", "correctpassword", "Test User")

        // When
        val authenticatedUser = userDao.authenticate("test@example.com", "wrongpassword")

        // Then
        assertNull(authenticatedUser)
    }

    @Test
    fun `authenticate returns null for nonexistent user`() {
        // When
        val authenticatedUser = userDao.authenticate("nonexistent@example.com", "password")

        // Then
        assertNull(authenticatedUser)
    }

    @Test
    fun `rowToUser maps database row correctly`() {
        // Given - create a user to test the mapping
        val user = userDao.insert("mapping@example.com", "hash", "Mapping Test")!!

        // When - retrieve and verify mapping
        val retrievedUser = userDao.findById(user.id)!!

        // Then
        assertEquals(user.id, retrievedUser.id)
        assertEquals("mapping@example.com", retrievedUser.email)
        assertEquals("Mapping Test", retrievedUser.fullName)
        assertTrue(retrievedUser.isActive)
        assertNotNull(retrievedUser.createdAt)
        assertNotNull(retrievedUser.updatedAt)
    }

    @Test
    fun `addUserRole assigns role to user`() = transaction {
        // Given
        val user = userDao.insert("role@example.com", "hashedPassword", "Role Test")!!
        val role = roleDao.insert("TEST_ROLE")!!

        // When
        val result = userDao.addUserRole(user.id, role.id)

        // Then
        assertTrue(result)
    }

    @Test
    fun `addUserRole returns false for duplicate role assignment`() = transaction {
        // Given
        val user = userDao.insert("duplicate@example.com", "hashedPassword", "Duplicate Test")!!
        val role = roleDao.insert("DUPLICATE_ROLE")!!
        userDao.addUserRole(user.id, role.id)

        // When
        val result = userDao.addUserRole(user.id, role.id)

        // Then
        assertFalse(result)
    }

    @Test
    fun `removeUserRole removes specific role from user`() = transaction {
        // Given
        val user = userDao.insert("remove@example.com", "hashedPassword", "Remove Test")!!
        val role1 = roleDao.insert("ROLE_1")!!
        val role2 = roleDao.insert("ROLE_2")!!
        userDao.addUserRole(user.id, role1.id)
        userDao.addUserRole(user.id, role2.id)

        // When
        val result = userDao.removeUserRole(user.id, role1.id)

        // Then
        assertTrue(result)
    }

    @Test
    fun `replaceUserRoles replaces all user roles`() = transaction {
        // Given
        val user = userDao.insert("replace@example.com", "hashedPassword", "Replace Test")!!
        val role1 = roleDao.insert("OLD_ROLE_1")!!
        val role2 = roleDao.insert("OLD_ROLE_2")!!
        val role3 = roleDao.insert("NEW_ROLE_1")!!
        val role4 = roleDao.insert("NEW_ROLE_2")!!

        userDao.addUserRole(user.id, role1.id)
        userDao.addUserRole(user.id, role2.id)

        // When
        val result = userDao.replaceUserRoles(user.id, listOf(role3.id, role4.id))

        // Then
        assertTrue(result)
    }

    @Test
    fun `replaceUserRoles with empty list removes all roles`() = transaction {
        // Given
        val user = userDao.insert("empty@example.com", "hashedPassword", "Empty Test")!!
        val role = roleDao.insert("TEMP_ROLE")!!
        userDao.addUserRole(user.id, role.id)

        // When
        val result = userDao.replaceUserRoles(user.id, emptyList())

        // Then
        assertTrue(result)
    }
}
