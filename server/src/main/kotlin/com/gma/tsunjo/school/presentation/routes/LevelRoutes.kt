// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.presentation.extensions.handleException

import com.gma.tsunjo.school.api.requests.CreateLevelRequest
import com.gma.tsunjo.school.api.requests.UpdateLevelRequest
import com.gma.tsunjo.school.domain.repositories.LevelRepository

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
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.levelRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    val levelRepository = get<LevelRepository>()
    
    logger.debug("<<<< levelRoutes")
    routing {
        authenticate("auth-jwt") {
            route("/levels") {
                getLevels(logger, levelRepository)
                getLevelById(logger, levelRepository)
                getStudentsInLevel(logger, levelRepository)
                createLevel(logger, levelRepository)
                updateLevel(logger, levelRepository)
                deleteLevel(logger, levelRepository)
            }
        }
    }
}

fun Route.getLevels(logger: Logger, levelRepository: LevelRepository) {
    get {
        try {
            logger.debug("GET /levels")
            val levels = levelRepository.getAllLevels()
            call.respond(levels)
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getLevelById(logger: Logger, levelRepository: LevelRepository) {
    get("/{id}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("GET /levels/$id")

            if (id == null) {
                throw AppException.BadRequest("Invalid ID")
                
            }

            val level = levelRepository.getLevelById(id)
            if (level != null) {
                call.respond(level)
            } else {
                throw AppException.LevelNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getStudentsInLevel(logger: Logger, levelRepository: LevelRepository) {
    val studentRepository by inject<com.gma.tsunjo.school.domain.repositories.StudentRepository>()

    get("/{id}/students") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("GET /levels/$id/students")

            if (id == null) {
                throw AppException.BadRequest("Invalid ID")
                
            }

            val students = studentRepository.getStudentsByLevel(id)
            call.respond(students)
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.createLevel(logger: Logger, levelRepository: LevelRepository) {
    post {
        try {
            logger.debug("POST /levels")
            val request = call.receive<CreateLevelRequest>()
            val result = levelRepository.createLevel(request.code, request.displayName, request.orderSeq, request.description)

            result.fold(
                onSuccess = { call.respond(HttpStatusCode.Created, it) },
                onFailure = { throw it }
            )
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.updateLevel(logger: Logger, levelRepository: LevelRepository) {
    put("/{id}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("PUT /levels/$id")

            if (id == null) {
                throw AppException.BadRequest("Invalid ID")
                
            }

            val request = call.receive<UpdateLevelRequest>()
            val updated = levelRepository.updateLevel(id, request.code, request.displayName, request.orderSeq, request.description)

            if (updated) {
                call.respond(HttpStatusCode.OK, "Level updated")
            } else {
                throw AppException.LevelNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.deleteLevel(logger: Logger, levelRepository: LevelRepository) {
    delete("/{id}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("DELETE /levels/$id")

            if (id == null) {
                throw AppException.BadRequest("Invalid ID")
                
            }

            val deleted = levelRepository.deleteLevel(id)

            if (deleted) {
                call.respond(HttpStatusCode.OK, "Level deleted")
            } else {
                throw AppException.LevelNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}
