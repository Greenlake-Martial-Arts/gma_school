// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.domain.exceptions.AppException

import com.gma.tsunjo.school.api.requests.CreateMoveCategoryRequest
import com.gma.tsunjo.school.domain.repositories.MoveCategoryRepository

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.moveCategoryRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    routing {
        logger.debug("<<<< moveCategoryRoutes")
        authenticate("auth-jwt") {
            route("/move-categories") {
                getMoveCategories(logger)
                getMoveCategoryById(logger)
                createMoveCategory(logger)
                updateMoveCategory(logger)
                deleteMoveCategory(logger)
            }
        }
    }
}

fun Route.getMoveCategories(logger: Logger) {
    val moveCategoryRepository by inject<MoveCategoryRepository>()

    get {
        logger.debug("GET /move-categories")
        val categories = moveCategoryRepository.getAllMoveCategories()
        call.respond(categories)
    }
}

fun Route.getMoveCategoryById(logger: Logger) {
    val moveCategoryRepository by inject<MoveCategoryRepository>()

    get("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("GET /move-categories/$id")

        if (id == null) {
            throw AppException.BadRequest("Invalid ID")
            
        }

        val category = moveCategoryRepository.getMoveCategoryById(id)
        if (category != null) {
            call.respond(category)
        } else {
            throw AppException.MoveCategoryNotFound(id)
        }
    }
}

fun Route.createMoveCategory(logger: Logger) {
    val moveCategoryRepository by inject<MoveCategoryRepository>()

    post {
        logger.debug("POST /move-categories")
        val request = call.receive<CreateMoveCategoryRequest>()
        val result = moveCategoryRepository.createMoveCategory(request.name, request.description)

        result.fold(
            onSuccess = { call.respond(HttpStatusCode.Created, it) },
            onFailure = { throw it }
        )
    }
}

fun Route.updateMoveCategory(logger: Logger) {
    val moveCategoryRepository by inject<MoveCategoryRepository>()

    put("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("PUT /move-categories/$id")

        if (id == null) {
            throw AppException.BadRequest("Invalid ID")
            
        }

        val request = call.receive<CreateMoveCategoryRequest>()
        val updated = moveCategoryRepository.updateMoveCategory(id, request.name, request.description)

        if (updated) {
            call.respond(HttpStatusCode.OK, "Move category updated")
        } else {
            throw AppException.MoveCategoryNotFound(id)
        }
    }
}

fun Route.deleteMoveCategory(logger: Logger) {
    val moveCategoryRepository by inject<MoveCategoryRepository>()

    delete("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("DELETE /move-categories/$id")

        if (id == null) {
            throw AppException.BadRequest("Invalid ID")
            
        }

        val deleted = moveCategoryRepository.deleteMoveCategory(id)

        if (deleted) {
            call.respond(HttpStatusCode.OK, "Move category deleted")
        } else {
            throw AppException.MoveCategoryNotFound(id)
        }
    }
}
