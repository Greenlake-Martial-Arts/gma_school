// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.tsunjo.school.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class StudentProgress(
    val id: Long,
    val studentId: Long,
    val levelRequirementId: Long,
    val completedAt: LocalDateTime?,
    val instructorId: Long?,
    val attempts: Int,
    val notes: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
