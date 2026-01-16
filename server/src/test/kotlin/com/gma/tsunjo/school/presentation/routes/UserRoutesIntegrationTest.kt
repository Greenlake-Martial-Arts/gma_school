// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.api.requests.CreateUserRequest
import com.gma.tsunjo.school.api.requests.UpdateUserRequest
import io.ktor.client.request.bearerAuth
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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserRoutesIntegrationTest : BaseIntegrationTest() {

    @Test
    fun `GET users returns 200 with users list`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/users") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET users active returns 200 with active users`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/users/active") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET users by invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/users/invalid") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid user ID"))
    }

    @Test
    fun `GET users by non-existent id returns 404`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/users/999") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("User not found"))
    }

    @Test
    fun `POST auth register creates user successfully`() = withTestApp {
        val request = CreateUserRequest("create@example.com", "password123")
        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateUserRequest.serializer(), request))
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("create@example.com"))
    }

    @Test
    fun `POST auth register with duplicate email returns 409`() = withTestApp {
        val request = CreateUserRequest("duplicate@example.com", "password123")

        // Create first user
        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateUserRequest.serializer(), request))
        }

        // Try to create duplicate
        val duplicateRequest = CreateUserRequest("duplicate@example.com", "password456")
        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateUserRequest.serializer(), duplicateRequest))
        }
        assertEquals(HttpStatusCode.Conflict, response.status)
        assertTrue(response.bodyAsText().contains("already exists"))
    }

    @Test
    fun `POST auth register with malformed JSON returns 400`() = withTestApp {
        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("{invalid json}")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `PUT users with valid data updates user successfully`() = withTestApp {
        val token = getAuthToken()

        // First create a user (will have ID 2, since test user is ID 1)
        val createRequest = CreateUserRequest("update@example.com", "password123")
        val createResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateUserRequest.serializer(), createRequest))
        }

        // Extract user ID from response
        val responseJson = Json.parseToJsonElement(createResponse.bodyAsText()).jsonObject
        val userId = responseJson["id"]?.jsonPrimitive?.content ?: "2"

        // Update the user
        val updateRequest = UpdateUserRequest("updated@example.com", true)
        val response = client.put("/users/$userId") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(UpdateUserRequest.serializer(), updateRequest))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("updated@example.com"))
    }

    @Test
    fun `PUT users with invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val request = UpdateUserRequest("updated@example.com", true)
        val response = client.put("/users/invalid") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(UpdateUserRequest.serializer(), request))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid user ID"))
    }

    @Test
    fun `PUT users with non-existent id returns 404`() = withTestApp {
        val token = getAuthToken()
        val request = UpdateUserRequest("updated@example.com", true)
        val response = client.put("/users/999") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(UpdateUserRequest.serializer(), request))
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("User not found"))
    }

    @Test
    fun `PATCH users deactivate with valid id deactivates user successfully`() = withTestApp {
        val token = getAuthToken()

        // First create a user
        val createRequest = CreateUserRequest("deactivate@example.com", "password123")
        val createResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateUserRequest.serializer(), createRequest))
        }

        // Extract user ID from response
        val responseJson = Json.parseToJsonElement(createResponse.bodyAsText()).jsonObject
        val userId = responseJson["id"]?.jsonPrimitive?.content ?: "2"

        // Deactivate the user
        val response = client.patch("/users/$userId/deactivate") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("User deactivated"))
    }

    @Test
    fun `PATCH users deactivate with invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.patch("/users/invalid/deactivate") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid user ID"))
    }

    @Test
    fun `PATCH users deactivate with non-existent id returns 404`() = withTestApp {
        val token = getAuthToken()
        val response = client.patch("/users/999/deactivate") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("User not found"))
    }
}
