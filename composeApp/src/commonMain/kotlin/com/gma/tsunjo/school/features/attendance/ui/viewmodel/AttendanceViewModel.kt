// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.attendance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gma.tsunjo.school.domain.exceptions.UiErrorMapper
import com.gma.tsunjo.school.features.attendance.data.repository.AttendanceRepository
import com.gma.tsunjo.school.features.attendance.domain.model.AttendanceClass
import com.gma.tsunjo.school.features.attendance.domain.model.AttendanceRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AttendanceUiState {
    data object Loading : AttendanceUiState()
    data class Success(val classes: List<AttendanceClass>) : AttendanceUiState()
    data class Error(val message: String) : AttendanceUiState()
}

sealed class AttendanceDetailUiState {
    data object Loading : AttendanceDetailUiState()
    data class Success(val record: AttendanceRecord) : AttendanceDetailUiState()
    data class Error(val message: String) : AttendanceDetailUiState()
}

class AttendanceViewModel(
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Loading)
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _detailUiState = MutableStateFlow<AttendanceDetailUiState>(AttendanceDetailUiState.Loading)
    val detailUiState: StateFlow<AttendanceDetailUiState> = _detailUiState.asStateFlow()

    fun loadClassesForDate(date: String) {
        _selectedDate.value = date
        _uiState.value = AttendanceUiState.Loading

        viewModelScope.launch {
            attendanceRepository.getClassesForDate(date)
                .onSuccess { classes ->
                    _uiState.value = AttendanceUiState.Success(classes)
                }
                .onFailure { error ->
                    _uiState.value = AttendanceUiState.Error(UiErrorMapper.toMessage(error))
                }
        }
    }

    fun refreshClasses() {
        if (_selectedDate.value.isNotEmpty()) {
            loadClassesForDate(_selectedDate.value)
        }
    }

    fun loadAttendanceRecord(classId: String, date: String) {
        _detailUiState.value = AttendanceDetailUiState.Loading

        viewModelScope.launch {
            attendanceRepository.getAttendanceRecord(classId, date)
                .onSuccess { record ->
                    _detailUiState.value = AttendanceDetailUiState.Success(record)
                }
                .onFailure { error ->
                    _detailUiState.value = AttendanceDetailUiState.Error(UiErrorMapper.toMessage(error))
                }
        }
    }

    fun updateAttendance(recordId: String, studentIds: List<String>) {
        viewModelScope.launch {
            attendanceRepository.updateAttendance(recordId, studentIds)
                .onSuccess { record ->
                    _detailUiState.value = AttendanceDetailUiState.Success(record)
                }
                .onFailure { error ->
                    _detailUiState.value = AttendanceDetailUiState.Error(UiErrorMapper.toMessage(error))
                }
        }
    }
}
