// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.students.data.repository

import com.gma.tsunjo.school.auth.AuthenticationHandler
import com.gma.tsunjo.school.features.students.data.remote.StudentsApi
import com.gma.tsunjo.school.features.students.domain.model.RequirementItem
import com.gma.tsunjo.school.features.students.domain.model.Student
import com.gma.tsunjo.school.features.students.domain.model.StudentProgressDetail
import com.gma.tsunjo.school.features.students.domain.model.StudentWithLevel

class StudentsRepository(
    private val studentsApi: StudentsApi,
    private val authHandler: AuthenticationHandler
) {
    suspend fun getStudents(): Result<List<Student>> {
        return try {
            val students = studentsApi.getStudents()
            Result.success(students)
        } catch (e: Exception) {
            authHandler.handleError(e)
            Result.failure(e)
        }
    }

    suspend fun getActiveStudents(): Result<List<StudentWithLevel>> {
        return try {
            val response = studentsApi.getActiveStudents()
            val students = response.map { item ->
                StudentWithLevel(
                    id = item.student.id,
                    userId = item.student.userId,
                    firstName = item.student.firstName,
                    lastName = item.student.lastName,
                    fullName = "${item.student.firstName} ${item.student.lastName}",
                    isActive = item.student.isActive,
                    currentLevel = item.currentLevel.displayName,
                    code = item.currentLevel.code
                )
            }
            Result.success(students)
        } catch (e: Exception) {
            authHandler.handleError(e)
            Result.failure(e)
        }
    }

    suspend fun getStudentProgress(studentId: Long, studentName: String): Result<StudentProgressDetail> {
        return try {
            val response = studentsApi.getStudentProgress(studentId)
            val detail = StudentProgressDetail(
                studentId = studentId,
                studentName = studentName,
                levelName = response.level.displayName,
                requirements = response.requirements.map { req ->
                    RequirementItem(
                        moveId = req.move.id,
                        moveName = req.move.name,
                        progressId = req.progress.id,
                        status = req.progress.status,
                        notes = req.progress.notes
                    )
                }
            )
            Result.success(detail)
        } catch (e: Exception) {
            authHandler.handleError(e)
            Result.failure(e)
        }
    }
}
