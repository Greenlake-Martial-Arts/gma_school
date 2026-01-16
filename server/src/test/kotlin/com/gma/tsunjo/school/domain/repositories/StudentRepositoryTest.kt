// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.LevelDao
import com.gma.school.database.data.dao.MemberTypeDao
import com.gma.school.database.data.dao.StudentDao
import com.gma.school.database.data.dao.StudentLevelDao
import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.models.MemberType
import com.gma.tsunjo.school.domain.models.Student
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StudentRepositoryTest {

    private lateinit var studentDao: StudentDao
    private lateinit var memberTypeDao: MemberTypeDao
    private lateinit var studentLevelDao: StudentLevelDao
    private lateinit var levelDao: LevelDao
    private lateinit var studentRepository: StudentRepository

    private val testMemberType = MemberType(id = 1L, name = "Regular")
    private val testDateTime = LocalDateTime.parse("2023-01-01T10:00:00")
    private val testDate = LocalDate.parse("2023-01-01")
    private val testStudent = Student(
        id = 1L,
        userId = 100L,
        externalCode = "EXT001",
        firstName = "John",
        lastName = "Doe",
        email = "john.doe@example.com",
        phone = "555-1234",
        address = "123 Main St",
        memberTypeId = 1L,
        signupDate = testDate,
        isActive = true,
        createdAt = testDateTime,
        updatedAt = testDateTime
    )

    @Before
    fun setup() {
        studentDao = mockk()
        memberTypeDao = mockk()
        studentLevelDao = mockk(relaxed = true)
        levelDao = mockk()
        studentRepository = StudentRepository(studentDao, memberTypeDao, studentLevelDao, levelDao)
    }

    @Test
    fun `should get all students`() {
        val students = listOf(testStudent)
        every { studentDao.findAll() } returns students

        val result = studentRepository.getAllStudents()

        assertEquals(students, result)
        verify { studentDao.findAll() }
    }

    @Test
    fun `should get active students`() {
        val students = listOf(testStudent)
        every { studentDao.findAllActive() } returns students

        val result = studentRepository.getActiveStudents()

        assertEquals(students, result)
        verify { studentDao.findAllActive() }
    }

    @Test
    fun `should get student by id`() {
        every { studentDao.findById(1L) } returns testStudent

        val result = studentRepository.getStudentById(1L)

        assertEquals(testStudent, result)
        verify { studentDao.findById(1L) }
    }

    @Test
    fun `should create student successfully`() {
        val testLevel = com.gma.tsunjo.school.domain.models.Level(
            id = 1L,
            code = "BASIC",
            displayName = "Basic",
            orderSeq = 1,
            description = null,
            createdAt = testDateTime,
            updatedAt = testDateTime
        )

        every { memberTypeDao.findById(1L) } returns testMemberType
        every { studentDao.findByEmail("john.doe@example.com") } returns null
        every { studentDao.findByExternalCode("EXT001") } returns null
        every {
            studentDao.insert(
                100L,
                "EXT001",
                "John",
                "Doe",
                "john.doe@example.com",
                "555-1234",
                "123 Main St",
                1L,
                testDate
            )
        } returns testStudent
        every { levelDao.findByCode("BASIC") } returns testLevel

        val result = studentRepository.createStudent(
            userId = 100L,
            externalCode = "EXT001",
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phone = "555-1234",
            address = "123 Main St",
            memberTypeId = 1L,
            signupDate = testDate
        )

        assertTrue(result.isSuccess)
        assertEquals(testStudent, result.getOrNull())
        verify {
            studentDao.insert(
                100L,
                "EXT001",
                "John",
                "Doe",
                "john.doe@example.com",
                "555-1234",
                "123 Main St",
                1L,
                testDate
            )
        }
    }

    @Test
    fun `should fail to create student with invalid member type`() {
        every { memberTypeDao.findById(999L) } returns null

        val result = studentRepository.createStudent(
            userId = 100L,
            externalCode = null,
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phone = null,
            address = null,
            memberTypeId = 999L,
            signupDate = null
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException.MemberTypeNotFound)
    }

    @Test
    fun `should fail to create student with duplicate email`() {
        every { memberTypeDao.findById(1L) } returns testMemberType
        every { studentDao.findByEmail("john.doe@example.com") } returns testStudent

        val result = studentRepository.createStudent(
            userId = 100L,
            externalCode = null,
            firstName = "Jane",
            lastName = "Smith",
            email = "john.doe@example.com",
            phone = null,
            address = null,
            memberTypeId = 1L,
            signupDate = null
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException.StudentAlreadyExists)
    }

    @Test
    fun `should fail to create student with duplicate external code`() {
        every { memberTypeDao.findById(1L) } returns testMemberType
        every { studentDao.findByEmail(any()) } returns null
        every { studentDao.findByExternalCode("EXT001") } returns testStudent

        val result = studentRepository.createStudent(
            userId = 100L,
            externalCode = "EXT001",
            firstName = "Jane",
            lastName = "Smith",
            email = "jane.smith@example.com",
            phone = null,
            address = null,
            memberTypeId = 1L,
            signupDate = null
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException.StudentAlreadyExists)
    }

    @Test
    fun `should update student successfully`() {
        val updatedStudent = testStudent.copy(firstName = "Jane")
        every { memberTypeDao.findById(1L) } returns testMemberType
        every { studentDao.update(1L, firstName = "Jane", memberTypeId = 1L) } returns updatedStudent

        val result = studentRepository.updateStudent(
            id = 1L,
            firstName = "Jane",
            memberTypeId = 1L
        )

        assertNotNull(result)
        assertEquals("Jane", result.firstName)
        verify { studentDao.update(1L, firstName = "Jane", memberTypeId = 1L) }
    }

    @Test
    fun `should fail to update student with invalid member type`() {
        every { memberTypeDao.findById(999L) } returns null

        val result = studentRepository.updateStudent(
            id = 1L,
            memberTypeId = 999L
        )

        assertEquals(null, result)
        verify { memberTypeDao.findById(999L) }
    }

    @Test
    fun `should get student userId`() {
        every { studentDao.findById(1L) } returns testStudent

        val result = studentRepository.getStudentUserId(1L)

        assertEquals(100L, result)
        verify { studentDao.findById(1L) }
    }

    @Test
    fun `should activate student`() {
        every { studentDao.update(1L, isActive = true) } returns testStudent.copy(isActive = true)

        val result = studentRepository.setStudentActiveStatus(1L, true)

        assertTrue(result)
        verify { studentDao.update(1L, isActive = true) }
    }

    @Test
    fun `should deactivate student`() {
        every { studentDao.update(1L, isActive = false) } returns testStudent.copy(isActive = false)

        val result = studentRepository.setStudentActiveStatus(1L, false)

        assertTrue(result)
        verify { studentDao.update(1L, isActive = false) }
    }
}
