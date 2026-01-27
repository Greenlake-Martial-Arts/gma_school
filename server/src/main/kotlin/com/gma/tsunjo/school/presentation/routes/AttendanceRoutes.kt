// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.presentation.extensions.handleException

import com.gma.tsunjo.school.api.requests.CreateAttendanceRequest
import com.gma.tsunjo.school.domain.repositories.AttendanceRepository

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
import kotlinx.datetime.LocalDate
import org.koin.ktor.ext.get
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.attendanceRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    val attendanceRepository = get<AttendanceRepository>()
    routing {
        logger.debug("<<<< attendanceRoutes")
        authenticate("auth-jwt") {
            route("/attendance") {
                getAttendances(logger, attendanceRepository)
                getAttendanceById(logger, attendanceRepository)
                getAttendanceWithStudents(logger, attendanceRepository)
                getAttendancesByDateRange(logger, attendanceRepository)
                createAttendance(logger, attendanceRepository)
                updateAttendance(logger, attendanceRepository)
                deleteAttendance(logger, attendanceRepository)
                addStudentToAttendance(logger, attendanceRepository)
                addStudentsToAttendanceBulk(logger, attendanceRepository)
                removeStudentFromAttendance(logger, attendanceRepository)
                getStudentsInAttendance(logger, attendanceRepository)
            }
        }
    }
}

fun Route.getAttendances(logger: Logger, attendanceRepository: AttendanceRepository) {
    get {
        try {
            logger.debug("GET /attendance")
            val attendances = attendanceRepository.getAllAttendances()
            call.respond(attendances)
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getAttendanceById(logger: Logger, attendanceRepository: AttendanceRepository) {
    get("/{id}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("GET /attendance/$id")

            if (id == null) {
                throw AppException.BadRequest("Invalid ID")
                
            }

            val attendance = attendanceRepository.getAttendanceById(id)
            if (attendance != null) {
                call.respond(attendance)
            } else {
                throw AppException.AttendanceNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getAttendancesByDateRange(logger: Logger, attendanceRepository: AttendanceRepository) {
    get("/date-range") {
        try {
            val startDate = call.request.queryParameters["startDate"]?.let { LocalDate.parse(it) }
            val endDate = call.request.queryParameters["endDate"]?.let { LocalDate.parse(it) }
            logger.debug("GET /attendance/date-range?startDate=$startDate&endDate=$endDate")

            if (startDate == null || endDate == null) {
                throw AppException.BadRequest("startDate and endDate parameters required")
                
            }

            val attendances = attendanceRepository.getAttendancesByDateRange(startDate, endDate)
            call.respond(attendances)
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.createAttendance(logger: Logger, attendanceRepository: AttendanceRepository) {
    post {
        try {
            logger.debug("POST /attendance")
            val request = call.receive<CreateAttendanceRequest>()
            val result = attendanceRepository.createAttendance(request.classDate, request.notes)

            result.fold(
                onSuccess = { call.respond(HttpStatusCode.Created, it) },
                onFailure = { throw it }
            )
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.updateAttendance(logger: Logger, attendanceRepository: AttendanceRepository) {
    put("/{id}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("PUT /attendance/$id")

            if (id == null) {
                throw AppException.BadRequest("Invalid ID")
                
            }

            val request = call.receive<CreateAttendanceRequest>()
            val updated = attendanceRepository.updateAttendance(id, request.notes)

            if (updated) {
                call.respond(HttpStatusCode.OK, "Attendance updated")
            } else {
                throw AppException.AttendanceNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.deleteAttendance(logger: Logger, attendanceRepository: AttendanceRepository) {
    delete("/{id}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("DELETE /attendance/$id")

            if (id == null) {
                throw AppException.BadRequest("Invalid ID")
                
            }

            val deleted = attendanceRepository.deleteAttendance(id)

            if (deleted) {
                call.respond(HttpStatusCode.OK, "Attendance deleted")
            } else {
                throw AppException.AttendanceNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.addStudentToAttendance(logger: Logger, attendanceRepository: AttendanceRepository) {
    post("/{id}/students/{studentId}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            val studentId = call.parameters["studentId"]?.toLongOrNull()
            logger.debug("POST /attendance/$id/students/$studentId")

            if (id == null || studentId == null) {
                throw AppException.BadRequest("Invalid attendance ID or student ID")
                
            }

            val added = attendanceRepository.addStudentToAttendance(id, studentId)

            if (added) {
                call.respond(HttpStatusCode.OK, "Student added to attendance")
            } else {
                throw AppException.BadRequest("Failed to add student to attendance")
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.addStudentsToAttendanceBulk(logger: Logger, attendanceRepository: AttendanceRepository) {
    post("/{id}/students/bulk") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("POST /attendance/$id/students/bulk")

            if (id == null) {
                throw AppException.BadRequest("Invalid attendance ID")
                
            }

            val request = call.receive<com.gma.tsunjo.school.api.requests.BulkAddStudentsRequest>()
            val count = attendanceRepository.addStudentsToAttendance(id, request.studentIds)

            call.respond(HttpStatusCode.OK, mapOf("added" to count, "total" to request.studentIds.size))
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.removeStudentFromAttendance(logger: Logger, attendanceRepository: AttendanceRepository) {
    delete("/{id}/students/{studentId}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            val studentId = call.parameters["studentId"]?.toLongOrNull()
            logger.debug("DELETE /attendance/$id/students/$studentId")

            if (id == null || studentId == null) {
                throw AppException.BadRequest("Invalid attendance ID or student ID")
                
            }

            val removed = attendanceRepository.removeStudentFromAttendance(id, studentId)

            if (removed) {
                call.respond(HttpStatusCode.OK, "Student removed from attendance")
            } else {
                throw AppException.ValidationError("Student not found in attendance")
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getStudentsInAttendance(logger: Logger, attendanceRepository: AttendanceRepository) {
    get("/{id}/students") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("GET /attendance/$id/students")

            if (id == null) {
                throw AppException.BadRequest("Invalid ID")
                
            }

            val studentIds = attendanceRepository.getStudentsInAttendance(id)
            call.respond(studentIds)
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getAttendanceWithStudents(logger: Logger, attendanceRepository: AttendanceRepository) {
    get("/{id}/with-students") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("GET /attendance/$id/with-students")

            if (id == null) {
                throw AppException.BadRequest("Invalid ID")
                
            }

            val attendanceWithStudents = attendanceRepository.getAttendanceWithStudents(id)
            if (attendanceWithStudents != null) {
                call.respond(attendanceWithStudents)
            } else {
                throw AppException.AttendanceNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}
