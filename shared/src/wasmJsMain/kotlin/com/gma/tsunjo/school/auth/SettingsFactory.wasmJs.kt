// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.auth

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings

// WasmJS uses localStorage via StorageSettings
actual fun createSettings(): Settings {
    return StorageSettings() // Uses localStorage by default
}
