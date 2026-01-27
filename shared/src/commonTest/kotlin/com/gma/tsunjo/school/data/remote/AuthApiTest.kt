// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.data.remote

import com.gma.tsunjo.school.api.requests.LoginRequest
import com.gma.tsunjo.school.api.responses.LoginResponse
import com.gma.tsunjo.school.api.responses.UserInfo
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
import kotlin.test.fail

class AuthApiTest {

    private fun createAuthApi(mockEngine: MockEngine): AuthApi {
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
        return AuthApi(client, "https://test.com")
    }

    @Test
    fun `login returns LoginResponse on success`() = runTest {
        val expectedResponse = LoginResponse(
            token = "test-token-123",
            user = UserInfo(id = 1, username = "test@example.com", isActive = true)
        )

        val mockEngine = MockEngine { _ ->
            respond(
                content = Json.encodeToString(expectedResponse),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val authApi = createAuthApi(mockEngine)
        val response = authApi.login(LoginRequest("test@example.com", "password"))

        assertEquals(expectedResponse.token, response.token)
        assertEquals(expectedResponse.user.id, response.user.id)
        assertEquals(expectedResponse.user.username, response.user.username)
    }

    @Test
    fun `login throws InvalidCredentials on 401`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = """{"error": "Invalid credentials"}""",
                status = HttpStatusCode.Unauthorized,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val authApi = createAuthApi(mockEngine)

        try {
            authApi.login(LoginRequest("test@example.com", "wrong-password"))
            fail("Expected InvalidCredentials exception")
        } catch (e: AppException.InvalidCredentials) {
            // Expected
            assertTrue(true)
        }
    }

    @Test
    fun `login throws BadRequest on 400`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = """{"error": "Invalid request format"}""",
                status = HttpStatusCode.BadRequest,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val authApi = createAuthApi(mockEngine)

        try {
            authApi.login(LoginRequest("", ""))
            fail("Expected BadRequest exception")
        } catch (e: AppException.BadRequest) {
            // Expected
            assertTrue(true)
        }
    }

    @Test
    fun `login throws ServerError on 500`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = """{"error": "Internal server error"}""",
                status = HttpStatusCode.InternalServerError,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val authApi = createAuthApi(mockEngine)

        try {
            authApi.login(LoginRequest("test@example.com", "password"))
            fail("Expected ServerError exception")
        } catch (e: AppException.ServerError) {
            // Expected
            assertTrue(true)
        }
    }
}
