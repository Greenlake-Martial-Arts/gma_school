// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.data.remote

import com.gma.tsunjo.school.domain.exceptions.AppException
import io.ktor.http.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HttpErrorMapperTest {

    @Test
    fun `mapError returns InvalidCredentials for 401`() {
        val exception = HttpErrorMapper.mapError(HttpStatusCode.Unauthorized)
        assertTrue(exception is AppException.InvalidCredentials)
    }

    @Test
    fun `mapError returns Unauthorized for 403`() {
        val exception = HttpErrorMapper.mapError(HttpStatusCode.Forbidden)
        assertTrue(exception is AppException.Unauthorized)
    }

    @Test
    fun `mapError returns BadRequest for 400`() {
        val exception = HttpErrorMapper.mapError(HttpStatusCode.BadRequest)
        assertTrue(exception is AppException.BadRequest)
    }

    @Test
    fun `mapError returns ValidationError for 404`() {
        val exception = HttpErrorMapper.mapError(HttpStatusCode.NotFound)
        assertTrue(exception is AppException.ValidationError)
    }

    @Test
    fun `mapError returns ServerError for 500`() {
        val exception = HttpErrorMapper.mapError(HttpStatusCode.InternalServerError)
        assertTrue(exception is AppException.ServerError)
    }

    @Test
    fun `mapError returns ServerError for 502`() {
        val exception = HttpErrorMapper.mapError(HttpStatusCode.BadGateway)
        assertTrue(exception is AppException.ServerError)
    }

    @Test
    fun `mapError returns ServerError for 503`() {
        val exception = HttpErrorMapper.mapError(HttpStatusCode.ServiceUnavailable)
        assertTrue(exception is AppException.ServerError)
    }

    @Test
    fun `mapError returns Unknown for unmapped status codes`() {
        val exception = HttpErrorMapper.mapError(HttpStatusCode.Continue) // 100
        assertTrue(exception is AppException.Unknown)
        assertEquals("HTTP 100: Continue", exception.message)
    }
}
