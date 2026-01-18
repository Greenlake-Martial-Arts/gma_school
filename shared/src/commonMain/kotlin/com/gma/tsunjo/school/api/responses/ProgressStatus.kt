// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.responses

import com.gma.tsunjo.school.domain.models.ProgressState
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ProgressStatus(
    val id: Long?,
    val status: ProgressState,
    val completedAt: LocalDateTime?,
    val instructor: InstructorInfo?,
    val attempts: Int,
    val notes: String?,
    val lastUpdated: LocalDateTime?
)
