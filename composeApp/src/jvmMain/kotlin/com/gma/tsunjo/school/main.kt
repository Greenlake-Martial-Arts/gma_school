package com.gma.tsunjo.school

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "gma_school",
    ) {
        App()
    }
}