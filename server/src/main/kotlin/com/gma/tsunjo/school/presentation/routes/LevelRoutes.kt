// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.api.requests.CreateLevelRequest
import com.gma.tsunjo.school.api.requests.UpdateLevelRequest
import com.gma.tsunjo.school.domain.repositories.LevelRepository
import com.gma.tsunjo.school.presentation.extensions.respondWithError
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

fun Application.levelRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    routing {
        logger.debug("<<<< levelRoutes")
        authenticate("auth-jwt") {
            route("/levels") {
                getLevels(logger)
                getLevelById(logger)
                getStudentsInLevel(logger)
                createLevel(logger)
                updateLevel(logger)
                deleteLevel(logger)
            }
        }
    }
}

fun Route.getLevels(logger: Logger) {
    val levelRepository by inject<LevelRepository>()

    get {
        logger.debug("GET /levels")
        val levels = levelRepository.getAllLevels()
        call.respond(levels)
    }
}

fun Route.getLevelById(logger: Logger) {
    val levelRepository by inject<LevelRepository>()

    get("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("GET /levels/$id")

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return@get
        }

        val level = levelRepository.getLevelById(id)
        if (level != null) {
            call.respond(level)
        } else {
            call.respond(HttpStatusCode.NotFound, "Level not found")
        }
    }
}

fun Route.getStudentsInLevel(logger: Logger) {
    val studentRepository by inject<com.gma.tsunjo.school.domain.repositories.StudentRepository>()

    get("/{id}/students") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("GET /levels/$id/students")

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return@get
        }

        val students = studentRepository.getStudentsByLevel(id)
        call.respond(students)
    }
}

fun Route.createLevel(logger: Logger) {
    val levelRepository by inject<LevelRepository>()

    post {
        logger.debug("POST /levels")
        val request = call.receive<CreateLevelRequest>()
        val result = levelRepository.createLevel(request.code, request.displayName, request.orderSeq, request.description)

        result.fold(
            onSuccess = { call.respond(HttpStatusCode.Created, it) },
            onFailure = { call.respondWithError(it) }
        )
    }
}

fun Route.updateLevel(logger: Logger) {
    val levelRepository by inject<LevelRepository>()

    put("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("PUT /levels/$id")

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return@put
        }

        val request = call.receive<UpdateLevelRequest>()
        val updated = levelRepository.updateLevel(id, request.code, request.displayName, request.orderSeq, request.description)

        if (updated) {
            call.respond(HttpStatusCode.OK, "Level updated")
        } else {
            call.respond(HttpStatusCode.NotFound, "Level not found")
        }
    }
}

fun Route.deleteLevel(logger: Logger) {
    val levelRepository by inject<LevelRepository>()

    delete("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("DELETE /levels/$id")

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return@delete
        }

        val deleted = levelRepository.deleteLevel(id)

        if (deleted) {
            call.respond(HttpStatusCode.OK, "Level deleted")
        } else {
            call.respond(HttpStatusCode.NotFound, "Level not found")
        }
    }
}
