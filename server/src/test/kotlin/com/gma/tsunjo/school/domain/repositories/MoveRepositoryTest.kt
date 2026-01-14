// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.MoveDao
import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.models.Move
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.LocalDateTime
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MoveRepositoryTest {

    private val moveDao = mockk<MoveDao>()
    private val moveRepository = MoveRepository(moveDao)

    private val testMove = Move(
        id = 1L,
        name = "Front Kick",
        description = "Basic front kick technique",
        moveCategoryId = 1L,
        createdAt = LocalDateTime.parse("2025-01-01T00:00:00"),
        updatedAt = LocalDateTime.parse("2025-01-01T00:00:00")
    )

    @Test
    fun `getAllMoves returns all moves from dao`() {
        // Given
        val moves = listOf(testMove)
        every { moveDao.findAll() } returns moves

        // When
        val result = moveRepository.getAllMoves()

        // Then
        assertEquals(moves, result)
        verify { moveDao.findAll() }
    }

    @Test
    fun `getMoveById returns move when found`() {
        // Given
        every { moveDao.findById(1L) } returns testMove

        // When
        val result = moveRepository.getMoveById(1L)

        // Then
        assertEquals(testMove, result)
        verify { moveDao.findById(1L) }
    }

    @Test
    fun `getMoveById returns null when not found`() {
        // Given
        every { moveDao.findById(999L) } returns null

        // When
        val result = moveRepository.getMoveById(999L)

        // Then
        assertEquals(null, result)
        verify { moveDao.findById(999L) }
    }

    @Test
    fun `getMovesByCategory returns moves for category`() {
        // Given
        val moves = listOf(testMove)
        every { moveDao.findByCategory(1L) } returns moves

        // When
        val result = moveRepository.getMovesByCategory(1L)

        // Then
        assertEquals(moves, result)
        verify { moveDao.findByCategory(1L) }
    }

    @Test
    fun `createMove returns success when dao succeeds`() {
        // Given
        every { moveDao.create("Front Kick", "Basic front kick technique") } returns testMove

        // When
        val result = moveRepository.createMove("Front Kick", "Basic front kick technique")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(testMove, result.getOrNull())
        verify { moveDao.create("Front Kick", "Basic front kick technique") }
    }

    @Test
    fun `createMove returns failure when dao throws exception`() {
        // Given
        every { moveDao.create("Front Kick", "Basic front kick technique") } throws RuntimeException("Database error")

        // When
        val result = moveRepository.createMove("Front Kick", "Basic front kick technique")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException.DatabaseError)
        verify { moveDao.create("Front Kick", "Basic front kick technique") }
    }

    @Test
    fun `updateMove returns true when dao succeeds`() {
        // Given
        every { moveDao.update(1L, "Updated Kick", "Updated description", 1L) } returns true

        // When
        val result = moveRepository.updateMove(1L, "Updated Kick", "Updated description", 1L)

        // Then
        assertTrue(result)
        verify { moveDao.update(1L, "Updated Kick", "Updated description", 1L) }
    }

    @Test
    fun `updateMove returns false when dao fails`() {
        // Given
        every { moveDao.update(1L, "Updated Kick", "Updated description", 1L) } returns false

        // When
        val result = moveRepository.updateMove(1L, "Updated Kick", "Updated description", 1L)

        // Then
        assertFalse(result)
        verify { moveDao.update(1L, "Updated Kick", "Updated description", 1L) }
    }

    @Test
    fun `deleteMove returns true when dao succeeds`() {
        // Given
        every { moveDao.delete(1L) } returns true

        // When
        val result = moveRepository.deleteMove(1L)

        // Then
        assertTrue(result)
        verify { moveDao.delete(1L) }
    }

    @Test
    fun `deleteMove returns false when dao fails`() {
        // Given
        every { moveDao.delete(1L) } returns false

        // When
        val result = moveRepository.deleteMove(1L)

        // Then
        assertFalse(result)
        verify { moveDao.delete(1L) }
    }
}
