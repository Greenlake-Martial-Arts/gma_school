// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.tsunjo.school.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class StudentLevel(
    val studentId: Long,
    val levelId: Long,
    val assignedAt: LocalDateTime
)
