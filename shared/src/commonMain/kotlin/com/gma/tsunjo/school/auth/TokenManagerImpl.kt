// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.auth

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings

class TokenManagerImpl(
    private val settings: Settings
) : TokenManager {

    private val log = Logger.withTag("TokenManager")

    override suspend fun saveToken(token: String) {
        settings.putString(KEY_TOKEN, token)
        log.d { "<< Token saved successfully" }
    }

    override fun getToken(): String? {
        val token = settings.getStringOrNull(KEY_TOKEN)
        log.d { "<< Token retrieved: ${if (token != null) "[PRESENT]" else "[ABSENT]"}" }
        return token
    }

    override suspend fun clearToken() {
        settings.remove(KEY_TOKEN)
        log.d { "<< Token cleared" }
    }

    override fun isAuthenticated(): Boolean {
        val authenticated = getToken() != null
        log.d { "<< Authentication check: $authenticated" }
        return authenticated
    }

    companion object {
        private const val KEY_TOKEN = "auth_token"
    }
}
