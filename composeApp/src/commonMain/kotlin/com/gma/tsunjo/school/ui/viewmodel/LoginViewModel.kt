// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gma.tsunjo.school.api.responses.UserInfo
import com.gma.tsunjo.school.data.repository.LoginRepository
import com.gma.tsunjo.school.domain.exceptions.UiErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val user: UserInfo, val token: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            // TODO: Remove fake login when API is ready
            if (username != "test") {
                // Fake success for non-test users
                _uiState.value = LoginUiState.Success(
                    user = UserInfo(
                        id = 1,
                        username = username,
                        isActive = true
                    ),
                    token = "fake-token-3434asdsadsadsad"
                )
            } else {
                // Real API call for "test" user
                loginRepository.login(username, password)
                    .onSuccess { response ->
                        _uiState.value = LoginUiState.Success(response.user, response.token)
                    }
                    .onFailure { error ->
                        _uiState.value = LoginUiState.Error(UiErrorMapper.toMessage(error))
                    }
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
