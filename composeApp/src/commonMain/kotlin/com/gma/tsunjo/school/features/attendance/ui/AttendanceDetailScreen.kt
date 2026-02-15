// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.attendance.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gma.tsunjo.school.features.attendance.ui.viewmodel.AttendanceDetailUiState
import com.gma.tsunjo.school.features.attendance.ui.viewmodel.AttendanceViewModel
import com.gma.tsunjo.school.features.students.ui.viewmodel.StudentsUiState
import com.gma.tsunjo.school.features.students.ui.viewmodel.StudentsViewModel
import com.gma.tsunjo.school.theme.BlackSash
import com.gma.tsunjo.school.theme.BlueSash
import com.gma.tsunjo.school.theme.BrownSash
import com.gma.tsunjo.school.theme.GMATheme
import com.gma.tsunjo.school.theme.GreenSash
import com.gma.tsunjo.school.theme.WhiteSash
import com.gma.tsunjo.school.ui.components.SearchableTopBar
import com.gma.tsunjo.school.ui.components.StudentItem
import com.gma.tsunjo.school.ui.components.StudentSelectionList
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceDetailScreen(
    attendanceId: Long,
    className: String,
    onNavigateBack: () -> Unit,
    studentsViewModel: StudentsViewModel = koinViewModel(),
    attendanceViewModel: AttendanceViewModel = koinViewModel()
) {
    val activeStudentsUiState by studentsViewModel.activeStudentsUiState.collectAsState()
    val detailUiState by attendanceViewModel.detailUiState.collectAsState()
    val selectedStudents by studentsViewModel.selectedStudents.collectAsState()

    LaunchedEffect(Unit) {
        studentsViewModel.loadActiveStudents()
    }

    LaunchedEffect(attendanceId) {
        attendanceViewModel.loadAttendanceDetail(attendanceId)
    }

    LaunchedEffect(detailUiState) {
        if (detailUiState is AttendanceDetailUiState.Success) {
            val attendance = (detailUiState as AttendanceDetailUiState.Success).attendance
            studentsViewModel.clearSelection()
            attendance.students.forEach { student ->
                studentsViewModel.toggleStudent(student.id.toString())
            }
        }
    }

    val students = when (activeStudentsUiState) {
        is com.gma.tsunjo.school.features.students.ui.viewmodel.ActiveStudentsUiState.Success -> 
            (activeStudentsUiState as com.gma.tsunjo.school.features.students.ui.viewmodel.ActiveStudentsUiState.Success).students.map {
                com.gma.tsunjo.school.features.students.domain.model.Student(
                    id = it.id.toString(),
                    name = it.fullName,
                    rankBadge = it.currentLevel,
                    rankColor = it.code
                )
            }
        else -> emptyList()
    }

    AttendanceDetailView(
        className = className,
        students = students,
        selectedStudents = selectedStudents,
        isLoading = activeStudentsUiState is com.gma.tsunjo.school.features.students.ui.viewmodel.ActiveStudentsUiState.Loading || detailUiState is AttendanceDetailUiState.Loading,
        onNavigateBack = onNavigateBack,
        onStudentToggle = { studentsViewModel.toggleStudent(it) },
        onSaveAttendance = {
            val studentIds = selectedStudents.map { it.toLong() }
            attendanceViewModel.addStudentsToAttendance(attendanceId, studentIds)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceDetailView(
    className: String,
    students: List<com.gma.tsunjo.school.features.students.domain.model.Student>,
    selectedStudents: Set<String>,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onStudentToggle: (String) -> Unit,
    onSaveAttendance: () -> Unit
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    val filteredStudents = remember(students, searchQuery) {
        if (searchQuery.isBlank()) {
            students
        } else {
            students.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            SearchableTopBar(
                title = "Attendance - $className",
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                isSearchActive = isSearchActive,
                onSearchActiveChange = { isSearchActive = it },
                showSearch = false,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                else -> {
                    val studentItems = filteredStudents.map { student ->
                        StudentItem(
                            id = student.id,
                            name = student.name,
                            rankBadge = student.rankBadge,
                            rankColor = getRankColor(student.rankColor),
                            isSelected = selectedStudents.contains(student.id)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StudentSelectionList(
                            students = studentItems,
                            onStudentToggle = onStudentToggle,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = { showSaveDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedStudents.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Update Attendance",
                                modifier = Modifier.padding(vertical = 8.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Update Attendance") },
            text = { Text("Update attendance for ${selectedStudents.size} student(s)?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSaveDialog = false
                        onSaveAttendance()
                        onNavigateBack()
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun getRankColor(colorName: String): Color {
    return when (colorName.lowercase()) {
        "white" -> WhiteSash
        "blue" -> BlueSash
        "green" -> GreenSash
        "brown" -> BrownSash
        "black" -> BlackSash
        else -> WhiteSash
    }
}


@Composable
private fun AttendanceDetailPreviewContent() {
    AttendanceDetailView(
        className = "6:00 PM Class",
        students = listOf(
            com.gma.tsunjo.school.features.students.domain.model.Student("1", "John Doe", "White Sash", "White"),
            com.gma.tsunjo.school.features.students.domain.model.Student("2", "Jane Smith", "Blue Sash", "Blue"),
            com.gma.tsunjo.school.features.students.domain.model.Student("3", "Bob Johnson", "Green Sash", "Green")
        ),
        isLoading = false,
        selectedStudents = setOf("1", "3"),
        onNavigateBack = {},
        onStudentToggle = {},
        onSaveAttendance = {}
    )
}

@Preview
@Composable
fun AttendanceDetailPreview() {
    GMATheme {
        AttendanceDetailPreviewContent()
    }
}

@Preview
@Composable
fun AttendanceDetailPreviewDark() {
    GMATheme(darkTheme = true) {
        AttendanceDetailPreviewContent()
    }
}
