// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school

import androidx.compose.runtime.Composable
import com.gma.tsunjo.school.di.appModule
import com.gma.tsunjo.school.di.platformModule
import com.gma.tsunjo.school.di.sharedModule
import com.gma.tsunjo.school.navigation.AppNavigation
import com.gma.tsunjo.school.theme.GMATheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(
        application = {
            modules(
                platformModule(),
                sharedModule(),
                appModule
            )
        }
    ) {
        GMATheme {
            AppNavigation()
        }
    }
}
