// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.MoveCategoryDao
import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.models.MoveCategory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MoveCategoryRepositoryTest {

    private val moveCategoryDao = mockk<MoveCategoryDao>()
    private val moveCategoryRepository = MoveCategoryRepository(moveCategoryDao)

    private val testMoveCategory = MoveCategory(
        id = 1L,
        name = "Kicks",
        description = "Kicking techniques"
    )

    @Test
    fun `getAllMoveCategories returns all categories from dao`() {
        // Given
        val categories = listOf(testMoveCategory)
        every { moveCategoryDao.findAll() } returns categories

        // When
        val result = moveCategoryRepository.getAllMoveCategories()

        // Then
        assertEquals(categories, result)
        verify { moveCategoryDao.findAll() }
    }

    @Test
    fun `getMoveCategoryById returns category when found`() {
        // Given
        every { moveCategoryDao.findById(1L) } returns testMoveCategory

        // When
        val result = moveCategoryRepository.getMoveCategoryById(1L)

        // Then
        assertEquals(testMoveCategory, result)
        verify { moveCategoryDao.findById(1L) }
    }

    @Test
    fun `getMoveCategoryById returns null when not found`() {
        // Given
        every { moveCategoryDao.findById(999L) } returns null

        // When
        val result = moveCategoryRepository.getMoveCategoryById(999L)

        // Then
        assertEquals(null, result)
        verify { moveCategoryDao.findById(999L) }
    }

    @Test
    fun `createMoveCategory returns success when dao succeeds`() {
        // Given
        every { moveCategoryDao.create("Kicks", "Kicking techniques") } returns testMoveCategory

        // When
        val result = moveCategoryRepository.createMoveCategory("Kicks", "Kicking techniques")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(testMoveCategory, result.getOrNull())
        verify { moveCategoryDao.create("Kicks", "Kicking techniques") }
    }

    @Test
    fun `createMoveCategory returns failure when dao throws exception`() {
        // Given
        every { moveCategoryDao.create("Kicks", "Kicking techniques") } throws RuntimeException("Database error")

        // When
        val result = moveCategoryRepository.createMoveCategory("Kicks", "Kicking techniques")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException.DatabaseError)
        verify { moveCategoryDao.create("Kicks", "Kicking techniques") }
    }

    @Test
    fun `updateMoveCategory returns true when dao succeeds`() {
        // Given
        every { moveCategoryDao.update(1L, "Updated Kicks", "Updated description") } returns true

        // When
        val result = moveCategoryRepository.updateMoveCategory(1L, "Updated Kicks", "Updated description")

        // Then
        assertTrue(result)
        verify { moveCategoryDao.update(1L, "Updated Kicks", "Updated description") }
    }

    @Test
    fun `updateMoveCategory returns false when dao fails`() {
        // Given
        every { moveCategoryDao.update(1L, "Updated Kicks", "Updated description") } returns false

        // When
        val result = moveCategoryRepository.updateMoveCategory(1L, "Updated Kicks", "Updated description")

        // Then
        assertFalse(result)
        verify { moveCategoryDao.update(1L, "Updated Kicks", "Updated description") }
    }

    @Test
    fun `deleteMoveCategory returns true when dao succeeds`() {
        // Given
        every { moveCategoryDao.delete(1L) } returns true

        // When
        val result = moveCategoryRepository.deleteMoveCategory(1L)

        // Then
        assertTrue(result)
        verify { moveCategoryDao.delete(1L) }
    }

    @Test
    fun `deleteMoveCategory returns false when dao fails`() {
        // Given
        every { moveCategoryDao.delete(1L) } returns false

        // When
        val result = moveCategoryRepository.deleteMoveCategory(1L)

        // Then
        assertFalse(result)
        verify { moveCategoryDao.delete(1L) }
    }
}
