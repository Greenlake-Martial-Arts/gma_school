// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.requests

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class AssignLevelRequest(
    val studentId: Long,
    val levelId: Long,
    val achievedDate: LocalDate
)
