// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateStudentRequest(
    val externalCode: String? = null,
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val phone: String? = null,
    val memberTypeId: Long
)
