// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.LevelsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LevelDaoTest {

    private lateinit var database: Database
    private lateinit var levelDao: LevelDao

    @Before
    fun setup() {
        database = Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver"
        )

        transaction {
            SchemaUtils.create(LevelsTable)
        }

        levelDao = LevelDao()
    }

    @After
    fun tearDown() {
        transaction {
            SchemaUtils.drop(LevelsTable)
        }
    }

    @Test
    fun `should create and find level by id`() {
        val level = levelDao.create("WHITE", "White Belt", 1, "Beginning level")

        assertNotNull(level)
        assertEquals("WHITE", level.code)
        assertEquals("White Belt", level.displayName)
        assertEquals(1, level.orderSeq)
        assertEquals("Beginning level", level.description)
        assertNotNull(level.createdAt)
        assertNotNull(level.updatedAt)

        val found = levelDao.findById(level.id)
        assertNotNull(found)
        assertEquals(level.id, found.id)
        assertEquals(level.code, found.code)
    }

    @Test
    fun `should find all levels`() {
        levelDao.create("WHITE", "White Belt", 1, "First level")
        levelDao.create("YELLOW", "Yellow Belt", 2, "Second level")
        levelDao.create("ORANGE", "Orange Belt", 3, "Third level")

        val levels = levelDao.findAll()
        assertEquals(3, levels.size)
    }

    @Test
    fun `should update level`() {
        val level = levelDao.create("WHITE", "White Belt", 1, "Original description")

        val updated = levelDao.update(
            level.id,
            "WHITE_UPDATED",
            "Updated White Belt",
            10,
            "Updated description"
        )
        assertTrue(updated)

        val found = levelDao.findById(level.id)
        assertNotNull(found)
        assertEquals("WHITE_UPDATED", found.code)
        assertEquals("Updated White Belt", found.displayName)
        assertEquals(10, found.orderSeq)
        assertEquals("Updated description", found.description)
    }

    @Test
    fun `should delete level`() {
        val level = levelDao.create("TO_DELETE", "To Delete", 99, "Will be deleted")

        val deleted = levelDao.delete(level.id)
        assertTrue(deleted)

        val found = levelDao.findById(level.id)
        assertNull(found)
    }

    @Test
    fun `should return null for non-existent level by id`() {
        val found = levelDao.findById(999L)
        assertNull(found)
    }

    @Test
    fun `should handle duplicate code constraint`() {
        levelDao.create("DUPLICATE", "First", 1, "First level")

        // Test that duplicate code is handled (should fail)
        var exceptionThrown = false
        try {
            levelDao.create("DUPLICATE", "Second", 2, "Second level")
        } catch (e: Exception) {
            exceptionThrown = true
        }

        // Either an exception was thrown or the duplicate was silently ignored
        // Both are acceptable behaviors for this test
        assertTrue(
            exceptionThrown || levelDao.findAll().size == 1,
            "Duplicate handling should either throw exception or ignore"
        )
    }

    @Test
    fun `should create level with null description`() {
        val level = levelDao.create("NULL_DESC", "No Description", 5, null)

        assertNotNull(level)
        assertEquals("NULL_DESC", level.code)
        assertNull(level.description)
    }
}
