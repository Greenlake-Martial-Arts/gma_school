// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.api.requests.CreateUserRequest
import com.gma.tsunjo.school.api.requests.LoginRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthRoutesIntegrationTest : BaseIntegrationTest() {

    @Test
    fun `POST auth login with valid credentials returns 200`() = withTestApp {
        // First create a user
        val createRequest = CreateUserRequest("login@example.com", "password123")
        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateUserRequest.serializer(), createRequest))
        }

        // Login with correct credentials
        val loginRequest = LoginRequest("login@example.com", "password123")
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(LoginRequest.serializer(), loginRequest))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("login@example.com"))
    }

    @Test
    fun `POST auth login with invalid email returns 401`() = withTestApp {
        val request = LoginRequest("nonexistent@example.com", "password123")
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(LoginRequest.serializer(), request))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertTrue(response.bodyAsText().contains("Invalid credentials"))
    }

    @Test
    fun `POST auth login with wrong password returns 401`() = withTestApp {
        // First create a user
        val createRequest = CreateUserRequest("wrongpass@example.com", "correctpass")
        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateUserRequest.serializer(), createRequest))
        }

        // Login with wrong password
        val loginRequest = LoginRequest("wrongpass@example.com", "wrongpass")
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(LoginRequest.serializer(), loginRequest))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertTrue(response.bodyAsText().contains("Invalid credentials"))
    }

    @Test
    fun `POST auth login with malformed JSON returns 400`() = withTestApp {
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("{invalid json}")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}