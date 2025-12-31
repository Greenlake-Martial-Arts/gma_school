// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.school.database.data.tables.RolesTable
import com.gma.school.database.data.tables.StudentsTable
import com.gma.school.database.data.tables.UserRolesTable
import com.gma.school.database.data.tables.UsersTable
import com.gma.tsunjo.school.configurePlugins
import com.gma.tsunjo.school.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

abstract class BaseIntegrationTest {

    /**
     * Test application module that configures plugins and routing
     * without database initialization to avoid MySQL connection in tests
     */
    fun Application.testModule() {

        // Setup H2 in-memory database for tests (instead of MySQL)
        val testDatabase = Database.connect(
            url = "jdbc:h2:mem:test_${System.nanoTime()};DB_CLOSE_DELAY=-1;MODE=MySQL",
            driver = "org.h2.Driver",
            user = "sa",
            password = ""
        )

        // Create all required tables in correct order (dependencies first)
        transaction(testDatabase) {
            SchemaUtils.createMissingTablesAndColumns(StudentsTable, RolesTable, UsersTable, UserRolesTable)

            // Insert default STUDENT role if it doesn't exist
            if (RolesTable.selectAll().count() == 0L) {
                RolesTable.insert {
                    it[name] = "STUDENT"
                }
            }
        }

        configurePlugins()
        configureRouting()
    }

    // Helper function to run tests with the test application
    protected fun withTestApp(test: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            testModule()
        }
        test()
    }
}
