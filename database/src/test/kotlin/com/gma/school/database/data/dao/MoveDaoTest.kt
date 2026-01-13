// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.MoveCategoriesTable
import com.gma.school.database.data.tables.MovesTable
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

class MoveDaoTest {

    private lateinit var database: Database
    private lateinit var moveDao: MoveDao
    private var categoryId: Long = 0

    @Before
    fun setup() {
        database = Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver"
        )

        transaction {
            SchemaUtils.create(MoveCategoriesTable, MovesTable)

            // Insert test category
            val insertStatement = MoveCategoriesTable.insert {
                it[name] = "General"
                it[description] = "General moves"
            }
            categoryId = insertStatement[MoveCategoriesTable.id].value
        }

        moveDao = MoveDao()
    }

    @After
    fun tearDown() {
        transaction {
            SchemaUtils.drop(MovesTable, MoveCategoriesTable)
        }
    }

    @Test
    fun `should create and find move by id`() {
        val move = moveDao.create("Front Kick", "Basic front kick technique")

        assertNotNull(move)
        assertEquals("Front Kick", move.name)
        assertEquals("Basic front kick technique", move.description)
        assertNotNull(move.createdAt)
        assertNotNull(move.updatedAt)

        val found = moveDao.findById(move.id)
        assertNotNull(found)
        assertEquals(move.id, found.id)
        assertEquals(move.name, found.name)
    }

    @Test
    fun `should find all moves`() {
        moveDao.create("Move 1", "Description 1")
        moveDao.create("Move 2", "Description 2")

        val moves = moveDao.findAll()
        assertEquals(2, moves.size)
    }

    @Test
    fun `should find moves by category`() {
        val move1 = moveDao.create("Move 1", "Description 1")
        moveDao.create("Move 2", "Description 2")

        val movesByCategory = moveDao.findByCategory(move1.moveCategoryId)
        assertEquals(2, movesByCategory.size)
    }

    @Test
    fun `should update move`() {
        val move = moveDao.create("Original Name", "Original description")

        val updated = moveDao.update(move.id, "Updated Name", "Updated description", move.moveCategoryId)
        assertTrue(updated)

        val found = moveDao.findById(move.id)
        assertNotNull(found)
        assertEquals("Updated Name", found.name)
        assertEquals("Updated description", found.description)
    }

    @Test
    fun `should delete move`() {
        val move = moveDao.create("To Delete", "Will be deleted")

        val deleted = moveDao.delete(move.id)
        assertTrue(deleted)

        val found = moveDao.findById(move.id)
        assertNull(found)
    }

    @Test
    fun `should return null for non-existent move`() {
        val found = moveDao.findById(999L)
        assertNull(found)
    }
}
