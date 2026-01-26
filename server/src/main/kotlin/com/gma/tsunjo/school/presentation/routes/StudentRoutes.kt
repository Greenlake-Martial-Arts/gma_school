// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.domain.exceptions.AppException

import com.gma.tsunjo.school.api.requests.CreateStudentRequest
import com.gma.tsunjo.school.api.requests.UpdateStudentRequest
import com.gma.tsunjo.school.domain.repositories.StudentRepository
import com.gma.tsunjo.school.domain.services.StudentService

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
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
        authenticate("auth-jwt") {
            route("/students") {
                getStudents(logger)
                getActiveStudents(logger)
                getStudentsByLevel(logger)
                getStudentById(logger)
                createStudent(logger)
                updateStudent(logger)
                deactivateStudent(logger)
                activateStudent(logger)
            }
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

fun Route.getStudentsByLevel(logger: Logger) {
    val studentRepository by inject<StudentRepository>()

    get("/level/{levelId}") {
        val levelId = call.parameters["levelId"]?.toLongOrNull()
        logger.debug("GET /students/level/$levelId")

        if (levelId == null) {
            throw AppException.BadRequest("Invalid level ID")
            
        }

        val students = studentRepository.getStudentsByLevel(levelId)
        call.respond(students)
    }
}

fun Route.getStudentById(logger: Logger) {
    val studentRepository by inject<StudentRepository>()

    get("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("GET /students/$id")

        if (id == null) {
            throw AppException.BadRequest("Invalid student ID")
            
        }

        val student = studentRepository.getStudentById(id)
        if (student != null) {
            call.respond(student)
        } else {
            throw AppException.StudentNotFound(id)
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
            address = request.address,
            memberTypeId = request.memberTypeId,
            signupDate = request.signupDate,
            initialLevelCode = request.initialLevelCode
        ).fold(
            onSuccess = { student ->
                call.respond(HttpStatusCode.Created, student)
            },
            onFailure = { error ->
                throw error
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
            throw AppException.BadRequest("Invalid student ID")
            
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
            throw AppException.StudentNotFound(id)
        }
    }
}

fun Route.deactivateStudent(logger: Logger) {
    val studentService by inject<StudentService>()

    patch("/{id}/deactivate") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("PATCH /students/$id/deactivate")

        if (id == null) {
            throw AppException.BadRequest("Invalid student ID")
            
        }

        val deactivated = studentService.deactivateStudent(id)
        if (deactivated) {
            call.respond(HttpStatusCode.OK, "Student deactivated")
        } else {
            throw AppException.StudentNotFound(id)
        }
    }
}

fun Route.activateStudent(logger: Logger) {
    val studentService by inject<StudentService>()

    patch("/{id}/activate") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("PATCH /students/$id/activate")

        if (id == null) {
            throw AppException.BadRequest("Invalid student ID")
            
        }

        val activated = studentService.activateStudent(id)
        if (activated) {
            call.respond(HttpStatusCode.OK, "Student activated")
        } else {
            throw AppException.StudentNotFound(id)
        }
    }
}
