// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.domain.exceptions.AppException

import com.gma.tsunjo.school.api.requests.CreateStudentRequest
import com.gma.tsunjo.school.api.requests.UpdateStudentRequest
import com.gma.tsunjo.school.domain.repositories.StudentRepository
import com.gma.tsunjo.school.domain.services.StudentService
import com.gma.tsunjo.school.presentation.extensions.handleException

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
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.studentRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    val studentRepository = get<StudentRepository>()
    val studentService = get<StudentService>()
    
    logger.debug("<<<< studentRoutes")
    routing {
        authenticate("auth-jwt") {
            route("/students") {
                getStudents(logger, studentRepository)
                getActiveStudents(logger, studentRepository)
                getStudentsByLevel(logger, studentRepository)
                getStudentById(logger, studentRepository)
                createStudent(logger, studentService)
                updateStudent(logger, studentService)
                deactivateStudent(logger, studentService)
                activateStudent(logger, studentService)
            }
        }
    }
}

fun Route.getStudents(logger: Logger, studentRepository: StudentRepository) {

    get {
        try {
            logger.debug("GET /students")
            val students = studentRepository.getAllStudents()
            call.respond(students)
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getActiveStudents(logger: Logger, studentRepository: StudentRepository) {

    get("/active") {
        try {
            logger.debug("GET /students/active")
            val students = studentRepository.getActiveStudents()
            call.respond(students)
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getStudentsByLevel(logger: Logger, studentRepository: StudentRepository) {

    get("/level/{levelId}") {
        try {
            val levelId = call.parameters["levelId"]?.toLongOrNull()
            logger.debug("GET /students/level/$levelId")

            if (levelId == null) {
                throw AppException.BadRequest("Invalid level ID")
                
            }

            val students = studentRepository.getStudentsByLevel(levelId)
            call.respond(students)
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getStudentById(logger: Logger, studentRepository: StudentRepository) {

    get("/{id}") {
        try {
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
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.createStudent(logger: Logger, studentService: StudentService) {

    post {
        try {
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
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.updateStudent(logger: Logger, studentService: StudentService) {

    put("/{id}") {
        try {
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
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.deactivateStudent(logger: Logger, studentService: StudentService) {

    patch("/{id}/deactivate") {
        try {
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
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.activateStudent(logger: Logger, studentService: StudentService) {

    patch("/{id}/activate") {
        try {
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
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}
