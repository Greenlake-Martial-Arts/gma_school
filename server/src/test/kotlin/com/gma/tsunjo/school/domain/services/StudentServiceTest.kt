// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.services

import com.gma.tsunjo.school.domain.models.Student
import com.gma.tsunjo.school.domain.repositories.StudentRepository
import com.gma.tsunjo.school.domain.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class StudentServiceTest {

    private lateinit var studentRepository: StudentRepository
    private lateinit var userRepository: UserRepository
    private lateinit var studentService: StudentService

    private val testStudent = Student(
        id = 1L,
        userId = 100L,
        externalCode = "EXT001",
        firstName = "John",
        lastName = "Doe",
        email = "john.doe@example.com",
        phone = "555-1234",
        memberTypeId = 1L,
        isActive = true,
        createdAt = "2025-01-01T00:00:00",
        updatedAt = "2025-01-01T00:00:00"
    )

    @Before
    fun setup() {
        studentRepository = mockk()
        userRepository = mockk()
        studentService = StudentService(studentRepository, userRepository)
    }

    @Test
    fun `should update student and sync user fullName when name changes`() {
        val updatedStudent = testStudent.copy(firstName = "Jane")
        every { studentRepository.updateStudent(1L, firstName = "Jane", memberTypeId = 1L) } returns updatedStudent
        every { userRepository.updateUser(100L, email = null, fullName = "Jane Doe") } returns mockk()

        val result = studentService.updateStudent(
            id = 1L,
            firstName = "Jane",
            memberTypeId = 1L
        )

        assertNotNull(result)
        assertEquals("Jane", result.firstName)
        verify { studentRepository.updateStudent(1L, firstName = "Jane", memberTypeId = 1L) }
        verify { userRepository.updateUser(100L, email = null, fullName = "Jane Doe") }
    }

    @Test
    fun `should update student and sync user email when email changes`() {
        val updatedStudent = testStudent.copy(email = "jane.doe@example.com")
        every { studentRepository.updateStudent(1L, email = "jane.doe@example.com") } returns updatedStudent
        every { userRepository.updateUser(100L, email = "jane.doe@example.com", fullName = null) } returns mockk()

        val result = studentService.updateStudent(
            id = 1L,
            email = "jane.doe@example.com"
        )

        assertNotNull(result)
        assertEquals("jane.doe@example.com", result.email)
        verify { studentRepository.updateStudent(1L, email = "jane.doe@example.com") }
        verify { userRepository.updateUser(100L, email = "jane.doe@example.com", fullName = null) }
    }

    @Test
    fun `should update student and sync both user fullName and email when both change`() {
        val updatedStudent = testStudent.copy(firstName = "Jane", email = "jane.smith@example.com")
        every { studentRepository.updateStudent(1L, firstName = "Jane", email = "jane.smith@example.com") } returns updatedStudent
        every { userRepository.updateUser(100L, email = "jane.smith@example.com", fullName = "Jane Doe") } returns mockk()

        val result = studentService.updateStudent(
            id = 1L,
            firstName = "Jane",
            email = "jane.smith@example.com"
        )

        assertNotNull(result)
        assertEquals("Jane", result.firstName)
        assertEquals("jane.smith@example.com", result.email)
        verify { studentRepository.updateStudent(1L, firstName = "Jane", email = "jane.smith@example.com") }
        verify { userRepository.updateUser(100L, email = "jane.smith@example.com", fullName = "Jane Doe") }
    }

    @Test
    fun `should update student without syncing user when name unchanged`() {
        val updatedStudent = testStudent.copy(phone = "555-9999")
        every { studentRepository.updateStudent(1L, phone = "555-9999", memberTypeId = 1L) } returns updatedStudent

        val result = studentService.updateStudent(
            id = 1L,
            phone = "555-9999",
            memberTypeId = 1L
        )

        assertNotNull(result)
        assertEquals("555-9999", result.phone)
        verify { studentRepository.updateStudent(1L, phone = "555-9999", memberTypeId = 1L) }
        verify(exactly = 0) { userRepository.updateUser(any(), any()) }
    }
}
