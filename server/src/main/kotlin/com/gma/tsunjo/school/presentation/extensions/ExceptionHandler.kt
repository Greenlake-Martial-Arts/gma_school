// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.extensions

import com.gma.tsunjo.school.domain.exceptions.AppException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.response.respond
import org.slf4j.Logger

suspend fun ApplicationCall.handleException(e: Exception, logger: Logger) {
    logger.error("<<<< EXCEPTION: ${e.message}", e)
    val status = when (e) {
        is BadRequestException -> HttpStatusCode.BadRequest
        is AppException.InvalidCredentials -> HttpStatusCode.Unauthorized
        is AppException.Unauthorized -> HttpStatusCode.Forbidden
        is AppException.SessionExpired -> HttpStatusCode.Unauthorized
        is AppException.BadRequest -> HttpStatusCode.BadRequest
        is AppException.ValidationError -> HttpStatusCode.UnprocessableEntity
        is AppException.UserAlreadyExists -> HttpStatusCode.Conflict
        is AppException.UserNotFound -> HttpStatusCode.NotFound
        is AppException.RoleNotFound -> HttpStatusCode.NotFound
        is AppException.StudentAlreadyExists -> HttpStatusCode.Conflict
        is AppException.StudentNotFound -> HttpStatusCode.NotFound
        is AppException.StudentProgressNotFound -> HttpStatusCode.NotFound
        is AppException.MemberTypeNotFound -> HttpStatusCode.NotFound
        is AppException.MemberTypeAlreadyExists -> HttpStatusCode.Conflict
        is AppException.LevelNotFound -> HttpStatusCode.NotFound
        is AppException.LevelAlreadyExists -> HttpStatusCode.Conflict
        is AppException.MoveNotFound -> HttpStatusCode.NotFound
        is AppException.MoveAlreadyExists -> HttpStatusCode.Conflict
        is AppException.MoveCategoryNotFound -> HttpStatusCode.NotFound
        is AppException.MoveCategoryAlreadyExists -> HttpStatusCode.Conflict
        is AppException.AttendanceNotFound -> HttpStatusCode.NotFound
        is AppException.AttendanceAlreadyExists -> HttpStatusCode.Conflict
        is AppException.LevelRequirementNotFound -> HttpStatusCode.NotFound
        is AppException.LevelRequirementAlreadyExists -> HttpStatusCode.Conflict
        is AppException.StudentLevelAlreadyExists -> HttpStatusCode.Conflict
        is AppException.DatabaseError -> HttpStatusCode.InternalServerError
        else -> HttpStatusCode.InternalServerError
    }
    respond(status, mapOf("error" to (e.message ?: "Unknown error")))
}
