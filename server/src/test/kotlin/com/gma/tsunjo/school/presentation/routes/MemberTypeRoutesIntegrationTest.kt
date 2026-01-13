// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MemberTypeRoutesIntegrationTest : BaseIntegrationTest() {

    @Test
    fun `GET member-types returns 200 with member types list`() = withTestApp {
        val response = client.get("/member-types")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET member-types by invalid id returns 400`() = withTestApp {
        val response = client.get("/member-types/invalid")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid ID"))
    }

    @Test
    fun `GET member-types by non-existent id returns 404`() = withTestApp {
        val response = client.get("/member-types/999")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("Member type not found"))
    }
}
