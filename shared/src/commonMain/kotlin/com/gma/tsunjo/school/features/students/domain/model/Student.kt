// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.students.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val id: String,
    val name: String,
    val rankBadge: String,
    val rankColor: String
)
