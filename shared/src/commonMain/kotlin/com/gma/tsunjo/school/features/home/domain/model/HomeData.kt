// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.home.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class HomeData(
    val userName: String,
    val greeting: String,
    val upcomingClass: UpcomingClass?
)

@Serializable
data class UpcomingClass(
    val name: String,
    val time: String,
    val registeredStudents: Int
)
