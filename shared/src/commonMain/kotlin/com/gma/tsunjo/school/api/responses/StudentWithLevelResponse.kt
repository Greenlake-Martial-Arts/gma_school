// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.responses

import kotlinx.serialization.Serializable

@Serializable
data class StudentWithLevelResponse(
    val student: StudentDetails,
    val currentLevel: LevelDetails
)

@Serializable
data class StudentDetails(
    val id: Long,
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean
)

@Serializable
data class LevelDetails(
    val code: String,
    val displayName: String
)
