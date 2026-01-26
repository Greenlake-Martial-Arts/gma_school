// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.config

import com.gma.tsunjo.school.domain.exceptions.AppException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<AppException.InvalidCredentials> { call, _ ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
        }
        exception<AppException.Unauthorized> { call, _ ->
            call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Unauthorized access"))
        }
        exception<AppException.SessionExpired> { call, _ ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Session expired"))
        }
        exception<AppException.BadRequest> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to cause.message))
        }
        exception<AppException.ValidationError> { call, cause ->
            call.respond(HttpStatusCode.UnprocessableEntity, mapOf("error" to cause.message))
        }
        exception<AppException.UserNotFound> { call, cause ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
        }
        exception<AppException.UserAlreadyExists> { call, cause ->
            call.respond(HttpStatusCode.Conflict, mapOf("error" to cause.message))
        }
        exception<AppException.StudentNotFound> { call, cause ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
        }
        exception<AppException.StudentAlreadyExists> { call, cause ->
            call.respond(HttpStatusCode.Conflict, mapOf("error" to cause.message))
        }
        exception<AppException.RoleNotFound> { call, cause ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
        }
        exception<AppException.MemberTypeNotFound> { call, cause ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
        }
        exception<AppException.MemberTypeAlreadyExists> { call, cause ->
            call.respond(HttpStatusCode.Conflict, mapOf("error" to cause.message))
        }
        exception<AppException.LevelNotFound> { call, cause ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
        }
        exception<AppException.LevelAlreadyExists> { call, cause ->
            call.respond(HttpStatusCode.Conflict, mapOf("error" to cause.message))
        }
        exception<AppException.MoveNotFound> { call, cause ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
        }
        exception<AppException.MoveAlreadyExists> { call, cause ->
            call.respond(HttpStatusCode.Conflict, mapOf("error" to cause.message))
        }
        exception<AppException.MoveCategoryNotFound> { call, cause ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
        }
        exception<AppException.MoveCategoryAlreadyExists> { call, cause ->
            call.respond(HttpStatusCode.Conflict, mapOf("error" to cause.message))
        }
        exception<AppException.DatabaseError> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Database error occurred"))
        }
        exception<AppException> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to cause.message))
        }
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "An unexpected error occurred"))
        }
    }
}
