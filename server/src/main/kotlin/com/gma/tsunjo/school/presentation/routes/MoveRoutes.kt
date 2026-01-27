// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.presentation.extensions.handleException

import com.gma.tsunjo.school.api.requests.CreateMoveRequest
import com.gma.tsunjo.school.domain.repositories.MoveRepository

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
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.moveRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    val moveRepository = get<MoveRepository>()
    routing {
        logger.debug("<<<< moveRoutes")
        authenticate("auth-jwt") {
            route("/moves") {
                getMoves(logger, moveRepository)
                getMoveById(logger, moveRepository)
                getMovesByCategory(logger, moveRepository)
                createMove(logger, moveRepository)
                deleteMove(logger, moveRepository)
            }
        }
    }
}

fun Route.getMoves(logger: Logger, moveRepository: MoveRepository) {
    get {
        try {
            logger.debug("GET /moves")
            val moves = moveRepository.getAllMoves()
            call.respond(moves)
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getMoveById(logger: Logger, moveRepository: MoveRepository) {
    get("/{id}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("GET /moves/$id")

            if (id == null) {
                throw AppException.BadRequest("Invalid ID")
                
            }

            val move = moveRepository.getMoveById(id)
            if (move != null) {
                call.respond(move)
            } else {
                throw AppException.MoveNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getMovesByCategory(logger: Logger, moveRepository: MoveRepository) {
    get("/category/{categoryId}") {
        try {
            val categoryId = call.parameters["categoryId"]?.toLongOrNull()
            logger.debug("GET /moves/category/$categoryId")

            if (categoryId == null) {
                throw AppException.BadRequest("Invalid category ID")
                
            }

            val moves = moveRepository.getMovesByCategory(categoryId)
            call.respond(moves)
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.createMove(logger: Logger, moveRepository: MoveRepository) {
    post {
        try {
            logger.debug("POST /moves")
            val request = call.receive<CreateMoveRequest>()
            val result = moveRepository.createMove(request.name, request.description)

            result.fold(
                onSuccess = { call.respond(HttpStatusCode.Created, it) },
                onFailure = { throw it }
            )
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.deleteMove(logger: Logger, moveRepository: MoveRepository) {
    delete("/{id}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("DELETE /moves/$id")

            if (id == null) {
                throw AppException.BadRequest("Invalid ID")
                
            }

            val deleted = moveRepository.deleteMove(id)

            if (deleted) {
                call.respond(HttpStatusCode.OK, "Move deleted")
            } else {
                throw AppException.MoveNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}
