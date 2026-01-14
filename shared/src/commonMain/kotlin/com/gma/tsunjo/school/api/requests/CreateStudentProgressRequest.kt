// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateStudentProgressRequest(
    val studentId: Long,
    val levelRequirementId: Long,
    val instructorId: Long?,
    val attempts: Int,
    val notes: String?
)
