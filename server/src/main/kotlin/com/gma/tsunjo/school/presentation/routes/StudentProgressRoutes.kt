// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.domain.exceptions.AppException

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
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.studentProgressRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    routing {
        logger.debug("<<<< studentProgressRoutes")
        authenticate("auth-jwt") {
            route("/student-progress") {
                getStudentProgress(logger)
                getStudentProgressById(logger)
                getProgressByStudent(logger)
                getProgressByStudentAndLevel(logger)
                createStudentProgress(logger)
                updateStudentProgress(logger)
                bulkUpdateStudentProgress(logger)
                deleteStudentProgress(logger)
            }
        }
    }
}

fun Route.getStudentProgress(logger: Logger) {
    val repository by inject<StudentProgressRepository>()

    get {
        logger.debug("GET /student-progress")
        val progress = repository.getAllProgress()
        call.respond(progress)
    }
}

fun Route.getStudentProgressById(logger: Logger) {
    val repository by inject<StudentProgressRepository>()

    get("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("GET /student-progress/$id")

        if (id == null) {
            throw AppException.BadRequest("Invalid ID")
            
        }

        val progress = repository.getProgressById(id)
        if (progress != null) {
            call.respond(progress)
        } else {
            throw AppException.ValidationError("Progress not found")
        }
    }
}

fun Route.getProgressByStudent(logger: Logger) {
    val repository by inject<StudentProgressRepository>()

    get("/student/{studentId}") {
        val studentId = call.parameters["studentId"]?.toLongOrNull()
        logger.debug("GET /student-progress/student/$studentId")

        if (studentId == null) {
            throw AppException.BadRequest("Invalid student ID")
            
        }

        val progress = repository.getProgressByStudent(studentId)
        call.respond(progress)
    }
}

fun Route.getProgressByStudentAndLevel(logger: Logger) {
    val repository by inject<StudentProgressRepository>()

    get("/student/{studentId}/level/{levelId}") {
        val studentId = call.parameters["studentId"]?.toLongOrNull()
        val levelId = call.parameters["levelId"]?.toLongOrNull()
        logger.debug("GET /student-progress/student/$studentId/level/$levelId")

        if (studentId == null || levelId == null) {
            throw AppException.BadRequest("Invalid student ID or level ID")
            
        }

        val progress = repository.getProgressByStudentAndLevel(studentId, levelId)
        if (progress != null) {
            call.respond(progress)
        } else {
            throw AppException.LevelNotFound(levelId)
        }
    }
}

fun Route.createStudentProgress(logger: Logger) {
    val repository by inject<StudentProgressRepository>()

    post {
        logger.debug("POST /student-progress")
        val request = call.receive<CreateStudentProgressRequest>()
        val result = repository.createProgress(
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
    }
}

fun Route.updateStudentProgress(logger: Logger) {
    val repository by inject<StudentProgressRepository>()

    put("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("PUT /student-progress/$id")

        if (id == null) {
            throw AppException.BadRequest("Invalid ID")
            
        }

        val request = call.receive<UpdateStudentProgressRequest>()
        val updated = repository.updateProgress(
            id = id,
            status = request.status,
            instructorId = request.instructorId,
            attempts = request.attempts,
            notes = request.notes
        )

        if (updated) {
            call.respond(HttpStatusCode.OK, "Progress updated")
        } else {
            throw AppException.ValidationError("Progress not found")
        }
    }
}

fun Route.bulkUpdateStudentProgress(logger: Logger) {
    val repository by inject<StudentProgressRepository>()

    put("/bulk") {
        logger.debug("PUT /student-progress/bulk")

        val request = call.receive<com.gma.tsunjo.school.api.requests.BulkUpdateProgressRequest>()
        val count = repository.bulkUpdateProgress(
            progressIds = request.progressIds,
            status = request.status,
            instructorId = request.instructorId,
            attempts = request.attempts,
            notes = request.notes
        )

        call.respond(HttpStatusCode.OK, mapOf("updated" to count, "total" to request.progressIds.size))
    }
}

fun Route.deleteStudentProgress(logger: Logger) {
    val repository by inject<StudentProgressRepository>()

    delete("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("DELETE /student-progress/$id")

        if (id == null) {
            throw AppException.BadRequest("Invalid ID")
            
        }

        val deleted = repository.deleteProgress(id)
        if (deleted) {
            call.respond(HttpStatusCode.OK, "Progress deleted")
        } else {
            throw AppException.ValidationError("Progress not found")
        }
    }
}
