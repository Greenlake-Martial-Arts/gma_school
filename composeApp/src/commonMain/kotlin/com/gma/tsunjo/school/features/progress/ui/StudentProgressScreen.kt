// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.progress.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gma.tsunjo.school.features.students.domain.model.StudentWithLevel
import com.gma.tsunjo.school.features.students.ui.viewmodel.ActiveStudentsUiState
import com.gma.tsunjo.school.features.students.ui.viewmodel.StudentsViewModel
import com.gma.tsunjo.school.theme.GMATheme
import com.gma.tsunjo.school.theme.getLevelColor
import com.gma.tsunjo.school.ui.components.BottomNavigationBar
import com.gma.tsunjo.school.ui.components.SearchableTopBar
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StudentProgressScreen(
    viewModel: StudentsViewModel = koinViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStudentDetail: (String, String, String, String) -> Unit
) {
    val uiState by viewModel.activeStudentsUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadActiveStudents()
    }

    StudentProgressView(
        uiState = uiState,
        onRetry = { viewModel.loadActiveStudents() },
        onNavigateToHome = onNavigateToHome,
        onNavigateToAttendance = onNavigateToAttendance,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToStudentDetail = onNavigateToStudentDetail
    )
}

@Composable
fun StudentProgressView(
    uiState: ActiveStudentsUiState,
    onRetry: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStudentDetail: (String, String, String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SearchableTopBar(
                title = "Student Progress",
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                isSearchActive = isSearchActive,
                onSearchActiveChange = { isSearchActive = it },
                showSearch = true
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 2,
                onHomeClick = onNavigateToHome,
                onAttendanceClick = onNavigateToAttendance,
                onProgressClick = {},
                onSettingsClick = onNavigateToSettings
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when (uiState) {
            is ActiveStudentsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ActiveStudentsUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = onRetry) {
                            Text("Retry")
                        }
                    }
                }
            }

            is ActiveStudentsUiState.Success -> {
                val filteredStudents = remember(uiState.students, searchQuery) {
                    if (searchQuery.isBlank()) {
                        uiState.students
                    } else {
                        uiState.students.filter { it.fullName.contains(searchQuery, ignoreCase = true) }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(filteredStudents) { student ->
                        com.gma.tsunjo.school.ui.components.StudentSelectionItem(
                            student = com.gma.tsunjo.school.ui.components.StudentItem(
                                id = student.id.toString(),
                                name = student.fullName,
                                rankBadge = student.currentLevel,
                                rankColor = getLevelColor(student.code),
                                isSelected = false
                            ),
                            onToggle = {
                                onNavigateToStudentDetail(
                                    student.id.toString(),
                                    student.fullName,
                                    student.currentLevel,
                                    student.code
                                )
                            },
                            showCheckbox = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentProgressPreviewContent() {
    val mockStudents = listOf(
        StudentWithLevel(
            id = 1,
            userId = 1,
            firstName = "Alex",
            lastName = "Rivers",
            fullName = "Alex Rivers",
            isActive = true,
            currentLevel = "White Sash",
            code = "White"
        ),
        StudentWithLevel(
            id = 2,
            userId = 2,
            firstName = "Sarah",
            lastName = "Chen",
            fullName = "Sarah Chen",
            isActive = true,
            currentLevel = "Blue Sash",
            code = "Blue"
        ),
        StudentWithLevel(
            id = 3,
            userId = 3,
            firstName = "Mike",
            lastName = "Johnson",
            fullName = "Mike Johnson",
            isActive = true,
            currentLevel = "Green Sash",
            code = "Green"
        )
    )

    androidx.compose.material3.Surface {
        StudentProgressView(
            uiState = ActiveStudentsUiState.Success(mockStudents),
            onRetry = {},
            onNavigateToHome = {},
            onNavigateToAttendance = {},
            onNavigateToSettings = {},
            onNavigateToStudentDetail = { _, _, _, _ -> }
        )
    }
}

@Preview
@Composable
fun StudentProgressPreview() {
    GMATheme {
        StudentProgressPreviewContent()
    }
}

@Preview
@Composable
fun StudentProgressPreviewDark() {
    GMATheme(darkTheme = true) {
        StudentProgressPreviewContent()
    }
}
