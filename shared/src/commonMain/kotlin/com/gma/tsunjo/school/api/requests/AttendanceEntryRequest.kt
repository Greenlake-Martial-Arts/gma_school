// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceEntryRequest(
    val studentId: Long,
    val present: Boolean,
    val notes: String?
)
