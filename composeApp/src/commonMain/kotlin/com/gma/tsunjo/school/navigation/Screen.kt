// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Login : Screen
    
    @Serializable
    data object Dashboard : Screen
}
