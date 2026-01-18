// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.requests

import com.gma.tsunjo.school.domain.models.ProgressState
import kotlinx.serialization.Serializable

@Serializable
data class BulkUpdateProgressRequest(
    val progressIds: List<Long>,
    val status: ProgressState? = null,
    val instructorId: Long? = null,
    val attempts: Int? = null,
    val notes: String? = null
)
