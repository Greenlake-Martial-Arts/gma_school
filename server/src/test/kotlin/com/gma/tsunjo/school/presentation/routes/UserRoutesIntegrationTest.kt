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
    fun `GET users returns 200 with users list`() = withTestApp {
        val response = client.get("/users")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET users active returns 200 with active users`() = withTestApp {
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
    fun `POST users creates user successfully`() = withTestApp {
        val request = CreateUserRequest("create@example.com", "password123", "Create User")
        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateUserRequest.serializer(), request))
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("create@example.com"))
        assertTrue(response.bodyAsText().contains("Create User"))
    }

    @Test
    fun `POST users with duplicate email returns 409`() = withTestApp {
        val request = CreateUserRequest("duplicate@example.com", "password123", "First User")

        // Create first user
        client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateUserRequest.serializer(), request))
        }

        // Try to create duplicate
        val duplicateRequest = CreateUserRequest("duplicate@example.com", "password456", "Second User")
        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateUserRequest.serializer(), duplicateRequest))
        }
        assertEquals(HttpStatusCode.Conflict, response.status)
        assertTrue(response.bodyAsText().contains("already exists"))
    }

    @Test
    fun `POST users with malformed JSON returns 400`() = withTestApp {
        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody("{invalid json}")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `PUT users with valid data updates user successfully`() = withTestApp {
        // First create a user (will have ID 1)
        val createRequest = CreateUserRequest("update@example.com", "password123", "Original Name")
        client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateUserRequest.serializer(), createRequest))
        }

        // Update the user with ID 1
        val updateRequest = UpdateUserRequest("updated@example.com", "Updated Name", true)
        val response = client.put("/users/1") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(UpdateUserRequest.serializer(), updateRequest))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("updated@example.com"))
        assertTrue(response.bodyAsText().contains("Updated Name"))
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
    fun `PUT users with non-existent id returns 404`() = withTestApp {
        val request = UpdateUserRequest("updated@example.com", "Updated Name", true)
        val response = client.put("/users/999") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(UpdateUserRequest.serializer(), request))
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("User not found"))
    }

    @Test
    fun `PATCH users deactivate with valid id deactivates user successfully`() = withTestApp {
        // First create a user (will have ID 1)
        val createRequest = CreateUserRequest("deactivate@example.com", "password123", "Test User")
        client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateUserRequest.serializer(), createRequest))
        }

        // Deactivate the user with ID 1
        val response = client.patch("/users/1/deactivate")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("User deactivated"))
    }

    @Test
    fun `PATCH users deactivate with invalid id returns 400`() = withTestApp {
        val response = client.patch("/users/invalid/deactivate")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid user ID"))
    }

    @Test
    fun `PATCH users deactivate with non-existent id returns 404`() = withTestApp {
        val response = client.patch("/users/999/deactivate")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("User not found"))
    }

    @Test
    fun `POST auth login with valid credentials returns 200`() = withTestApp {
        // First create a user
        val createRequest = CreateUserRequest("login@example.com", "password123", "Login User")
        client.post("/users") {
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
        val createRequest = CreateUserRequest("wrongpass@example.com", "correctpass", "Test User")
        client.post("/users") {
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
