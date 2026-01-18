// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.responses

import com.gma.tsunjo.school.domain.models.Level
import com.gma.tsunjo.school.domain.models.Move
import com.gma.tsunjo.school.domain.models.ProgressState
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class StudentProgressWithDetails(
    val id: Long,
    val studentId: Long,
    val levelRequirementId: Long,
    val level: Level?,
    val move: Move?,
    val status: ProgressState,
    val completedAt: LocalDateTime?,
    val instructorId: Long?,
    val instructorName: String?,
    val attempts: Int,
    val notes: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
