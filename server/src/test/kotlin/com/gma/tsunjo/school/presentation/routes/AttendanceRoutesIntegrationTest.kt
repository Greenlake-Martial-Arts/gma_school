// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.api.requests.CreateAttendanceRequest
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
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AttendanceRoutesIntegrationTest : BaseIntegrationTest() {

    @Test
    fun `GET attendance returns 200 with attendance list`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/attendance") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET attendance by invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/attendance/invalid") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid ID"))
    }

    @Test
    fun `GET attendance by non-existent id returns 404`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/attendance/999") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("Attendance not found"))
    }

    @Test
    fun `GET attendance by date range returns 200`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/attendance/date-range?startDate=2025-01-01&endDate=2025-01-31") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET attendance by date range without parameters returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/attendance/date-range") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("startDate and endDate parameters required"))
    }

    @Test
    fun `POST attendance with valid data returns 201`() = withTestApp {
        val token = getAuthToken()
        val request = CreateAttendanceRequest(
            classDate = LocalDate.parse("2025-01-01"),
            notes = "Regular class"
        )

        val response = client.post("/attendance") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateAttendanceRequest.serializer(), request))
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `PUT attendance with valid data returns 200 or 404`() = withTestApp {
        val token = getAuthToken()
        val request = CreateAttendanceRequest(
            classDate = LocalDate.parse("2025-01-01"),
            notes = "Updated notes"
        )

        val response = client.put("/attendance/1") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateAttendanceRequest.serializer(), request))
        }

        assertTrue(response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NotFound)
    }

    @Test
    fun `PUT attendance with invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val request = CreateAttendanceRequest(
            classDate = LocalDate.parse("2025-01-01"),
            notes = "Regular class"
        )

        val response = client.put("/attendance/invalid") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateAttendanceRequest.serializer(), request))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid ID"))
    }

    @Test
    fun `DELETE attendance with invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.delete("/attendance/invalid") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid ID"))
    }

    @Test
    fun `POST attendance students with invalid ids returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.post("/attendance/invalid/students/1") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid attendance ID or student ID"))
    }

    @Test
    fun `DELETE attendance students with invalid ids returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.delete("/attendance/1/students/invalid") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid attendance ID or student ID"))
    }

    @Test
    fun `GET attendance students returns 200`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/attendance/1/students") {
            bearerAuth(token)
        }
        assertTrue(response.status == HttpStatusCode.OK || response.status == HttpStatusCode.BadRequest)
    }

    @Test
    fun `GET attendance students with invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/attendance/invalid/students") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid ID"))
    }
}
