// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.exceptions

import com.gma.tsunjo.school.resources.Strings
import kotlin.test.Test
import kotlin.test.assertEquals

class UiErrorMapperTest {

    @Test
    fun `toMessage returns correct message for InvalidCredentials`() {
        val exception = AppException.InvalidCredentials()
        assertEquals(Strings.ERROR_INVALID_CREDENTIALS, UiErrorMapper.toMessage(exception))
    }

    @Test
    fun `toMessage returns correct message for NetworkError`() {
        val exception = AppException.NetworkError()
        assertEquals(Strings.ERROR_NETWORK, UiErrorMapper.toMessage(exception))
    }

    @Test
    fun `toMessage returns correct message for Timeout`() {
        val exception = AppException.Timeout()
        assertEquals(Strings.ERROR_TIMEOUT, UiErrorMapper.toMessage(exception))
    }

    @Test
    fun `toMessage returns correct message for ServerError`() {
        val exception = AppException.ServerError()
        assertEquals(Strings.ERROR_SERVER, UiErrorMapper.toMessage(exception))
    }

    @Test
    fun `toMessage returns correct message for Unauthorized`() {
        val exception = AppException.Unauthorized()
        assertEquals(Strings.ERROR_UNAUTHORIZED, UiErrorMapper.toMessage(exception))
    }

    @Test
    fun `toMessage returns correct message for SessionExpired`() {
        val exception = AppException.SessionExpired()
        assertEquals(Strings.ERROR_SESSION_EXPIRED, UiErrorMapper.toMessage(exception))
    }

    @Test
    fun `toMessage returns exception message for ValidationError`() {
        val message = "Email is required"
        val exception = AppException.ValidationError(message)
        assertEquals(message, UiErrorMapper.toMessage(exception))
    }

    @Test
    fun `toMessage returns exception message for BadRequest`() {
        val message = "Invalid format"
        val exception = AppException.BadRequest(message)
        assertEquals(message, UiErrorMapper.toMessage(exception))
    }

    @Test
    fun `toMessage returns exception message for Unknown`() {
        val exception = AppException.Unknown("Something went wrong")
        assertEquals("Something went wrong", UiErrorMapper.toMessage(exception))
    }

    @Test
    fun `toMessage returns exception message for unmapped exceptions`() {
        val exception = AppException.UserNotFound(123)
        assertEquals("User with id 123 not found", UiErrorMapper.toMessage(exception))
    }
}
