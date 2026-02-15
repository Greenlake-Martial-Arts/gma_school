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
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAttendanceScreen(
    classTime: String,
    onNavigateBack: () -> Unit,
    studentsViewModel: StudentsViewModel = koinViewModel(),
    attendanceViewModel: com.gma.tsunjo.school.features.attendance.ui.viewmodel.AttendanceViewModel = koinViewModel()
) {
    val activeStudentsUiState by studentsViewModel.activeStudentsUiState.collectAsState()
    val selectedStudents by studentsViewModel.selectedStudents.collectAsState()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        studentsViewModel.loadActiveStudents()
    }

    val students = when (activeStudentsUiState) {
        is com.gma.tsunjo.school.features.students.ui.viewmodel.ActiveStudentsUiState.Success -> 
            (activeStudentsUiState as com.gma.tsunjo.school.features.students.ui.viewmodel.ActiveStudentsUiState.Success).students.map {
                com.gma.tsunjo.school.ui.components.StudentItem(
                    id = it.id.toString(),
                    name = it.fullName,
                    rankBadge = it.currentLevel,
                    rankColor = com.gma.tsunjo.school.theme.getLevelColor(it.code),
                    isSelected = false
                )
            }
        else -> emptyList()
    }

    NewAttendanceView(
        classTime = classTime,
        students = students,
        selectedStudents = selectedStudents,
        isLoading = activeStudentsUiState is com.gma.tsunjo.school.features.students.ui.viewmodel.ActiveStudentsUiState.Loading,
        onNavigateBack = onNavigateBack,
        onStudentToggle = { studentsViewModel.toggleStudent(it) },
        onSaveAttendance = {
            val studentIds = selectedStudents.map { it.toLong() }
            attendanceViewModel.createAttendanceForToday(classTime, studentIds) {
                onNavigateBack()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAttendanceView(
    classTime: String,
    students: List<com.gma.tsunjo.school.ui.components.StudentItem>,
    selectedStudents: Set<String>,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onStudentToggle: (String) -> Unit,
    onSaveAttendance: () -> Unit
) {
    var showBackDialog by remember { mutableStateOf(false) }
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
                title = "Attendance - $classTime",
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                isSearchActive = isSearchActive,
                onSearchActiveChange = { isSearchActive = it },
                showSearch = true,
                onNavigateBack = {
                    if (selectedStudents.isNotEmpty()) {
                        showBackDialog = true
                    } else {
                        onNavigateBack()
                    }
                }
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
                        student.copy(isSelected = selectedStudents.contains(student.id))
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
                                "Save Attendance",
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

    if (showBackDialog) {
        AlertDialog(
            onDismissRequest = { showBackDialog = false },
            title = { Text("Cancel Attendance?") },
            text = { Text("You have selected ${selectedStudents.size} student(s). Are you sure you want to cancel?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showBackDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Yes, Cancel", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackDialog = false }) {
                    Text("Continue")
                }
            }
        )
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Attendance") },
            text = { Text("Save attendance for ${selectedStudents.size} student(s)?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSaveDialog = false
                        onSaveAttendance()
                        onNavigateBack()
                    }
                ) {
                    Text("Save")
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
private fun NewAttendancePreviewContent() {
    NewAttendanceView(
        classTime = "6:00 PM Class",
        students = listOf(
            com.gma.tsunjo.school.ui.components.StudentItem("1", "John Doe", "White Sash", WhiteSash),
            com.gma.tsunjo.school.ui.components.StudentItem("2", "Jane Smith", "Blue Sash", BlueSash),
            com.gma.tsunjo.school.ui.components.StudentItem("3", "Bob Johnson", "Green Sash", GreenSash)
        ),
        isLoading = false,
        selectedStudents = setOf("2"),
        onNavigateBack = {},
        onStudentToggle = {},
        onSaveAttendance = {}
    )
}

@Preview
@Composable
fun NewAttendancePreview() {
    GMATheme {
        NewAttendancePreviewContent()
    }
}

@Preview
@Composable
fun NewAttendancePreviewDark() {
    GMATheme(darkTheme = true) {
        NewAttendancePreviewContent()
    }
}
