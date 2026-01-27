// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.responses

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val user: UserInfo
)
