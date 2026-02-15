// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.students.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gma.tsunjo.school.domain.exceptions.UiErrorMapper
import com.gma.tsunjo.school.domain.exceptions.logToFirebase
import com.gma.tsunjo.school.features.students.data.repository.StudentsRepository
import com.gma.tsunjo.school.features.students.domain.model.Student
import com.gma.tsunjo.school.features.students.domain.model.StudentProgressDetail
import com.gma.tsunjo.school.features.students.domain.model.StudentWithLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class StudentsUiState {
    data object Loading : StudentsUiState()
    data class Success(val students: List<Student>) : StudentsUiState()
    data class Error(val message: String) : StudentsUiState()
}

sealed class ActiveStudentsUiState {
    data object Loading : ActiveStudentsUiState()
    data class Success(val students: List<StudentWithLevel>) : ActiveStudentsUiState()
    data class Error(val message: String) : ActiveStudentsUiState()
}

sealed class StudentProgressDetailUiState {
    data object Loading : StudentProgressDetailUiState()
    data class Success(val detail: StudentProgressDetail) : StudentProgressDetailUiState()
    data class Error(val message: String) : StudentProgressDetailUiState()
}

class StudentsViewModel(
    private val studentsRepository: StudentsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudentsUiState>(StudentsUiState.Loading)
    val uiState: StateFlow<StudentsUiState> = _uiState.asStateFlow()

    private val _activeStudentsUiState = MutableStateFlow<ActiveStudentsUiState>(ActiveStudentsUiState.Loading)
    val activeStudentsUiState: StateFlow<ActiveStudentsUiState> = _activeStudentsUiState.asStateFlow()

    private val _studentProgressDetailUiState = MutableStateFlow<StudentProgressDetailUiState>(StudentProgressDetailUiState.Loading)
    val studentProgressDetailUiState: StateFlow<StudentProgressDetailUiState> = _studentProgressDetailUiState.asStateFlow()

    private val _selectedStudents = MutableStateFlow<Set<String>>(emptySet())
    val selectedStudents: StateFlow<Set<String>> = _selectedStudents.asStateFlow()

    fun loadActiveStudents() {
        _activeStudentsUiState.value = ActiveStudentsUiState.Loading
        viewModelScope.launch {
            studentsRepository.getActiveStudents()
                .onSuccess { students ->
                    _activeStudentsUiState.value = ActiveStudentsUiState.Success(students)
                }
                .onFailure { error ->
                    error.logToFirebase()
                    _activeStudentsUiState.value = ActiveStudentsUiState.Error(UiErrorMapper.toMessage(error))
                }
        }
    }

    fun loadStudentProgress(studentId: Long, studentName: String) {
        _studentProgressDetailUiState.value = StudentProgressDetailUiState.Loading
        viewModelScope.launch {
            studentsRepository.getStudentProgress(studentId, studentName)
                .onSuccess { detail ->
                    _studentProgressDetailUiState.value = StudentProgressDetailUiState.Success(detail)
                }
                .onFailure { error ->
                    error.logToFirebase()
                    _studentProgressDetailUiState.value = StudentProgressDetailUiState.Error(UiErrorMapper.toMessage(error))
                }
        }
    }

    fun createStudentProgress(
        studentId: Long,
        levelRequirementId: Long,
        status: com.gma.tsunjo.school.domain.models.ProgressState,
        notes: String?
    ) {
        viewModelScope.launch {
            studentsRepository.createStudentProgress(
                studentId = studentId,
                levelRequirementId = levelRequirementId,
                status = status,
                notes = notes
            ).onSuccess {
                loadStudentProgress(studentId, "")
            }.onFailure { error ->
                error.logToFirebase()
                _studentProgressDetailUiState.value = StudentProgressDetailUiState.Error(UiErrorMapper.toMessage(error))
            }
        }
    }

    fun updateStudentProgress(
        studentId: Long,
        progressId: Long,
        status: com.gma.tsunjo.school.domain.models.ProgressState,
        notes: String?
    ) {
        viewModelScope.launch {
            studentsRepository.updateStudentProgress(
                progressId = progressId,
                status = status,
                notes = notes
            ).onSuccess {
                loadStudentProgress(studentId, "")
            }.onFailure { error ->
                error.logToFirebase()
                _studentProgressDetailUiState.value = StudentProgressDetailUiState.Error(UiErrorMapper.toMessage(error))
            }
        }
    }

    private fun loadAllStudents() {
        viewModelScope.launch {
            studentsRepository.getStudents()
                .onSuccess { students ->
                    _uiState.value = StudentsUiState.Success(students)
                }
                .onFailure { error ->
                    error.logToFirebase()
                    _uiState.value = StudentsUiState.Error(UiErrorMapper.toMessage(error))
                }
        }
    }

    fun toggleStudent(studentId: String) {
        val current = _selectedStudents.value.toMutableSet()
        if (current.contains(studentId)) {
            current.remove(studentId)
        } else {
            current.add(studentId)
        }
        _selectedStudents.value = current
    }

    fun clearSelection() {
        _selectedStudents.value = emptySet()
    }
}
