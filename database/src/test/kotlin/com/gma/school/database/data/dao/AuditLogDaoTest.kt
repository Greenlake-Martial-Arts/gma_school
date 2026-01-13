// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.AuditLogTable
import com.gma.school.database.data.tables.UsersTable
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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

class AuditLogDaoTest {

    private lateinit var database: Database
    private lateinit var auditLogDao: AuditLogDao
    private var userId: Long = 0

    @Before
    fun setup() {
        database = Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver"
        )

        transaction {
            SchemaUtils.create(UsersTable, AuditLogTable)

            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            // Insert test user
            val userStatement = UsersTable.insert {
                it[username] = "testuser@test.com"
                it[passwordHash] = "hash123"
                it[createdAt] = now
                it[updatedAt] = now
            }
            userId = userStatement[UsersTable.id].value
        }

        auditLogDao = AuditLogDao()
    }

    @After
    fun tearDown() {
        transaction {
            SchemaUtils.drop(AuditLogTable, UsersTable)
        }
    }

    @Test
    fun `should create and find audit log by id`() {
        val auditLog = auditLogDao.create(
            userId = userId,
            action = "CREATE",
            entity = "students",
            entityId = 123L,
            description = "Created new student",
            userAgent = "Mozilla/5.0"
        )

        assertNotNull(auditLog)
        assertEquals(userId, auditLog.userId)
        assertEquals("CREATE", auditLog.action)
        assertEquals("students", auditLog.entity)
        assertEquals(123L, auditLog.entityId)
        assertEquals("Created new student", auditLog.description)
        assertEquals("Mozilla/5.0", auditLog.userAgent)
        assertNotNull(auditLog.createdAt)

        val found = auditLogDao.findById(auditLog.id)
        assertNotNull(found)
        assertEquals(auditLog.id, found.id)
        assertEquals(auditLog.action, found.action)
    }

    @Test
    fun `should create audit log with null values`() {
        val auditLog = auditLogDao.create(
            userId = userId,
            action = "LOGIN",
            entity = "users",
            entityId = null,
            description = null,
            userAgent = null
        )

        assertNotNull(auditLog)
        assertEquals("LOGIN", auditLog.action)
        assertEquals("users", auditLog.entity)
        assertNull(auditLog.entityId)
        assertNull(auditLog.description)
        assertNull(auditLog.userAgent)
    }

    @Test
    fun `should find audit logs by user`() {
        auditLogDao.create(userId, "CREATE", "students", 1L, "Created student 1", null)
        auditLogDao.create(userId, "UPDATE", "students", 1L, "Updated student 1", null)

        val logs = auditLogDao.findByUser(userId)
        assertEquals(2, logs.size)
        assertEquals("CREATE", logs[0].action)
        assertEquals("UPDATE", logs[1].action)
    }

    @Test
    fun `should find audit logs by entity`() {
        auditLogDao.create(userId, "CREATE", "students", 1L, "Created student", null)
        auditLogDao.create(userId, "CREATE", "moves", 2L, "Created move", null)
        auditLogDao.create(userId, "UPDATE", "students", 1L, "Updated student", null)

        val studentLogs = auditLogDao.findByEntity("students", null)
        assertEquals(2, studentLogs.size)

        val moveLogs = auditLogDao.findByEntity("moves", null)
        assertEquals(1, moveLogs.size)
    }

    @Test
    fun `should find audit logs by entity and entityId`() {
        auditLogDao.create(userId, "CREATE", "students", 1L, "Created student", null)
        auditLogDao.create(userId, "CREATE", "students", 2L, "Created another student", null)

        val student1Logs = auditLogDao.findByEntity("students", 1L)
        assertEquals(1, student1Logs.size)
        assertEquals(1L, student1Logs[0].entityId)

        val student2Logs = auditLogDao.findByEntity("students", 2L)
        assertEquals(1, student2Logs.size)
        assertEquals(2L, student2Logs[0].entityId)
    }

    @Test
    fun `should return null for non-existent audit log`() {
        val found = auditLogDao.findById(999L)
        assertNull(found)
    }

    @Test
    fun `should return empty list for non-existent user`() {
        val logs = auditLogDao.findByUser(999L)
        assertEquals(0, logs.size)
    }

    @Test
    fun `should return empty list for non-existent entity`() {
        val logs = auditLogDao.findByEntity("nonexistent", null)
        assertEquals(0, logs.size)
    }
}
