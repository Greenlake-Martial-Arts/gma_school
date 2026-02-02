// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.attendance.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceClass(
    val id: String,
    val name: String,
    val time: String,
    val date: String,
    val attendanceCount: Int,
    val maxCapacity: Int,
    val isCurrentTime: Boolean = false
)

@Serializable
data class AttendanceRecord(
    val id: String,
    val classId: String,
    val studentIds: List<String>,
    val date: String,
    val createdAt: String
)

@Serializable
data class CreateAttendanceRequest(
    val classId: String,
    val studentIds: List<String>,
    val date: String
)
