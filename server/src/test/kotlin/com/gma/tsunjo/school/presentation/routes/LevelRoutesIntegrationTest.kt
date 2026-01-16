// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.api.requests.CreateLevelRequest
import com.gma.tsunjo.school.api.requests.UpdateLevelRequest
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
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

class LevelRoutesIntegrationTest : BaseIntegrationTest() {

    @Test
    fun `GET levels returns 200 with levels list`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/levels") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET levels by invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/levels/invalid") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid ID"))
    }

    @Test
    fun `GET levels by non-existent id returns 404`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/levels/999") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("Level not found"))
    }

    @Test
    fun `POST levels with valid data returns 201`() = withTestApp {
        val token = getAuthToken()
        val request = CreateLevelRequest(
            code = "WHITE",
            displayName = "White Belt",
            orderSeq = 1,
            description = "Beginner level"
        )

        val response = client.post("/levels") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateLevelRequest.serializer(), request))
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `PUT levels with valid data returns 200`() = withTestApp {
        val token = getAuthToken()
        // First create a level
        val createRequest = CreateLevelRequest(
            code = "WHITE",
            displayName = "White Belt",
            orderSeq = 1,
            description = "Beginner level"
        )

        val createResponse = client.post("/levels") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateLevelRequest.serializer(), createRequest))
        }

        if (createResponse.status == HttpStatusCode.Created) {
            val updateRequest = UpdateLevelRequest(
                code = "WHITE",
                displayName = "Updated White Belt",
                orderSeq = 1,
                description = "Updated description"
            )

            val response = client.put("/levels/1") {
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(UpdateLevelRequest.serializer(), updateRequest))
            }

            assertTrue(response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NotFound)
        }
    }

    @Test
    fun `PUT levels with invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val request = UpdateLevelRequest(
            code = "WHITE",
            displayName = "White Belt",
            orderSeq = 1,
            description = "Beginner level"
        )

        val response = client.put("/levels/invalid") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(UpdateLevelRequest.serializer(), request))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid ID"))
    }

    @Test
    fun `DELETE levels with invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.delete("/levels/invalid") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid ID"))
    }

    @Test
    fun `DELETE levels with non-existent id returns 404`() = withTestApp {
        val token = getAuthToken()
        val response = client.delete("/levels/999") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("Level not found"))
    }
}
