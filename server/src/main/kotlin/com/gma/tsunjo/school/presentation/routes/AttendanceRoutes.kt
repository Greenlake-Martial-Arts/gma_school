// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.domain.exceptions.AppException

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
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.attendanceRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    routing {
        logger.debug("<<<< attendanceRoutes")
        authenticate("auth-jwt") {
            route("/attendance") {
                getAttendances(logger)
                getAttendanceById(logger)
                getAttendanceWithStudents(logger)
                getAttendancesByDateRange(logger)
                createAttendance(logger)
                updateAttendance(logger)
                deleteAttendance(logger)
                addStudentToAttendance(logger)
                addStudentsToAttendanceBulk(logger)
                removeStudentFromAttendance(logger)
                getStudentsInAttendance(logger)
            }
        }
    }
}

fun Route.getAttendances(logger: Logger) {
    val attendanceRepository by inject<AttendanceRepository>()

    get {
        logger.debug("GET /attendance")
        val attendances = attendanceRepository.getAllAttendances()
        call.respond(attendances)
    }
}

fun Route.getAttendanceById(logger: Logger) {
    val attendanceRepository by inject<AttendanceRepository>()

    get("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("GET /attendance/$id")

        if (id == null) {
            throw AppException.BadRequest("Invalid ID")
            
        }

        val attendance = attendanceRepository.getAttendanceById(id)
        if (attendance != null) {
            call.respond(attendance)
        } else {
            throw AppException.ValidationError("Attendance not found")
        }
    }
}

fun Route.getAttendancesByDateRange(logger: Logger) {
    val attendanceRepository by inject<AttendanceRepository>()

    get("/date-range") {
        val startDate = call.request.queryParameters["startDate"]?.let { LocalDate.parse(it) }
        val endDate = call.request.queryParameters["endDate"]?.let { LocalDate.parse(it) }
        logger.debug("GET /attendance/date-range?startDate=$startDate&endDate=$endDate")

        if (startDate == null || endDate == null) {
            throw AppException.BadRequest("startDate and endDate parameters required")
            
        }

        val attendances = attendanceRepository.getAttendancesByDateRange(startDate, endDate)
        call.respond(attendances)
    }
}

fun Route.createAttendance(logger: Logger) {
    val attendanceRepository by inject<AttendanceRepository>()

    post {
        logger.debug("POST /attendance")
        val request = call.receive<CreateAttendanceRequest>()
        val result = attendanceRepository.createAttendance(request.classDate, request.notes)

        result.fold(
            onSuccess = { call.respond(HttpStatusCode.Created, it) },
            onFailure = { throw it }
        )
    }
}

fun Route.updateAttendance(logger: Logger) {
    val attendanceRepository by inject<AttendanceRepository>()

    put("/{id}") {
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
            throw AppException.ValidationError("Attendance not found")
        }
    }
}

fun Route.deleteAttendance(logger: Logger) {
    val attendanceRepository by inject<AttendanceRepository>()

    delete("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("DELETE /attendance/$id")

        if (id == null) {
            throw AppException.BadRequest("Invalid ID")
            
        }

        val deleted = attendanceRepository.deleteAttendance(id)

        if (deleted) {
            call.respond(HttpStatusCode.OK, "Attendance deleted")
        } else {
            throw AppException.ValidationError("Attendance not found")
        }
    }
}

fun Route.addStudentToAttendance(logger: Logger) {
    val attendanceRepository by inject<AttendanceRepository>()

    post("/{id}/students/{studentId}") {
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
    }
}

fun Route.addStudentsToAttendanceBulk(logger: Logger) {
    val attendanceRepository by inject<AttendanceRepository>()

    post("/{id}/students/bulk") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("POST /attendance/$id/students/bulk")

        if (id == null) {
            throw AppException.BadRequest("Invalid attendance ID")
            
        }

        val request = call.receive<com.gma.tsunjo.school.api.requests.BulkAddStudentsRequest>()
        val count = attendanceRepository.addStudentsToAttendance(id, request.studentIds)

        call.respond(HttpStatusCode.OK, mapOf("added" to count, "total" to request.studentIds.size))
    }
}

fun Route.removeStudentFromAttendance(logger: Logger) {
    val attendanceRepository by inject<AttendanceRepository>()

    delete("/{id}/students/{studentId}") {
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
    }
}

fun Route.getStudentsInAttendance(logger: Logger) {
    val attendanceRepository by inject<AttendanceRepository>()

    get("/{id}/students") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("GET /attendance/$id/students")

        if (id == null) {
            throw AppException.BadRequest("Invalid ID")
            
        }

        val studentIds = attendanceRepository.getStudentsInAttendance(id)
        call.respond(studentIds)
    }
}

fun Route.getAttendanceWithStudents(logger: Logger) {
    val attendanceRepository by inject<AttendanceRepository>()

    get("/{id}/with-students") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("GET /attendance/$id/with-students")

        if (id == null) {
            throw AppException.BadRequest("Invalid ID")
            
        }

        val attendanceWithStudents = attendanceRepository.getAttendanceWithStudents(id)
        if (attendanceWithStudents != null) {
            call.respond(attendanceWithStudents)
        } else {
            throw AppException.ValidationError("Attendance not found")
        }
    }
}
