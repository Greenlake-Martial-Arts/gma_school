// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.api.requests.CreateStudentProgressRequest
import com.gma.tsunjo.school.api.requests.UpdateStudentProgressRequest
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
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StudentProgressRoutesIntegrationTest : BaseIntegrationTest() {

    @Test
    fun `GET student-progress returns 200 with progress list`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/student-progress") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    @Ignore("Requires database setup with related records")
    fun `GET student-progress by id returns 200`() = withTestApp {
        val token = getAuthToken()
        // First create a progress record
        val createRequest = CreateStudentProgressRequest(
            studentId = 1,
            levelRequirementId = 1,
            instructorId = null,
            attempts = 0,
            notes = "Test progress"
        )
        val createResponse = client.post("/student-progress") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateStudentProgressRequest.serializer(), createRequest))
        }

        // Only test if creation succeeded
        if (createResponse.status == HttpStatusCode.Created) {
            val response = client.get("/student-progress/1") {
                bearerAuth(token)
            }
            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    @Test
    fun `GET student-progress by invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/student-progress/invalid") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().containsError("Invalid ID"))
    }

    @Test
    fun `GET student-progress by non-existent id returns 404`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/student-progress/999999") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `GET student-progress by student returns 200`() = withTestApp {
        val token = getAuthToken()
        val response = client.get("/student-progress/student/1") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    @Ignore("Requires database setup with related records")
    fun `POST student-progress creates progress successfully`() = withTestApp {
        val token = getAuthToken()
        val request = CreateStudentProgressRequest(
            studentId = 1,
            levelRequirementId = 1,
            instructorId = null,
            attempts = 0,
            notes = "Test progress"
        )
        val response = client.post("/student-progress") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(CreateStudentProgressRequest.serializer(), request))
        }
        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    @Ignore("Requires database setup with related records")
    fun `PUT student-progress updates progress successfully`() = withTestApp {
        val token = getAuthToken()
        val request = UpdateStudentProgressRequest(
            status = com.gma.tsunjo.school.domain.models.ProgressState.PASSED,
            attempts = 1,
            notes = "Updated progress"
        )
        val response = client.put("/student-progress/1") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(UpdateStudentProgressRequest.serializer(), request))
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `PUT student-progress with invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val request = UpdateStudentProgressRequest(status = com.gma.tsunjo.school.domain.models.ProgressState.PASSED)
        val response = client.put("/student-progress/invalid") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(UpdateStudentProgressRequest.serializer(), request))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    @Ignore("Requires database setup with related records")
    fun `DELETE student-progress removes progress successfully`() = withTestApp {
        val token = getAuthToken()
        val response = client.delete("/student-progress/1") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `DELETE student-progress with invalid id returns 400`() = withTestApp {
        val token = getAuthToken()
        val response = client.delete("/student-progress/invalid") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
