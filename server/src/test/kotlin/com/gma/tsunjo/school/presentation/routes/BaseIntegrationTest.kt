// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.school.database.data.tables.AttendanceEntriesTable
import com.gma.school.database.data.tables.AttendancesTable
import com.gma.school.database.data.tables.AuditLogTable
import com.gma.school.database.data.tables.LevelRequirementsTable
import com.gma.school.database.data.tables.LevelsTable
import com.gma.school.database.data.tables.MemberTypesTable
import com.gma.school.database.data.tables.MoveCategoriesTable
import com.gma.school.database.data.tables.MovesTable
import com.gma.school.database.data.tables.RolesTable
import com.gma.school.database.data.tables.StudentLevelsTable
import com.gma.school.database.data.tables.StudentProgressTable
import com.gma.school.database.data.tables.StudentsTable
import com.gma.school.database.data.tables.UserRolesTable
import com.gma.school.database.data.tables.UsersTable
import com.gma.tsunjo.school.api.requests.LoginRequest
import com.gma.tsunjo.school.config.configureJwtAuthentication
import com.gma.tsunjo.school.configurePlugins
import com.gma.tsunjo.school.configureRouting
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.application.Application
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

abstract class BaseIntegrationTest {

    companion object {
        // Shared test user credentials
        const val TEST_USER_EMAIL = "test@example.com"
        const val TEST_USER_PASSWORD = "testpass123"
    }

    // Cache for auth token - will be set once per test class
    private var cachedAuthToken: String? = null

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
            SchemaUtils.createMissingTablesAndColumns(
                // Lookup tables first
                RolesTable,
                MemberTypesTable,
                MoveCategoriesTable,

                // Core entities
                UsersTable,
                UserRolesTable,
                StudentsTable,
                LevelsTable,
                MovesTable,

                // Relationship tables
                LevelRequirementsTable,
                StudentLevelsTable,
                AttendancesTable,
                AttendanceEntriesTable,
                StudentProgressTable,
                AuditLogTable
            )

            // Insert default STUDENT role if it doesn't exist
            if (RolesTable.selectAll().count() == 0L) {
                RolesTable.insert {
                    it[name] = "STUDENT"
                }
            }

            // Pre-populate test user for authentication
            val existingUser = UsersTable.select { UsersTable.username eq TEST_USER_EMAIL }.singleOrNull()
            if (existingUser == null) {
                val now = kotlinx.datetime.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.UTC)
                val insertStatement = UsersTable.insert {
                    it[UsersTable.username] = TEST_USER_EMAIL
                    it[UsersTable.passwordHash] = java.util.Base64.getEncoder().encodeToString(TEST_USER_PASSWORD.toByteArray())
                    it[UsersTable.isActive] = true
                    it[UsersTable.createdAt] = now
                    it[UsersTable.updatedAt] = now
                }
                val userId = insertStatement[UsersTable.id].value

                // Assign STUDENT role to test user
                val studentRoleId = RolesTable.select { RolesTable.name eq "STUDENT" }
                    .map { it[RolesTable.id].value }
                    .first()

                UserRolesTable.insert {
                    it[UserRolesTable.userId] = userId
                    it[UserRolesTable.roleId] = studentRoleId
                }
            }
        }

        configurePlugins()
        configureJwtAuthentication()
        configureRouting()
    }

    // Helper function to run tests with the test application
    protected fun withTestApp(test: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            testModule()
        }
        test()
    }

    // Helper function to get JWT token for authenticated requests
    // Token is cached and reused across tests in the same test class
    // Uses pre-populated test user from database
    protected suspend fun ApplicationTestBuilder.getAuthToken(): String {
        // Return cached token if available
        cachedAuthToken?.let { return it }

        // Login with pre-populated test user
        val loginResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                Json.encodeToString(
                    LoginRequest.serializer(),
                    LoginRequest(TEST_USER_EMAIL, TEST_USER_PASSWORD)
                )
            )
        }

        val responseBody = loginResponse.bodyAsText()
        val jsonElement = Json.parseToJsonElement(responseBody)
        val token = jsonElement.jsonObject["token"]?.jsonPrimitive?.content
            ?: throw IllegalStateException("Failed to get auth token")

        // Cache the token for reuse
        cachedAuthToken = token
        return token
    }

    // Helper function to check if error response contains expected message
    protected fun String.containsError(message: String): Boolean {
        val json = Json.parseToJsonElement(this).jsonObject
        return json["error"]?.jsonPrimitive?.content?.contains(message) == true
    }
}
