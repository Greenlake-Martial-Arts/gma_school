// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.auth

import co.touchlab.kermit.Logger
import com.gma.tsunjo.school.domain.exceptions.AppException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AuthenticationHandler(
    private val tokenManager: TokenManager
) {
    private val log = Logger.withTag("AuthenticationHandler")
    
    private val _authenticationFailed = MutableSharedFlow<Unit>(replay = 0)
    val authenticationFailed: SharedFlow<Unit> = _authenticationFailed.asSharedFlow()
    
    suspend fun handleError(error: Throwable) {
        if (error is AppException.Unauthorized || error is AppException.InvalidCredentials) {
            log.w { "<< Authentication failed (401), clearing tokens" }
            tokenManager.clearToken()
            _authenticationFailed.emit(Unit)
        }
    }
}
