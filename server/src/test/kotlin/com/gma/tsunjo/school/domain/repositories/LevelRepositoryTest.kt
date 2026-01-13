// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.LevelDao
import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.models.Level
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.LocalDateTime
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LevelRepositoryTest {

    private val levelDao = mockk<LevelDao>()
    private val levelRepository = LevelRepository(levelDao)

    private val testLevel = Level(
        id = 1L,
        code = "WHITE",
        displayName = "White Belt",
        orderSeq = 1,
        description = "Beginner level",
        createdAt = LocalDateTime.parse("2025-01-01T00:00:00"),
        updatedAt = LocalDateTime.parse("2025-01-01T00:00:00")
    )

    @Test
    fun `getAllLevels returns all levels from dao`() {
        // Given
        val levels = listOf(testLevel)
        every { levelDao.findAll() } returns levels

        // When
        val result = levelRepository.getAllLevels()

        // Then
        assertEquals(levels, result)
        verify { levelDao.findAll() }
    }

    @Test
    fun `getLevelById returns level when found`() {
        // Given
        every { levelDao.findById(1L) } returns testLevel

        // When
        val result = levelRepository.getLevelById(1L)

        // Then
        assertEquals(testLevel, result)
        verify { levelDao.findById(1L) }
    }

    @Test
    fun `getLevelById returns null when not found`() {
        // Given
        every { levelDao.findById(999L) } returns null

        // When
        val result = levelRepository.getLevelById(999L)

        // Then
        assertEquals(null, result)
        verify { levelDao.findById(999L) }
    }

    @Test
    fun `createLevel returns success when dao succeeds`() {
        // Given
        every { levelDao.create("WHITE", "White Belt", 1, "Beginner level") } returns testLevel

        // When
        val result = levelRepository.createLevel("WHITE", "White Belt", 1, "Beginner level")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(testLevel, result.getOrNull())
        verify { levelDao.create("WHITE", "White Belt", 1, "Beginner level") }
    }

    @Test
    fun `createLevel returns failure when dao throws exception`() {
        // Given
        every { levelDao.create("WHITE", "White Belt", 1, "Beginner level") } throws RuntimeException("Database error")

        // When
        val result = levelRepository.createLevel("WHITE", "White Belt", 1, "Beginner level")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException.DatabaseError)
        verify { levelDao.create("WHITE", "White Belt", 1, "Beginner level") }
    }

    @Test
    fun `updateLevel returns true when dao succeeds`() {
        // Given
        every { levelDao.update(1L, "WHITE", "White Belt", 1, "Updated description") } returns true

        // When
        val result = levelRepository.updateLevel(1L, "WHITE", "White Belt", 1, "Updated description")

        // Then
        assertTrue(result)
        verify { levelDao.update(1L, "WHITE", "White Belt", 1, "Updated description") }
    }

    @Test
    fun `updateLevel returns false when dao fails`() {
        // Given
        every { levelDao.update(1L, "WHITE", "White Belt", 1, "Updated description") } returns false

        // When
        val result = levelRepository.updateLevel(1L, "WHITE", "White Belt", 1, "Updated description")

        // Then
        assertFalse(result)
        verify { levelDao.update(1L, "WHITE", "White Belt", 1, "Updated description") }
    }

    @Test
    fun `deleteLevel returns true when dao succeeds`() {
        // Given
        every { levelDao.delete(1L) } returns true

        // When
        val result = levelRepository.deleteLevel(1L)

        // Then
        assertTrue(result)
        verify { levelDao.delete(1L) }
    }

    @Test
    fun `deleteLevel returns false when dao fails`() {
        // Given
        every { levelDao.delete(1L) } returns false

        // When
        val result = levelRepository.deleteLevel(1L)

        // Then
        assertFalse(result)
        verify { levelDao.delete(1L) }
    }
}
