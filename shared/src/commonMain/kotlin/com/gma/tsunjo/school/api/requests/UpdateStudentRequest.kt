// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.tsunjo.school.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateStudentRequest(
    val externalCode: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val memberTypeId: Long? = null,
    val isActive: Boolean? = null
)
