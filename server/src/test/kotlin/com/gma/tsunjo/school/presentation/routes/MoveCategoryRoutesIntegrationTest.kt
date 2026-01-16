// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.api.requests.CreateMoveCategoryRequest
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

class MoveCategoryRoutesIntegrationTest : BaseIntegrationTest() {

    @Test
    fun `GET move-categories returns 200 with categories list`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/move-categories") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET move-categories by invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/move-categories/invalid") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid ID"))
    }

    @Test
    fun `GET move-categories by non-existent id returns 404`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/move-categories/999") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("Move category not found"))
    }

    @Test
    fun `POST move-categories with valid data returns 201`() = withTestApp {
        val token = getAuthToken()
        val request = CreateMoveCategoryRequest(
            name = "Kicks",
            description = "Kicking techniques"
        )

        val response = client.post("/move-categories") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateMoveCategoryRequest.serializer(), request))
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `PUT move-categories with valid data returns 200 or 404`() = withTestApp {
        val token = getAuthToken()
        val request = CreateMoveCategoryRequest(
            name = "Updated Kicks",
            description = "Updated description"
        )

        val response = client.put("/move-categories/1") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateMoveCategoryRequest.serializer(), request))
        }

        assertTrue(response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NotFound)
    }

    @Test
    fun `PUT move-categories with invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val request = CreateMoveCategoryRequest(
            name = "Kicks",
            description = "Kicking techniques"
        )

        val response = client.put("/move-categories/invalid") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateMoveCategoryRequest.serializer(), request))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid ID"))
    }

    @Test
    fun `DELETE move-categories with invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.delete("/move-categories/invalid") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid ID"))
    }

    @Test
    fun `DELETE move-categories with non-existent id returns 404`() = withTestApp {
        val token = getAuthToken()
        val response = client.delete("/move-categories/999") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("Move category not found"))
    }
}
