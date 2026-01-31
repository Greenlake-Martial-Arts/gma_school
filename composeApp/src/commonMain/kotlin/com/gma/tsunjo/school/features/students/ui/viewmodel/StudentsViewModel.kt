// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.students.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gma.tsunjo.school.domain.exceptions.UiErrorMapper
import com.gma.tsunjo.school.features.students.data.repository.StudentsRepository
import com.gma.tsunjo.school.features.students.domain.model.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class StudentsUiState {
    data object Loading : StudentsUiState()
    data class Success(val students: List<Student>) : StudentsUiState()
    data class Error(val message: String) : StudentsUiState()
}

class StudentsViewModel(
    private val studentsRepository: StudentsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudentsUiState>(StudentsUiState.Loading)
    val uiState: StateFlow<StudentsUiState> = _uiState.asStateFlow()

    private val _selectedStudents = MutableStateFlow<Set<String>>(emptySet())
    val selectedStudents: StateFlow<Set<String>> = _selectedStudents.asStateFlow()

    init {
        loadStudents()
    }

    private fun loadStudents() {
        viewModelScope.launch {
            studentsRepository.getStudents()
                .onSuccess { students ->
                    _uiState.value = StudentsUiState.Success(students)
                }
                .onFailure { error ->
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
