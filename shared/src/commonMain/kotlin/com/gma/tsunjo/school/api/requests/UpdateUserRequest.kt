// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val email: String? = null,
    val fullName: String? = null,
    val isActive: Boolean? = null
)
