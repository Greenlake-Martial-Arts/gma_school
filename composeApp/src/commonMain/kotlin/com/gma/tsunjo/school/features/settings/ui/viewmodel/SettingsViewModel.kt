// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.features.settings.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.gma.tsunjo.school.auth.TokenManager
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val log = Logger.withTag("SettingsViewModel")

    fun logout(onComplete: () -> Unit) {
        log.i { "<< Logout initiated" }
        viewModelScope.launch {
            tokenManager.clearToken()
            log.i { "<< Token cleared, logout complete" }
            onComplete()
        }
    }
}
