// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.home.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gma.tsunjo.school.features.home.data.repository.HomeRepository
import com.gma.tsunjo.school.domain.exceptions.UiErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val userName: String,
        val greeting: String,
        val upcomingClass: UpcomingClass?
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()

    data class UpcomingClass(
        val name: String,
        val time: String,
        val registeredStudents: Int
    )
}

class HomeViewModel(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Success(
        userName = "Jacob",
        greeting = "Hi Instructor",
        upcomingClass = HomeUiState.UpcomingClass(
            name = "Tsun Jo Advance\n with Instructor Matt",
            time = "9:00 AM",
            registeredStudents = 8
    )))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
//        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            homeRepository.getHomeData()
                .onSuccess { data ->
                    _uiState.value = HomeUiState.Success(
                        userName = data.userName,
                        greeting = data.greeting,
                        upcomingClass = data.upcomingClass?.let {
                            HomeUiState.UpcomingClass(
                                name = it.name,
                                time = it.time,
                                registeredStudents = it.registeredStudents
                            )
                        }
                    )
                }
                .onFailure { error ->
                    _uiState.value = HomeUiState.Error(UiErrorMapper.toMessage(error))
                }
        }
    }

    fun navigateToRoster() {
        // TODO: Implement roster navigation
    }
}
