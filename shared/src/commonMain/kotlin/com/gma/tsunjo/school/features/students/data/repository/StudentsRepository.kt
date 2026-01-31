// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.students.data.repository

import com.gma.tsunjo.school.features.students.data.remote.StudentsApi
import com.gma.tsunjo.school.features.students.domain.model.Student

class StudentsRepository(
    private val studentsApi: StudentsApi
) {
    suspend fun getStudents(): Result<List<Student>> {
        // TODO: Remove dummy data when API is ready
        return Result.success(
            listOf(
                Student("1", "Alex Rivers", "White Sash", "White"),
                Student("2", "Alice Morgan", "Blue Sash", "Blue"),
                Student("3", "Bob Chen", "Green Sash", "Green"),
                Student("4", "Charlie Davis", "Brown Sash", "Brown"),
                Student("5", "Diana Evans", "Black Sash", "Black"),
                Student("6", "Emma Wilson", "White Sash", "White"),
                Student("7", "Frank Martinez", "Blue Sash", "Blue"),
                Student("8", "Grace Lee", "Green Sash", "Green")
            )
        )
        
        /* Real API call - uncomment when ready
        return try {
            val students = studentsApi.getStudents()
            Result.success(students)
        } catch (e: Exception) {
            Result.failure(e)
        }
        */
    }
}
