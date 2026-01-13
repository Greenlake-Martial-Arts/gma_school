// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.tsunjo.school.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceEntry(
    val attendanceId: Long,
    val studentId: Long
)
