// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class ProgressState {
    NOT_STARTED,
    IN_PROGRESS,
    PASSED
}
