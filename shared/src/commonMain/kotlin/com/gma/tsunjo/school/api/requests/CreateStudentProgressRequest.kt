// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.requests

import com.gma.tsunjo.school.domain.models.ProgressState
import kotlinx.serialization.Serializable

@Serializable
data class CreateStudentProgressRequest(
    val studentId: Long,
    val levelRequirementId: Long,
    val status: ProgressState,
    val instructorId: Long? = null,
    val attempts: Int = 0,
    val notes: String? = null
)
