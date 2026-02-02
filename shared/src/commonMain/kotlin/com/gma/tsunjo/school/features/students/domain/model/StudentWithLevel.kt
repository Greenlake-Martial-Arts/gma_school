// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.features.students.domain.model

data class StudentWithLevel(
    val id: Long,
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val isActive: Boolean,
    val currentLevel: String,
    val code: String
)
