// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.tsunjo.school.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Level(
    val id: Long,
    val code: String,
    val displayName: String,
    val orderSeq: Int,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
