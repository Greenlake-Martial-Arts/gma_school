// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.attendance.data.remote

import com.gma.tsunjo.school.api.requests.CreateAttendanceRequest
import com.gma.tsunjo.school.api.responses.AttendanceWithStudents
import com.gma.tsunjo.school.data.remote.HttpErrorMapper
import com.gma.tsunjo.school.domain.models.Attendance
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class AttendanceApi(
    private val client: HttpClient,
    private val endpoint: String
) {
    suspend fun getAttendancesByDateRange(startDate: String, endDate: String): List<Attendance> {
        val response = client.get("$endpoint/attendance/date-range?startDate=$startDate&endDate=$endDate")

        return when {
            response.status.isSuccess() -> response.body()
            else -> throw HttpErrorMapper.mapError(response.status)
        }
    }

    suspend fun getAttendanceWithStudents(attendanceId: Long): AttendanceWithStudents {
        val response = client.get("$endpoint/attendance/$attendanceId/with-students")

        return when {
            response.status.isSuccess() -> response.body()
            else -> throw HttpErrorMapper.mapError(response.status)
        }
    }

    suspend fun createAttendance(request: CreateAttendanceRequest): Attendance {
        val response = client.post("$endpoint/attendance") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        return when {
            response.status.isSuccess() -> response.body()
            else -> throw HttpErrorMapper.mapError(response.status)
        }
    }

    suspend fun addStudentsToAttendance(attendanceId: Long, studentIds: List<Long>) {
        val response = client.post("$endpoint/attendance/$attendanceId/students/bulk") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("studentIds" to studentIds))
        }

        if (!response.status.isSuccess()) {
            throw HttpErrorMapper.mapError(response.status)
        }
    }
}
