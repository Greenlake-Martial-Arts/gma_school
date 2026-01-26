// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.presentation.extensions.handleException

import com.gma.tsunjo.school.api.requests.CreateStudentProgressRequest
import com.gma.tsunjo.school.api.requests.UpdateStudentProgressRequest
import com.gma.tsunjo.school.domain.repositories.StudentProgressRepository

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

fun Application.studentProgressRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    val studentProgressRepository = get<StudentProgressRepository>()
    routing {
        logger.debug("<<<< studentProgressRoutes")
        authenticate("auth-jwt") {
            route("/student-progress") {
                getStudentProgress(logger, studentProgressRepository)
                getStudentProgressById(logger, studentProgressRepository)
                getProgressByStudent(logger, studentProgressRepository)
                getProgressByStudentAndLevel(logger, studentProgressRepository)
                createStudentProgress(logger, studentProgressRepository)
                updateStudentProgress(logger, studentProgressRepository)
                bulkUpdateStudentProgress(logger, studentProgressRepository)
                deleteStudentProgress(logger, studentProgressRepository)
            }
        }
    }
}

fun Route.getStudentProgress(logger: Logger, studentProgressRepository: StudentProgressRepository) {
    get {
        try {
            logger.debug("GET /student-progress")
            val progress = studentProgressRepository.getAllProgress()
            call.respond(progress)
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getStudentProgressById(logger: Logger, studentProgressRepository: StudentProgressRepository) {
    get("/{id}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("GET /student-progress/$id")

            if (id == null) {
                throw AppException.BadRequest("Invalid ID")
                
            }

            val progress = studentProgressRepository.getProgressById(id)
            if (progress != null) {
                call.respond(progress)
            } else {
                throw AppException.StudentProgressNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getProgressByStudent(logger: Logger, studentProgressRepository: StudentProgressRepository) {
    get("/student/{studentId}") {
        try {
            val studentId = call.parameters["studentId"]?.toLongOrNull()
            logger.debug("GET /student-progress/student/$studentId")

            if (studentId == null) {
                throw AppException.BadRequest("Invalid student ID")
                
            }

            val progress = studentProgressRepository.getProgressByStudent(studentId)
            call.respond(progress)
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getProgressByStudentAndLevel(logger: Logger, studentProgressRepository: StudentProgressRepository) {
    get("/student/{studentId}/level/{levelId}") {
        try {
            val studentId = call.parameters["studentId"]?.toLongOrNull()
            val levelId = call.parameters["levelId"]?.toLongOrNull()
            logger.debug("GET /student-progress/student/$studentId/level/$levelId")

            if (studentId == null || levelId == null) {
                throw AppException.BadRequest("Invalid student ID or level ID")
                
            }

            val progress = studentProgressRepository.getProgressByStudentAndLevel(studentId, levelId)
            if (progress != null) {
                call.respond(progress)
            } else {
                throw AppException.LevelNotFound(levelId)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.createStudentProgress(logger: Logger, studentProgressRepository: StudentProgressRepository) {
    post {
        try {
            logger.debug("POST /student-progress")
            val request = call.receive<CreateStudentProgressRequest>()
            val result = studentProgressRepository.createProgress(
                studentId = request.studentId,
                levelRequirementId = request.levelRequirementId,
                instructorId = request.instructorId,
                attempts = request.attempts,
                notes = request.notes
            )

            result.fold(
                onSuccess = { call.respond(HttpStatusCode.Created, it) },
                onFailure = { throw it }
            )
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.updateStudentProgress(logger: Logger, studentProgressRepository: StudentProgressRepository) {
    put("/{id}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("PUT /student-progress/$id")

            if (id == null) {
                throw AppException.BadRequest("Invalid ID")
                
            }

            val request = call.receive<UpdateStudentProgressRequest>()
            val updated = studentProgressRepository.updateProgress(
                id = id,
                status = request.status,
                instructorId = request.instructorId,
                attempts = request.attempts,
                notes = request.notes
            )

            if (updated) {
                call.respond(HttpStatusCode.OK, "Progress updated")
            } else {
                throw AppException.StudentProgressNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.bulkUpdateStudentProgress(logger: Logger, studentProgressRepository: StudentProgressRepository) {
    put("/bulk") {
        try {
            logger.debug("PUT /student-progress/bulk")

            val request = call.receive<com.gma.tsunjo.school.api.requests.BulkUpdateProgressRequest>()
            val count = studentProgressRepository.bulkUpdateProgress(
                progressIds = request.progressIds,
                status = request.status,
                instructorId = request.instructorId,
                attempts = request.attempts,
                notes = request.notes
            )

            call.respond(HttpStatusCode.OK, mapOf("updated" to count, "total" to request.progressIds.size))
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.deleteStudentProgress(logger: Logger, studentProgressRepository: StudentProgressRepository) {
    delete("/{id}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("DELETE /student-progress/$id")

            if (id == null) {
                throw AppException.BadRequest("Invalid ID")
                
            }

            val deleted = studentProgressRepository.deleteProgress(id)
            if (deleted) {
                call.respond(HttpStatusCode.OK, "Progress deleted")
            } else {
                throw AppException.StudentProgressNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}
