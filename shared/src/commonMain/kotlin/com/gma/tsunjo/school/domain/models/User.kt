// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val email: String,
    val fullName: String?,
    val studentId: Long?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
