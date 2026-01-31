// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.attendance.data.remote

import com.gma.tsunjo.school.data.remote.HttpErrorMapper
import com.gma.tsunjo.school.features.attendance.domain.model.AttendanceClass
import com.gma.tsunjo.school.features.attendance.domain.model.AttendanceRecord
import com.gma.tsunjo.school.features.attendance.domain.model.CreateAttendanceRequest
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
    suspend fun getClassesForDate(date: String): List<AttendanceClass> {
        val response = client.get("$endpoint/attendance/classes?date=$date")

        return when {
            response.status.isSuccess() -> response.body()
            else -> throw HttpErrorMapper.mapError(response.status)
        }
    }

    suspend fun getAttendanceRecord(classId: String, date: String): AttendanceRecord {
        val response = client.get("$endpoint/attendance/record?classId=$classId&date=$date")

        return when {
            response.status.isSuccess() -> response.body()
            else -> throw HttpErrorMapper.mapError(response.status)
        }
    }

    suspend fun createAttendance(request: CreateAttendanceRequest): AttendanceRecord {
        val response = client.post("$endpoint/attendance") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        return when {
            response.status.isSuccess() -> response.body()
            else -> throw HttpErrorMapper.mapError(response.status)
        }
    }

    suspend fun updateAttendance(recordId: String, studentIds: List<String>): AttendanceRecord {
        val response = client.post("$endpoint/attendance/$recordId") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("studentIds" to studentIds))
        }

        return when {
            response.status.isSuccess() -> response.body()
            else -> throw HttpErrorMapper.mapError(response.status)
        }
    }
}
