// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateStudentProgressRequest(
    val instructorId: Long? = null,
    val attempts: Int? = null,
    val notes: String? = null
)
