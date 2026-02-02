// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class LevelRequirementWithDetails(
    val id: Long,
    val level: Level,
    val move: Move,
    val sortOrder: Int,
    val levelSpecificNotes: String?,
    val isRequired: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
