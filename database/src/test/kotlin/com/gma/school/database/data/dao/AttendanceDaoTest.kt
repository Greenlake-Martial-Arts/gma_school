// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.AttendanceEntriesTable
import com.gma.school.database.data.tables.AttendancesTable
import com.gma.school.database.data.tables.MemberTypesTable
import com.gma.school.database.data.tables.StudentsTable
import com.gma.school.database.data.tables.UsersTable
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
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

class AttendanceDaoTest {

    private lateinit var database: Database
    private lateinit var attendanceDao: AttendanceDao
    private var studentId1: Long = 0
    private var studentId2: Long = 0
    private val testDate = LocalDate.parse("2025-01-15")

    @Before
    fun setup() {
        database = Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver"
        )

        transaction {
            SchemaUtils.create(UsersTable, MemberTypesTable, StudentsTable, AttendancesTable, AttendanceEntriesTable)

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
        }

        attendanceDao = AttendanceDao()
    }

    @After
    fun tearDown() {
        transaction {
            SchemaUtils.drop(AttendanceEntriesTable, AttendancesTable, StudentsTable, MemberTypesTable, UsersTable)
        }
    }

    @Test
    fun `should create and find attendance by id`() {
        val attendance = attendanceDao.create(testDate, "Test class")

        assertNotNull(attendance)
        assertEquals(testDate, attendance.classDate)
        assertEquals("Test class", attendance.notes)
        assertNotNull(attendance.createdAt)

        val found = attendanceDao.findById(attendance.id)
        assertNotNull(found)
        assertEquals(attendance.id, found.id)
        assertEquals(attendance.classDate, found.classDate)
    }

    @Test
    fun `should find all attendances`() {
        attendanceDao.create(testDate, "Class 1")
        attendanceDao.create(testDate.plus(DatePeriod(days = 1)), "Class 2")

        val attendances = attendanceDao.findAll()
        assertEquals(2, attendances.size)
    }

    @Test
    fun `should find attendances by date range`() {
        val date1 = LocalDate.parse("2025-01-10")
        val date2 = LocalDate.parse("2025-01-15")
        val date3 = LocalDate.parse("2025-01-20")

        attendanceDao.create(date1, "Class 1")
        attendanceDao.create(date2, "Class 2")
        attendanceDao.create(date3, "Class 3")

        val attendances = attendanceDao.findByDateRange(date1, date2)
        assertEquals(2, attendances.size)
    }

    @Test
    fun `should update attendance notes`() {
        val attendance = attendanceDao.create(testDate, "Original notes")

        val updated = attendanceDao.update(attendance.id, "Updated notes")
        assertTrue(updated)

        val found = attendanceDao.findById(attendance.id)
        assertNotNull(found)
        assertEquals("Updated notes", found.notes)
    }

    @Test
    fun `should add and remove students from attendance`() {
        val attendance = attendanceDao.create(testDate, "Test class")

        // Add students
        val added1 = attendanceDao.addStudent(attendance.id, studentId1)
        val added2 = attendanceDao.addStudent(attendance.id, studentId2)
        assertTrue(added1)
        assertTrue(added2)

        // Check students
        val students = attendanceDao.getStudents(attendance.id)
        assertEquals(2, students.size)
        assertTrue(students.contains(studentId1))
        assertTrue(students.contains(studentId2))

        // Remove student
        val removed = attendanceDao.removeStudent(attendance.id, studentId1)
        assertTrue(removed)

        val remainingStudents = attendanceDao.getStudents(attendance.id)
        assertEquals(1, remainingStudents.size)
        assertTrue(remainingStudents.contains(studentId2))
    }

    @Test
    fun `should get attendances by student`() {
        val attendance1 = attendanceDao.create(testDate, "Class 1")
        val attendance2 = attendanceDao.create(testDate.plus(DatePeriod(days = 1)), "Class 2")

        attendanceDao.addStudent(attendance1.id, studentId1)
        attendanceDao.addStudent(attendance2.id, studentId1)

        val attendances = attendanceDao.getAttendancesByStudent(studentId1)
        assertEquals(2, attendances.size)
        assertTrue(attendances.contains(attendance1.id))
        assertTrue(attendances.contains(attendance2.id))
    }

    @Test
    fun `should register attendance with multiple students`() {
        val studentIds = listOf(studentId1, studentId2)
        val attendance = attendanceDao.registerAttendance(testDate, studentIds, "Group class")

        assertNotNull(attendance)
        assertEquals(testDate, attendance.classDate)
        assertEquals("Group class", attendance.notes)

        val students = attendanceDao.getStudents(attendance.id)
        assertEquals(2, students.size)
        assertTrue(students.containsAll(studentIds))
    }

    @Test
    fun `should delete attendance and entries`() {
        val attendance = attendanceDao.create(testDate, "To delete")
        attendanceDao.addStudent(attendance.id, studentId1)

        val deleted = attendanceDao.delete(attendance.id)
        assertTrue(deleted)

        val found = attendanceDao.findById(attendance.id)
        assertNull(found)

        val students = attendanceDao.getStudents(attendance.id)
        assertEquals(0, students.size)
    }

    @Test
    fun `should return null for non-existent attendance`() {
        val found = attendanceDao.findById(999L)
        assertNull(found)
    }
}
