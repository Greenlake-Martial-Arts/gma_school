// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.exceptions

import com.gma.tsunjo.school.resources.Strings

object UiErrorMapper {
    fun toMessage(error: Throwable): String {
        return when (error) {
            is AppException.InvalidCredentials -> Strings.ERROR_INVALID_CREDENTIALS
            is AppException.Unauthorized -> Strings.ERROR_UNAUTHORIZED
            is AppException.SessionExpired -> Strings.ERROR_SESSION_EXPIRED
            is AppException.NetworkError -> Strings.ERROR_NETWORK
            is AppException.Timeout -> Strings.ERROR_TIMEOUT
            is AppException.ServerError -> Strings.ERROR_SERVER
            is AppException.BadRequest -> error.message ?: Strings.ERROR_UNKNOWN
            is AppException.ValidationError -> error.message ?: Strings.ERROR_UNKNOWN
            is AppException -> error.message ?: Strings.ERROR_UNKNOWN
            else -> Strings.ERROR_UNKNOWN
        }
    }
}
