// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.extensions

import com.gma.tsunjo.school.domain.exceptions.AppException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

suspend fun ApplicationCall.respondWithError(error: Throwable) {
    val (status, message) = when (error) {
        is AppException.UserAlreadyExists -> HttpStatusCode.Conflict to error.message
        is AppException.UserNotFound -> HttpStatusCode.NotFound to error.message
        is AppException.RoleNotFound -> HttpStatusCode.NotFound to error.message
        is AppException.ValidationError -> HttpStatusCode.BadRequest to error.message
        is AppException.DatabaseError -> HttpStatusCode.InternalServerError to "Database error occurred"
        else -> HttpStatusCode.InternalServerError to "An unexpected error occurred"
    }

    respond(status, message ?: "Unknown error")
}
