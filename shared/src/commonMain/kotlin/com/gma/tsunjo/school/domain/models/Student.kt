// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val id: Long,
    val userId: Long,
    val externalCode: String?,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val memberTypeId: Long,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
