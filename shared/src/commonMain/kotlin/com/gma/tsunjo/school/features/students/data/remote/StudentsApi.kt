// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.students.data.remote

import com.gma.tsunjo.school.api.responses.StudentProgressByLevel
import com.gma.tsunjo.school.api.responses.StudentWithLevelResponse
import com.gma.tsunjo.school.data.remote.HttpErrorMapper
import com.gma.tsunjo.school.features.students.domain.model.Student
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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
}
