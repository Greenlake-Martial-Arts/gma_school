// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.LevelsTable
import com.gma.school.database.data.tables.MemberTypesTable
import com.gma.school.database.data.tables.StudentLevelsTable
import com.gma.school.database.data.tables.StudentsTable
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
import kotlin.test.assertTrue

class StudentLevelDaoTest {

    private lateinit var database: Database
    private lateinit var studentLevelDao: StudentLevelDao
    private var studentId1: Long = 0
    private var studentId2: Long = 0
    private var levelId1: Long = 0
    private var levelId2: Long = 0

    @Before
    fun setup() {
        database = Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver"
        )

        transaction {
            SchemaUtils.create(UsersTable, MemberTypesTable, StudentsTable, LevelsTable, StudentLevelsTable)

            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            // Insert test users
            for (i in 1..2) {
                UsersTable.insert {
                    it[username] = "user$i@test.com"
                    it[passwordHash] = "hash$i"
                    it[createdAt] = now
                    it[updatedAt] = now
                }
            }

            // Insert test member type
            val memberTypeStatement = MemberTypesTable.insert {
                it[name] = "Regular"
            }
            val memberTypeId = memberTypeStatement[MemberTypesTable.id].value

            // Insert test students
            val student1Statement = StudentsTable.insert {
                it[StudentsTable.userId] = 1L
                it[StudentsTable.firstName] = "John"
                it[StudentsTable.lastName] = "Doe"
                it[StudentsTable.email] = "john@test.com"
                it[StudentsTable.memberTypeId] = memberTypeId
                it[StudentsTable.createdAt] = now
                it[StudentsTable.updatedAt] = now
            }
            studentId1 = student1Statement[StudentsTable.id].value

            val student2Statement = StudentsTable.insert {
                it[StudentsTable.userId] = 2L
                it[StudentsTable.firstName] = "Jane"
                it[StudentsTable.lastName] = "Smith"
                it[StudentsTable.email] = "jane@test.com"
                it[StudentsTable.memberTypeId] = memberTypeId
                it[StudentsTable.createdAt] = now
                it[StudentsTable.updatedAt] = now
            }
            studentId2 = student2Statement[StudentsTable.id].value

            // Insert test levels
            val level1Statement = LevelsTable.insert {
                it[code] = "WHITE"
                it[displayName] = "White Belt"
                it[orderSeq] = 1
                it[createdAt] = now
                it[updatedAt] = now
            }
            levelId1 = level1Statement[LevelsTable.id].value

            val level2Statement = LevelsTable.insert {
                it[code] = "YELLOW"
                it[displayName] = "Yellow Belt"
                it[orderSeq] = 2
                it[createdAt] = now
                it[updatedAt] = now
            }
            levelId2 = level2Statement[LevelsTable.id].value
        }

        studentLevelDao = StudentLevelDao()
    }

    @After
    fun tearDown() {
        transaction {
            SchemaUtils.drop(StudentLevelsTable, LevelsTable, StudentsTable, MemberTypesTable, UsersTable)
        }
    }

    @Test
    fun `should assign level to student`() {
        val studentLevel = studentLevelDao.assign(studentId1, levelId1)

        assertNotNull(studentLevel)
        assertEquals(studentId1, studentLevel.studentId)
        assertEquals(levelId1, studentLevel.levelId)
        assertNotNull(studentLevel.assignedAt)
    }

    @Test
    fun `should find student level by student`() {
        studentLevelDao.assign(studentId1, levelId1)

        val found = studentLevelDao.findByStudent(studentId1)
        assertNotNull(found)
        assertEquals(studentId1, found.studentId)
        assertEquals(levelId1, found.levelId)
    }

    @Test
    fun `should find students by level`() {
        studentLevelDao.assign(studentId1, levelId1)
        studentLevelDao.assign(studentId2, levelId1)

        val studentsAtLevel = studentLevelDao.findByLevel(levelId1)
        assertEquals(2, studentsAtLevel.size)

        val studentIds = studentsAtLevel.map { it.studentId }
        assertTrue(studentIds.contains(studentId1))
        assertTrue(studentIds.contains(studentId2))
    }

    @Test
    fun `should replace student level when assigning new level`() {
        // Assign initial level
        studentLevelDao.assign(studentId1, levelId1)

        val initial = studentLevelDao.findByStudent(studentId1)
        assertNotNull(initial)
        assertEquals(levelId1, initial.levelId)

        // Assign new level (should replace)
        studentLevelDao.assign(studentId1, levelId2)

        val updated = studentLevelDao.findByStudent(studentId1)
        assertNotNull(updated)
        assertEquals(levelId2, updated.levelId)

        // Should only have one level assignment
        val level1Students = studentLevelDao.findByLevel(levelId1)
        assertEquals(0, level1Students.size)

        val level2Students = studentLevelDao.findByLevel(levelId2)
        assertEquals(1, level2Students.size)
    }

    @Test
    fun `should delete student level assignment`() {
        studentLevelDao.assign(studentId1, levelId1)

        val found = studentLevelDao.findByStudent(studentId1)
        assertNotNull(found)

        val deleted = studentLevelDao.delete(studentId1)
        assertTrue(deleted)

        val notFound = studentLevelDao.findByStudent(studentId1)
        assertNull(notFound)
    }

    @Test
    fun `should return null for student without level assignment`() {
        val found = studentLevelDao.findByStudent(studentId1)
        assertNull(found)
    }

    @Test
    fun `should return empty list for level with no students`() {
        val students = studentLevelDao.findByLevel(levelId1)
        assertEquals(0, students.size)
    }

    @Test
    fun `should handle multiple students at different levels`() {
        studentLevelDao.assign(studentId1, levelId1)
        studentLevelDao.assign(studentId2, levelId2)

        val level1Students = studentLevelDao.findByLevel(levelId1)
        assertEquals(1, level1Students.size)
        assertEquals(studentId1, level1Students[0].studentId)

        val level2Students = studentLevelDao.findByLevel(levelId2)
        assertEquals(1, level2Students.size)
        assertEquals(studentId2, level2Students[0].studentId)
    }

    @Test
    fun `should return false when deleting non-existent assignment`() {
        val deleted = studentLevelDao.delete(999L)
        assertTrue(!deleted) // Should return false for non-existent student
    }
}
