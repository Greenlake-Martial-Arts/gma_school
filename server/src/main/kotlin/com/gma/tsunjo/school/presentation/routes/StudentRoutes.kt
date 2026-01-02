// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.api.requests.CreateStudentRequest
import com.gma.tsunjo.school.api.requests.UpdateStudentRequest
import com.gma.tsunjo.school.domain.repositories.StudentRepository
import com.gma.tsunjo.school.domain.services.StudentService
import com.gma.tsunjo.school.presentation.extensions.respondWithError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.studentRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    routing {
        logger.debug("<<<< studentRoutes")
        route("/students") {
            getStudents(logger)
            getActiveStudents(logger)
            getStudentById(logger)
            createStudent(logger)
            updateStudent(logger)
            deactivateStudent(logger)
            activateStudent(logger)
        }
    }
}

fun Route.getStudents(logger: Logger) {
    val studentRepository by inject<StudentRepository>()

    get {
        logger.debug("GET /students")
        val students = studentRepository.getAllStudents()
        call.respond(students)
    }
}

fun Route.getActiveStudents(logger: Logger) {
    val studentRepository by inject<StudentRepository>()

    get("/active") {
        logger.debug("GET /students/active")
        val students = studentRepository.getActiveStudents()
        call.respond(students)
    }
}

fun Route.getStudentById(logger: Logger) {
    val studentRepository by inject<StudentRepository>()

    get("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("GET /students/$id")

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid student ID")
            return@get
        }

        val student = studentRepository.getStudentById(id)
        if (student != null) {
            call.respond(student)
        } else {
            call.respond(HttpStatusCode.NotFound, "Student not found")
        }
    }
}

fun Route.createStudent(logger: Logger) {
    val studentService by inject<StudentService>()

    post {
        logger.debug("POST /students")
        val request = call.receive<CreateStudentRequest>()

        studentService.createStudent(
            externalCode = request.externalCode,
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            phone = request.phone,
            memberTypeId = request.memberTypeId
        ).fold(
            onSuccess = { student ->
                call.respond(HttpStatusCode.Created, student)
            },
            onFailure = { error ->
                call.respondWithError(error)
            }
        )
    }
}

fun Route.updateStudent(logger: Logger) {
    val studentService by inject<StudentService>()

    put("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("PUT /students/$id")

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid student ID")
            return@put
        }

        val request = call.receive<UpdateStudentRequest>()
        val student = studentService.updateStudent(
            id = id,
            externalCode = request.externalCode,
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            phone = request.phone,
            memberTypeId = request.memberTypeId,
            isActive = request.isActive
        )

        if (student != null) {
            call.respond(student)
        } else {
            call.respond(HttpStatusCode.NotFound, "Student not found")
        }
    }
}

fun Route.deactivateStudent(logger: Logger) {
    val studentService by inject<StudentService>()

    patch("/{id}/deactivate") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("PATCH /students/$id/deactivate")

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid student ID")
            return@patch
        }

        val deactivated = studentService.deactivateStudent(id)
        if (deactivated) {
            call.respond(HttpStatusCode.OK, "Student deactivated")
        } else {
            call.respond(HttpStatusCode.NotFound, "Student not found")
        }
    }
}

fun Route.activateStudent(logger: Logger) {
    val studentService by inject<StudentService>()

    patch("/{id}/activate") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("PATCH /students/$id/activate")

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid student ID")
            return@patch
        }

        val activated = studentService.activateStudent(id)
        if (activated) {
            call.respond(HttpStatusCode.OK, "Student activated")
        } else {
            call.respond(HttpStatusCode.NotFound, "Student not found")
        }
    }
}
