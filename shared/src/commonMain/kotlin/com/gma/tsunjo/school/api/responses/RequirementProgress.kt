// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.responses

import com.gma.tsunjo.school.domain.models.Move
import kotlinx.serialization.Serializable

@Serializable
data class RequirementProgress(
    val id: Long,
    val sortOrder: Int,
    val move: Move,
    val isRequired: Boolean,
    val levelSpecificNotes: String?,
    val progress: ProgressStatus
)
