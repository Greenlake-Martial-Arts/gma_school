// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.data.remote

import com.gma.tsunjo.school.domain.exceptions.AppException
import io.ktor.http.*

object HttpErrorMapper {
    fun mapError(statusCode: HttpStatusCode, message: String = ""): AppException {
        return when (statusCode) {
            HttpStatusCode.Unauthorized -> AppException.InvalidCredentials()
            HttpStatusCode.Forbidden -> AppException.Unauthorized()
            HttpStatusCode.NotFound -> AppException.ValidationError(message.ifEmpty { "Not found" })
            HttpStatusCode.BadRequest -> AppException.ValidationError(message.ifEmpty { "Bad request" })
            HttpStatusCode.Conflict -> AppException.ValidationError(message.ifEmpty { "Conflict" })
            in HttpStatusCode.InternalServerError..HttpStatusCode.GatewayTimeout -> 
                AppException.ServerError(message.ifEmpty { "Server error" })
            else -> AppException.Unknown("HTTP ${statusCode.value}: ${statusCode.description}")
        }
    }
}
