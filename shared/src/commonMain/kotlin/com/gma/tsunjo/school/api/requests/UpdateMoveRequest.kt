// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateMoveRequest(
    val name: String? = null,
    val description: String? = null,
    val categoryId: Long? = null
)
