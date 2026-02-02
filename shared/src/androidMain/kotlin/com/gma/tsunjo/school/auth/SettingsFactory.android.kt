// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.auth

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

private lateinit var appContext: Context

fun initAndroidSettings(context: Context) {
    appContext = context.applicationContext
}

actual fun createSettings(): Settings {
    val sharedPreferences = appContext.getSharedPreferences("gma_school_prefs", Context.MODE_PRIVATE)
    return SharedPreferencesSettings(sharedPreferences)
}
