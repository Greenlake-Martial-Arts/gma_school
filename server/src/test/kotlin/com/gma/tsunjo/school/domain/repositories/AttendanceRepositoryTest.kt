// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.AttendanceDao
import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.models.Attendance
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AttendanceRepositoryTest {

    private val attendanceDao = mockk<AttendanceDao>()
    private val attendanceRepository = AttendanceRepository(attendanceDao)

    private val testAttendance = Attendance(
        id = 1L,
        classDate = LocalDate.parse("2025-01-01"),
        notes = "Regular class",
        createdAt = LocalDateTime.parse("2025-01-01T00:00:00")
    )

    @Test
    fun `getAllAttendances returns all attendances from dao`() {
        // Given
        val attendances = listOf(testAttendance)
        every { attendanceDao.findAll() } returns attendances

        // When
        val result = attendanceRepository.getAllAttendances()

        // Then
        assertEquals(attendances, result)
        verify { attendanceDao.findAll() }
    }

    @Test
    fun `getAttendanceById returns attendance when found`() {
        // Given
        every { attendanceDao.findById(1L) } returns testAttendance

        // When
        val result = attendanceRepository.getAttendanceById(1L)

        // Then
        assertEquals(testAttendance, result)
        verify { attendanceDao.findById(1L) }
    }

    @Test
    fun `getAttendanceById returns null when not found`() {
        // Given
        every { attendanceDao.findById(999L) } returns null

        // When
        val result = attendanceRepository.getAttendanceById(999L)

        // Then
        assertEquals(null, result)
        verify { attendanceDao.findById(999L) }
    }

    @Test
    fun `getAttendancesByDateRange returns attendances in range`() {
        // Given
        val startDate = LocalDate.parse("2025-01-01")
        val endDate = LocalDate.parse("2025-01-31")
        val attendances = listOf(testAttendance)
        every { attendanceDao.findByDateRange(startDate, endDate) } returns attendances

        // When
        val result = attendanceRepository.getAttendancesByDateRange(startDate, endDate)

        // Then
        assertEquals(attendances, result)
        verify { attendanceDao.findByDateRange(startDate, endDate) }
    }

    @Test
    fun `createAttendance returns success when dao succeeds`() {
        // Given
        val classDate = LocalDate.parse("2025-01-01")
        every { attendanceDao.create(classDate, "Regular class") } returns testAttendance

        // When
        val result = attendanceRepository.createAttendance(classDate, "Regular class")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(testAttendance, result.getOrNull())
        verify { attendanceDao.create(classDate, "Regular class") }
    }

    @Test
    fun `createAttendance returns failure when dao throws exception`() {
        // Given
        val classDate = LocalDate.parse("2025-01-01")
        every { attendanceDao.create(classDate, "Regular class") } throws RuntimeException("Database error")

        // When
        val result = attendanceRepository.createAttendance(classDate, "Regular class")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException.DatabaseError)
        verify { attendanceDao.create(classDate, "Regular class") }
    }

    @Test
    fun `updateAttendance returns true when dao succeeds`() {
        // Given
        every { attendanceDao.update(1L, "Updated notes") } returns true

        // When
        val result = attendanceRepository.updateAttendance(1L, "Updated notes")

        // Then
        assertTrue(result)
        verify { attendanceDao.update(1L, "Updated notes") }
    }

    @Test
    fun `deleteAttendance returns true when dao succeeds`() {
        // Given
        every { attendanceDao.delete(1L) } returns true

        // When
        val result = attendanceRepository.deleteAttendance(1L)

        // Then
        assertTrue(result)
        verify { attendanceDao.delete(1L) }
    }

    @Test
    fun `addStudentToAttendance returns true when dao succeeds`() {
        // Given
        every { attendanceDao.addStudent(1L, 2L) } returns true

        // When
        val result = attendanceRepository.addStudentToAttendance(1L, 2L)

        // Then
        assertTrue(result)
        verify { attendanceDao.addStudent(1L, 2L) }
    }

    @Test
    fun `removeStudentFromAttendance returns true when dao succeeds`() {
        // Given
        every { attendanceDao.removeStudent(1L, 2L) } returns true

        // When
        val result = attendanceRepository.removeStudentFromAttendance(1L, 2L)

        // Then
        assertTrue(result)
        verify { attendanceDao.removeStudent(1L, 2L) }
    }

    @Test
    fun `getStudentsInAttendance returns student ids`() {
        // Given
        val studentIds = listOf(1L, 2L, 3L)
        every { attendanceDao.getStudents(1L) } returns studentIds

        // When
        val result = attendanceRepository.getStudentsInAttendance(1L)

        // Then
        assertEquals(studentIds, result)
        verify { attendanceDao.getStudents(1L) }
    }
}
