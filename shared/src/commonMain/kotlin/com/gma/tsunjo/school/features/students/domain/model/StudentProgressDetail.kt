// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.features.students.domain.model

import com.gma.tsunjo.school.domain.models.ProgressState

data class StudentProgressDetail(
    val studentId: Long,
    val studentName: String,
    val levelName: String,
    val requirements: List<RequirementItem>
)

data class RequirementItem(
    val moveId: Long,
    val moveName: String,
    val progressId: Long?,
    val status: ProgressState,
    val notes: String?
)
