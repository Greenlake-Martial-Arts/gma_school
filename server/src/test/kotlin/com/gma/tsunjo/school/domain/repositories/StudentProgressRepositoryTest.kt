// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.StudentProgressDao
import com.gma.tsunjo.school.domain.models.ProgressState
import com.gma.tsunjo.school.domain.models.StudentProgress
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.LocalDateTime
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StudentProgressRepositoryTest {

    private val studentProgressDao = mockk<StudentProgressDao>()
    private val levelRequirementDao = mockk<com.gma.school.database.data.dao.LevelRequirementDao>()
    private val levelDao = mockk<com.gma.school.database.data.dao.LevelDao>()
    private val moveDao = mockk<com.gma.school.database.data.dao.MoveDao>()
    private val studentDao = mockk<com.gma.school.database.data.dao.StudentDao>()
    private val studentLevelDao = mockk<com.gma.school.database.data.dao.StudentLevelDao>()
    private val auditLogDao = mockk<com.gma.school.database.data.dao.AuditLogDao>(relaxed = true)
    private val repository = StudentProgressRepository(
        studentProgressDao,
        levelRequirementDao,
        levelDao,
        moveDao,
        studentDao,
        studentLevelDao,
        auditLogDao
    )

    private val testProgress = StudentProgress(
        id = 1L,
        studentId = 1L,
        levelRequirementId = 1L,
        status = com.gma.tsunjo.school.domain.models.ProgressState.NOT_STARTED,
        completedAt = null,
        instructorId = null,
        attempts = 0,
        notes = "Test progress",
        createdAt = LocalDateTime.parse("2025-01-01T00:00:00"),
        updatedAt = LocalDateTime.parse("2025-01-01T00:00:00")
    )

    private val testLevel = com.gma.tsunjo.school.domain.models.Level(
        id = 1L,
        code = "WHITE",
        displayName = "White Belt",
        orderSeq = 1,
        description = "First level",
        createdAt = LocalDateTime.parse("2025-01-01T00:00:00"),
        updatedAt = LocalDateTime.parse("2025-01-01T00:00:00")
    )

    private val testMove = com.gma.tsunjo.school.domain.models.Move(
        id = 1L,
        name = "Test Move",
        description = "Test",
        moveCategoryId = 1L,
        createdAt = LocalDateTime.parse("2025-01-01T00:00:00"),
        updatedAt = LocalDateTime.parse("2025-01-01T00:00:00")
    )

    private val testLevelRequirement = com.gma.tsunjo.school.domain.models.LevelRequirement(
        id = 1L,
        levelId = 1L,
        moveId = 1L,
        sortOrder = 1,
        levelSpecificNotes = null,
        isRequired = true,
        createdAt = LocalDateTime.parse("2025-01-01T00:00:00"),
        updatedAt = LocalDateTime.parse("2025-01-01T00:00:00")
    )

    private val testStudentLevel = com.gma.tsunjo.school.domain.models.StudentLevel(
        studentId = 1L,
        levelId = 1L,
        assignedAt = LocalDateTime.parse("2025-01-01T00:00:00")
    )

    @Test
    fun `getAllProgress returns all progress from dao`() {
        // Given
        val progressList = listOf(testProgress)
        every { studentProgressDao.findAll() } returns progressList
        every { levelRequirementDao.findById(any()) } returns null
        every { levelDao.findById(any()) } returns null
        every { moveDao.findById(any()) } returns null
        every { studentDao.findById(any()) } returns null

        // When
        val result = repository.getAllProgress()

        // Then
        assertEquals(1, result.size)
        assertEquals(testProgress.id, result[0].id)
        verify { studentProgressDao.findAll() }
    }

    @Test
    fun `getProgressById returns progress when found`() {
        // Given
        every { studentProgressDao.findById(1L) } returns testProgress
        every { levelRequirementDao.findById(any()) } returns null
        every { levelDao.findById(any()) } returns null
        every { moveDao.findById(any()) } returns null
        every { studentDao.findById(any()) } returns null

        // When
        val result = repository.getProgressById(1L)

        // Then
        assertNotNull(result)
        assertEquals(testProgress.id, result.id)
        verify { studentProgressDao.findById(1L) }
    }

    @Test
    fun `getProgressById returns null when not found`() {
        // Given
        every { studentProgressDao.findById(999L) } returns null

        // When
        val result = repository.getProgressById(999L)

        // Then
        assertNull(result)
        verify { studentProgressDao.findById(999L) }
    }

    @Test
    fun `getProgressByStudent returns student progress for current level`() {
        // Given - student has current level
        every { studentLevelDao.findByStudent(1L) } returns testStudentLevel
        every { levelDao.findById(1L) } returns testLevel
        every { levelRequirementDao.findByLevel(1L) } returns listOf(testLevelRequirement)
        every { studentProgressDao.findByStudent(1L) } returns listOf(testProgress)
        every { moveDao.findById(1L) } returns testMove

        // When
        val result = repository.getProgressByStudent(1L)

        // Then
        assertNotNull(result)
        assertEquals(testLevel.id, result?.level?.id)
    }

    @Test
    fun `getProgressByStudent returns null when student has no level`() {
        // Given
        every { studentLevelDao.findByStudent(1L) } returns null

        // When
        val result = repository.getProgressByStudent(1L)

        // Then
        assertNull(result)
    }

    @Test
    fun `createProgress returns success when dao succeeds`() {
        // Given
        every {
            studentProgressDao.create(1L, 1L, ProgressState.IN_PROGRESS, null, 0, "Test")
        } returns testProgress

        // When
        val result = repository.createProgress(1L, 1L, ProgressState.IN_PROGRESS, null, 0, "Test")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(testProgress, result.getOrNull())
        verify { studentProgressDao.create(1L, 1L, ProgressState.IN_PROGRESS, null, 0, "Test") }
    }

    @Test
    fun `createProgress returns failure when dao throws exception`() {
        // Given
        every {
            studentProgressDao.create(any(), any(), any(), any(), any(), any())
        } throws RuntimeException("Database error")

        // When
        val result = repository.createProgress(1L, 1L, ProgressState.IN_PROGRESS, null, 0, "Test")

        // Then
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `updateProgress marks completed when status is PASSED`() {
        // Given
        every {
            studentProgressDao.updateStatus(1L, com.gma.tsunjo.school.domain.models.ProgressState.PASSED, 2L, null)
        } returns true

        // When
        val result = repository.updateProgress(1L, com.gma.tsunjo.school.domain.models.ProgressState.PASSED, 2L, null, null)

        // Then
        assertTrue(result)
        verify { studentProgressDao.updateStatus(1L, com.gma.tsunjo.school.domain.models.ProgressState.PASSED, 2L, null) }
    }

    @Test
    fun `updateProgress updates attempts when attempts provided`() {
        // Given
        every {
            studentProgressDao.updateAttempts(1L, 3, "Updated")
        } returns true

        // When
        val result = repository.updateProgress(1L, null, null, 3, "Updated")

        // Then
        assertTrue(result)
        verify { studentProgressDao.updateAttempts(1L, 3, "Updated") }
    }

    @Test
    fun `deleteProgress returns true when dao succeeds`() {
        // Given
        every { studentProgressDao.delete(1L) } returns true

        // When
        val result = repository.deleteProgress(1L)

        // Then
        assertTrue(result)
        verify { studentProgressDao.delete(1L) }
    }
}
