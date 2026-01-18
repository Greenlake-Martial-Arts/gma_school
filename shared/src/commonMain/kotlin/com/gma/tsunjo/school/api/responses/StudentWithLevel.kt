// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.api.responses

import com.gma.tsunjo.school.domain.models.Level
import com.gma.tsunjo.school.domain.models.Student
import kotlinx.serialization.Serializable

@Serializable
data class StudentWithLevel(
    val student: Student,
    val currentLevel: Level?
)
