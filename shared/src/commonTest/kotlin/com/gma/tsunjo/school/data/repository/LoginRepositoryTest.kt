// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.data.repository

import com.gma.tsunjo.school.api.responses.LoginResponse
import com.gma.tsunjo.school.api.responses.UserInfo
import com.gma.tsunjo.school.data.remote.AuthApi
import com.gma.tsunjo.school.domain.exceptions.AppException
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for LoginRepository using MockEngine for HTTP mocking.
 * 
 * TODO: Revisit with proper KMP mocking library (Mockative/MocKMP) when adding more complex tests.
 *       Current approach uses MockEngine which is multiplatform-compatible but requires
 *       creating real AuthApi instances. For simple HTTP-based repositories this is sufficient,
 *       but for complex business logic, consider using a dedicated mocking framework.
 * 
 * These tests validate:
 * - Result wrapping (Success/Failure)
 * - Exception handling across all platforms
 * - HTTP error mapping to AppException
 */
class LoginRepositoryTest {

    // TODO: Replace with proper mock when using Mockative/MocKMP
    // Currently using MockEngine to create real AuthApi with mocked HTTP responses
    private fun createRepository(mockEngine: MockEngine): LoginRepository {
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
        val authApi = AuthApi(client, "https://test.com")
        return LoginRepository(authApi)
    }

    @Test
    fun `login returns success when API call succeeds`() = runTest {
        val expectedResponse = LoginResponse(
            token = "test-token",
            user = UserInfo(id = 1, username = "test@example.com", isActive = true)
        )

        val mockEngine = MockEngine { _ ->
            respond(
                content = Json.encodeToString(expectedResponse),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = createRepository(mockEngine)
        val result = repository.login("test@example.com", "password")

        assertTrue(result.isSuccess)
        assertEquals(expectedResponse.token, result.getOrNull()?.token)
        assertEquals(expectedResponse.user.id, result.getOrNull()?.user?.id)
    }

    @Test
    fun `login returns failure when API throws InvalidCredentials`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = """{"error": "Invalid credentials"}""",
                status = HttpStatusCode.Unauthorized,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = createRepository(mockEngine)
        val result = repository.login("test@example.com", "wrong-password")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException.InvalidCredentials)
    }

    @Test
    fun `login returns failure when API throws BadRequest`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = """{"error": "Invalid request"}""",
                status = HttpStatusCode.BadRequest,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = createRepository(mockEngine)
        val result = repository.login("", "")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException.BadRequest)
    }

    @Test
    fun `login returns failure when API throws ServerError`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = """{"error": "Server error"}""",
                status = HttpStatusCode.InternalServerError,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = createRepository(mockEngine)
        val result = repository.login("test@example.com", "password")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppException.ServerError)
    }
}
