// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.api.requests.CreateUserRequest
import com.gma.tsunjo.school.api.requests.LoginRequest
import com.gma.tsunjo.school.api.requests.UpdateUserRequest
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserRoutesIntegrationTest : BaseIntegrationTest() {

    @Test
    fun `GET users returns 200`() = withTestApp {
        val response = client.get("/users")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET users active returns 200`() = withTestApp {
        val response = client.get("/users/active")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET users by invalid id returns 400`() = withTestApp {
        val response = client.get("/users/invalid")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid user ID"))
    }

    @Test
    fun `GET users by non-existent id returns 404`() = withTestApp {
        val response = client.get("/users/999")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("User not found"))
    }

    @Test
    fun `POST users with valid data returns 201 or 409`() = withTestApp {
        val request = CreateUserRequest("test@example.com", "password", "Test User")
        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateUserRequest.serializer(), request))
        }
        // Either created successfully (201) or user already exists (409)
        assertTrue(response.status == HttpStatusCode.Created || response.status == HttpStatusCode.Conflict)
    }

    @Test
    fun `PUT users with invalid id returns 400`() = withTestApp {
        val request = UpdateUserRequest("updated@example.com", "Updated Name", true)
        val response = client.put("/users/invalid") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(UpdateUserRequest.serializer(), request))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid user ID"))
    }

    @Test
    fun `PATCH users deactivate with invalid id returns 400`() = withTestApp {
        val response = client.patch("/users/invalid/deactivate")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid user ID"))
    }

    @Test
    fun `POST auth login with invalid credentials returns 401`() = withTestApp {
        val request = LoginRequest("nonexistent@example.com", "wrongpassword")
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(LoginRequest.serializer(), request))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertTrue(response.bodyAsText().contains("Invalid credentials"))
    }
}
