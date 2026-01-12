// © 2025-2026 Hector Torres - Greenlake Martial Arts
package com.gma.tsunjo.school.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class AuditLog(
    val id: Long,
    val userId: Long,
    val action: String,
    val entity: String,
    val entityId: Long?,
    val description: String?,
    val userAgent: String?,
    val createdAt: LocalDateTime
)
