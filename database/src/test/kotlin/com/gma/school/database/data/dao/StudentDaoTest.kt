// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.MemberTypesTable
import com.gma.school.database.data.tables.StudentsTable
import com.gma.school.database.data.tables.UsersTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StudentDaoTest {

    private lateinit var database: Database
    private lateinit var studentDao: StudentDao
    private lateinit var memberTypeDao: MemberTypeDao
    private var memberTypeId: Long = 0

    @Before
    fun setup() {
        database = Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver"
        )

        transaction {
            SchemaUtils.create(UsersTable, MemberTypesTable, StudentsTable)

            // Insert test users for foreign key references
            for (i in 1..10) {
                UsersTable.insert {
                    it[email] = "user$i@test.com"
                    it[passwordHash] = "hash$i"
                }
            }

            // Insert test member type
            val insertStatement = MemberTypesTable.insert {
                it[name] = "Regular"
            }
            memberTypeId = insertStatement[MemberTypesTable.id].value
        }

        studentDao = StudentDao()
        memberTypeDao = MemberTypeDao()
    }

    @After
    fun tearDown() {
        transaction {
            SchemaUtils.drop(StudentsTable, MemberTypesTable, UsersTable)
        }
    }

    @Test
    fun `should create and find student by id`() {
        val student = studentDao.insert(
            userId = 1L,
            externalCode = "EXT001",
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phone = "555-1234",
            memberTypeId = memberTypeId
        )

        assertNotNull(student)
        assertEquals(1L, student.userId)
        assertEquals("John", student.firstName)
        assertEquals("Doe", student.lastName)
        assertEquals("john.doe@example.com", student.email)
        assertEquals("EXT001", student.externalCode)
        assertTrue(student.isActive)

        val found = studentDao.findById(student.id)
        assertNotNull(found)
        assertEquals(student.id, found.id)
        assertEquals(student.userId, found.userId)
    }

    @Test
    fun `should find student by email`() {
        val email = "test@example.com"
        studentDao.insert(
            userId = 2L,
            externalCode = null,
            firstName = "Jane",
            lastName = "Smith",
            email = email,
            phone = null,
            memberTypeId = memberTypeId
        )

        val found = studentDao.findByEmail(email)
        assertNotNull(found)
        assertEquals(email, found.email)
        assertEquals("Jane", found.firstName)
        assertEquals(2L, found.userId)
    }

    @Test
    fun `should find student by external code`() {
        val externalCode = "EXT123"
        studentDao.insert(
            userId = 3L,
            externalCode = externalCode,
            firstName = "Bob",
            lastName = "Wilson",
            email = null,
            phone = null,
            memberTypeId = memberTypeId
        )

        val found = studentDao.findByExternalCode(externalCode)
        assertNotNull(found)
        assertEquals(externalCode, found.externalCode)
        assertEquals("Bob", found.firstName)
        assertEquals(3L, found.userId)
    }

    @Test
    fun `should update student`() {
        val student = studentDao.insert(
            userId = 4L,
            externalCode = null,
            firstName = "Original",
            lastName = "Name",
            email = null,
            phone = null,
            memberTypeId = memberTypeId
        )!!

        val updated = studentDao.update(
            id = student.id,
            firstName = "Updated",
            email = "updated@example.com",
            isActive = false
        )

        assertNotNull(updated)
        assertEquals("Updated", updated.firstName)
        assertEquals("Name", updated.lastName) // unchanged
        assertEquals("updated@example.com", updated.email)
        assertEquals(false, updated.isActive)
        assertEquals(4L, updated.userId) // unchanged
    }

    @Test
    fun `should find all active students`() {
        // Create active student
        studentDao.insert(
            userId = 5L,
            externalCode = null,
            firstName = "Active",
            lastName = "Student",
            email = null,
            phone = null,
            memberTypeId = memberTypeId
        )

        // Create inactive student
        val inactive = studentDao.insert(
            userId = 6L,
            externalCode = null,
            firstName = "Inactive",
            lastName = "Student",
            email = null,
            phone = null,
            memberTypeId = memberTypeId
        )!!

        studentDao.update(inactive.id, isActive = false)

        val activeStudents = studentDao.findAllActive()
        assertEquals(1, activeStudents.size)
        assertEquals("Active", activeStudents[0].firstName)
    }

    @Test
    fun `should return null for non-existent student`() {
        val found = studentDao.findById(999L)
        assertNull(found)
    }

    @Test
    fun `should get student userId`() {
        val student = studentDao.insert(
            userId = 7L,
            externalCode = null,
            firstName = "Test",
            lastName = "User",
            email = null,
            phone = null,
            memberTypeId = memberTypeId
        )!!

        val found = studentDao.findById(student.id)
        assertNotNull(found)
        assertEquals(7L, found.userId)
    }
}
