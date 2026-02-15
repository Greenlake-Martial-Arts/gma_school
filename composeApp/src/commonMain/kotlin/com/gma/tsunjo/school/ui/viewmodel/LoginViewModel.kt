// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.gma.tsunjo.school.api.responses.UserInfo
import com.gma.tsunjo.school.auth.TokenManager
import com.gma.tsunjo.school.data.repository.LoginRepository
import com.gma.tsunjo.school.domain.exceptions.UiErrorMapper
import com.gma.tsunjo.school.domain.exceptions.logToFirebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val user: UserInfo, val token: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val log = Logger.withTag("LoginViewModel")
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        log.d { "<< Login attempt for user: $username" }
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            loginRepository.login(username, password)
                .onSuccess { response ->
                    log.d { "<< Login successful for user: ${response.user.username}" }
                    tokenManager.saveToken(response.token)
                    _uiState.value = LoginUiState.Success(response.user, response.token)
                }
                .onFailure { error ->
                    log.e { "<< Login failed: ${error.message}" }
                    error.logToFirebase()
                    _uiState.value = LoginUiState.Error(UiErrorMapper.toMessage(error))
                }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
