// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.domain.exceptions.AppException

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
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.moveRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    routing {
        logger.debug("<<<< moveRoutes")
        authenticate("auth-jwt") {
            route("/moves") {
                getMoves(logger)
                getMoveById(logger)
                getMovesByCategory(logger)
                createMove(logger)
                deleteMove(logger)
            }
        }
    }
}

fun Route.getMoves(logger: Logger) {
    val moveRepository by inject<MoveRepository>()

    get {
        logger.debug("GET /moves")
        val moves = moveRepository.getAllMoves()
        call.respond(moves)
    }
}

fun Route.getMoveById(logger: Logger) {
    val moveRepository by inject<MoveRepository>()

    get("/{id}") {
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
    }
}

fun Route.getMovesByCategory(logger: Logger) {
    val moveRepository by inject<MoveRepository>()

    get("/category/{categoryId}") {
        val categoryId = call.parameters["categoryId"]?.toLongOrNull()
        logger.debug("GET /moves/category/$categoryId")

        if (categoryId == null) {
            throw AppException.BadRequest("Invalid category ID")
            
        }

        val moves = moveRepository.getMovesByCategory(categoryId)
        call.respond(moves)
    }
}

fun Route.createMove(logger: Logger) {
    val moveRepository by inject<MoveRepository>()

    post {
        logger.debug("POST /moves")
        val request = call.receive<CreateMoveRequest>()
        val result = moveRepository.createMove(request.name, request.description)

        result.fold(
            onSuccess = { call.respond(HttpStatusCode.Created, it) },
            onFailure = { throw it }
        )
    }
}

fun Route.deleteMove(logger: Logger) {
    val moveRepository by inject<MoveRepository>()

    delete("/{id}") {
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
    }
}
