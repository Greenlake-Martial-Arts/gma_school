// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateStudentProgressRequest(
    val studentId: Long,
    val levelRequirementId: Long,
    val instructorId: Long? = null,
    val attempts: Int = 0,
    val notes: String? = null
)
