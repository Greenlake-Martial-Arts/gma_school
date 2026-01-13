// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.tsunjo.school.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Move(
    val id: Long,
    val name: String,
    val description: String?,
    val moveCategoryId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
