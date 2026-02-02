// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun getLevelColor(code: String?): Color {
    return when (code?.uppercase()) {
        "WHITE" -> WhiteSash
        "GREEN" -> GreenSash
        "BLUE" -> BlueSash
        "BROWN" -> BrownSash
        "BLACK" -> BlackSash
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}
