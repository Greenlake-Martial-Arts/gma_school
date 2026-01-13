// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateLevelRequirementRequest(
    val levelId: Long,
    val moveId: Long,
    val requiredCount: Int
)
