package com.gma.tsunjo.school

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Tsun Jo",
        state = rememberWindowState(width = 900.dp, height = 900.dp)
    ) {
        App()
    }
}