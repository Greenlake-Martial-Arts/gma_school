// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.students.data.remote

import com.gma.tsunjo.school.api.requests.CreateStudentProgressRequest
import com.gma.tsunjo.school.api.requests.UpdateStudentProgressRequest
import com.gma.tsunjo.school.api.responses.StudentProgressByLevel
import com.gma.tsunjo.school.api.responses.StudentWithLevelResponse
import com.gma.tsunjo.school.data.remote.HttpErrorMapper
import com.gma.tsunjo.school.domain.models.StudentProgress
import com.gma.tsunjo.school.features.students.domain.model.Student
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class StudentsApi(
    private val client: HttpClient,
    private val endpoint: String
) {
    suspend fun getStudents(): List<Student> {
        val response = client.get("$endpoint/students")

        return when {
            response.status.isSuccess() -> response.body()
            else -> throw HttpErrorMapper.mapError(response.status)
        }
    }

    suspend fun getActiveStudents(): List<StudentWithLevelResponse> {
        val response = client.get("$endpoint/students/active")

        return when {
            response.status.isSuccess() -> response.body()
            else -> throw HttpErrorMapper.mapError(response.status)
        }
    }

    suspend fun getStudentProgress(studentId: Long): StudentProgressByLevel {
        val response = client.get("$endpoint/student-progress/student/$studentId")

        return when {
            response.status.isSuccess() -> response.body()
            else -> throw HttpErrorMapper.mapError(response.status)
        }
    }

    suspend fun createStudentProgress(request: CreateStudentProgressRequest): StudentProgress {
        val response = client.post("$endpoint/student-progress") {
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody(request)
        }

        return when {
            response.status.isSuccess() -> response.body()
            else -> throw HttpErrorMapper.mapError(response.status)
        }
    }

    suspend fun updateStudentProgress(progressId: Long, request: com.gma.tsunjo.school.api.requests.UpdateStudentProgressRequest) {
        val response = client.put("$endpoint/student-progress/$progressId") {
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            throw HttpErrorMapper.mapError(response.status)
        }
    }
}
