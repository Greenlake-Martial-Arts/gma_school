// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateLevelRequest(
    val code: String,
    val displayName: String,
    val orderSeq: Int,
    val description: String?
)
