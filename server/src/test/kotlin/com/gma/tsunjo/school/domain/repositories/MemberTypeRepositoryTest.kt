// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.MemberTypeDao
import com.gma.tsunjo.school.domain.models.MemberType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import kotlin.test.assertEquals

class MemberTypeRepositoryTest {

    private val memberTypeDao = mockk<MemberTypeDao>()
    private val memberTypeRepository = MemberTypeRepository(memberTypeDao)

    private val testMemberType = MemberType(
        id = 1L,
        name = "Regular"
    )

    @Test
    fun `getAllMemberTypes returns all member types from dao`() {
        // Given
        val memberTypes = listOf(testMemberType)
        every { memberTypeDao.findAll() } returns memberTypes

        // When
        val result = memberTypeRepository.getAllMemberTypes()

        // Then
        assertEquals(memberTypes, result)
        verify { memberTypeDao.findAll() }
    }

    @Test
    fun `getMemberTypeById returns member type when found`() {
        // Given
        every { memberTypeDao.findById(1L) } returns testMemberType

        // When
        val result = memberTypeRepository.getMemberTypeById(1L)

        // Then
        assertEquals(testMemberType, result)
        verify { memberTypeDao.findById(1L) }
    }

    @Test
    fun `getMemberTypeById returns null when not found`() {
        // Given
        every { memberTypeDao.findById(999L) } returns null

        // When
        val result = memberTypeRepository.getMemberTypeById(999L)

        // Then
        assertEquals(null, result)
        verify { memberTypeDao.findById(999L) }
    }

    @Test
    fun `getMemberTypeByName returns member type when found`() {
        // Given
        every { memberTypeDao.findByName("Regular") } returns testMemberType

        // When
        val result = memberTypeRepository.getMemberTypeByName("Regular")

        // Then
        assertEquals(testMemberType, result)
        verify { memberTypeDao.findByName("Regular") }
    }

    @Test
    fun `getMemberTypeByName returns null when not found`() {
        // Given
        every { memberTypeDao.findByName("NonExistent") } returns null

        // When
        val result = memberTypeRepository.getMemberTypeByName("NonExistent")

        // Then
        assertEquals(null, result)
        verify { memberTypeDao.findByName("NonExistent") }
    }
}
