// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.api.requests.CreateMoveRequest
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
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

class MoveRoutesIntegrationTest : BaseIntegrationTest() {

    @Test
    fun `GET moves returns 200 with moves list`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/moves") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET moves by invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/moves/invalid") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().containsError("Invalid ID"))
    }

    @Test
    fun `GET moves by non-existent id returns 404`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/moves/999") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().containsError("Move not found"))
    }

    @Test
    fun `GET moves by category returns 200`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/moves/category/1") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET moves by invalid category id returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/moves/category/invalid") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().containsError("Invalid category ID"))
    }

    @Test
    fun `POST moves with valid data returns 201`() = withTestApp {
        val token = getAuthToken()
        val request = CreateMoveRequest(
            name = "Front Kick",
            description = "Basic front kick technique"
        )

        val response = client.post("/moves") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateMoveRequest.serializer(), request))
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `DELETE moves with invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.delete("/moves/invalid") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().containsError("Invalid ID"))
    }

    @Test
    fun `DELETE moves with non-existent id returns 404`() = withTestApp {
        val token = getAuthToken()
        val response = client.delete("/moves/999") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().containsError("Move not found"))
    }
}
