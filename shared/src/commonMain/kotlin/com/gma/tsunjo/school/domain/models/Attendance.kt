// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.tsunjo.school.domain.models

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Attendance(
    val id: Long,
    val classDate: LocalDate,
    val notes: String?,
    val createdAt: LocalDateTime
)
