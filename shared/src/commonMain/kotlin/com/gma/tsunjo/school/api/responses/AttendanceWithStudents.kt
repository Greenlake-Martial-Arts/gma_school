// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.responses

import com.gma.tsunjo.school.domain.models.Attendance
import com.gma.tsunjo.school.domain.models.Student
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceWithStudents(
    val attendance: Attendance,
    val students: List<Student>
)
