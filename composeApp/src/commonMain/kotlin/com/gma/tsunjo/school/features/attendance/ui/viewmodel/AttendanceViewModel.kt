// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.attendance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gma.tsunjo.school.api.responses.AttendanceWithStudents
import com.gma.tsunjo.school.domain.exceptions.UiErrorMapper
import com.gma.tsunjo.school.domain.exceptions.logToFirebase
import com.gma.tsunjo.school.domain.models.Attendance
import com.gma.tsunjo.school.features.attendance.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

sealed class AttendanceListUiState {
    data object Loading : AttendanceListUiState()
    data class Success(val attendances: List<Attendance>) : AttendanceListUiState()
    data class Error(val message: String) : AttendanceListUiState()
}

sealed class AttendanceDetailUiState {
    data object Loading : AttendanceDetailUiState()
    data class Success(val attendance: AttendanceWithStudents) : AttendanceDetailUiState()
    data class Error(val message: String) : AttendanceDetailUiState()
}

class AttendanceViewModel(
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _listUiState = MutableStateFlow<AttendanceListUiState>(AttendanceListUiState.Loading)
    val listUiState: StateFlow<AttendanceListUiState> = _listUiState.asStateFlow()

    private val _detailUiState = MutableStateFlow<AttendanceDetailUiState>(AttendanceDetailUiState.Loading)
    val detailUiState: StateFlow<AttendanceDetailUiState> = _detailUiState.asStateFlow()

    private val _selectedStartDate = MutableStateFlow(getTodayDate())
    val selectedStartDate: StateFlow<LocalDate> = _selectedStartDate.asStateFlow()

    private val _selectedEndDate = MutableStateFlow(getTodayDate())
    val selectedEndDate: StateFlow<LocalDate> = _selectedEndDate.asStateFlow()

    private fun getTodayDate(): LocalDate {
        val now = kotlin.time.Clock.System.now()
        val millis = now.toEpochMilliseconds()
        val seconds = millis / 1000
        val days = seconds / 86400 // seconds per day
        val epochDays = days.toInt()
        return kotlinx.datetime.LocalDate.fromEpochDays(epochDays)
    }

    fun loadAttendancesForDateRange(startDate: LocalDate, endDate: LocalDate) {
        _selectedStartDate.value = startDate
        _selectedEndDate.value = endDate
        _listUiState.value = AttendanceListUiState.Loading

        viewModelScope.launch {
            attendanceRepository.getAttendancesByDateRange(startDate, endDate)
                .onSuccess { attendances ->
                    _listUiState.value = AttendanceListUiState.Success(attendances)
                }
                .onFailure { error ->
                    error.logToFirebase()
                    _listUiState.value = AttendanceListUiState.Error(UiErrorMapper.toMessage(error))
                }
        }
    }

    fun loadAttendancesForToday() {
        val today = getTodayDate()
        loadAttendancesForDateRange(today, today)
    }

    fun loadAttendanceDetail(attendanceId: Long) {
        _detailUiState.value = AttendanceDetailUiState.Loading

        viewModelScope.launch {
            attendanceRepository.getAttendanceWithStudents(attendanceId)
                .onSuccess { attendance ->
                    _detailUiState.value = AttendanceDetailUiState.Success(attendance)
                }
                .onFailure { error ->
                    error.logToFirebase()
                    _detailUiState.value = AttendanceDetailUiState.Error(UiErrorMapper.toMessage(error))
                }
        }
    }

    fun createAttendance(classDate: LocalDate, notes: String?) {
        viewModelScope.launch {
            attendanceRepository.createAttendance(classDate, notes)
                .onSuccess {
                    loadAttendancesForDateRange(_selectedStartDate.value, _selectedEndDate.value)
                }
                .onFailure { error ->
                    error.logToFirebase()
                    _listUiState.value = AttendanceListUiState.Error(UiErrorMapper.toMessage(error))
                }
        }
    }

    fun createAttendanceForToday(notes: String?, studentIds: List<Long>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val today = getTodayDate()
            attendanceRepository.createAttendance(today, notes)
                .onSuccess { attendance ->
                    if (studentIds.isNotEmpty()) {
                        attendanceRepository.addStudentsToAttendance(attendance.id, studentIds)
                            .onSuccess {
                                loadAttendancesForDateRange(_selectedStartDate.value, _selectedEndDate.value)
                                onSuccess()
                            }
                            .onFailure { error ->
                                error.logToFirebase()
                                _listUiState.value = AttendanceListUiState.Error(UiErrorMapper.toMessage(error))
                            }
                    } else {
                        loadAttendancesForDateRange(_selectedStartDate.value, _selectedEndDate.value)
                        onSuccess()
                    }
                }
                .onFailure { error ->
                    error.logToFirebase()
                    _listUiState.value = AttendanceListUiState.Error(UiErrorMapper.toMessage(error))
                }
        }
    }

    fun addStudentsToAttendance(attendanceId: Long, studentIds: List<Long>) {
        viewModelScope.launch {
            attendanceRepository.addStudentsToAttendance(attendanceId, studentIds)
                .onSuccess {
                    loadAttendanceDetail(attendanceId)
                }
                .onFailure { error ->
                    error.logToFirebase()
                    _detailUiState.value = AttendanceDetailUiState.Error(UiErrorMapper.toMessage(error))
                }
        }
    }
}
