// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.progress.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gma.tsunjo.school.theme.BlueSash
import com.gma.tsunjo.school.theme.BrownSash
import com.gma.tsunjo.school.theme.GMATheme
import com.gma.tsunjo.school.theme.GreenSash
import com.gma.tsunjo.school.theme.WhiteSash
import com.gma.tsunjo.school.ui.components.BottomNavigationBar
import com.gma.tsunjo.school.ui.components.SearchableTopBar
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StudentProgressScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStudentDetail: (String, String, String, String) -> Unit
) {
    StudentProgressView(
        onNavigateToHome = onNavigateToHome,
        onNavigateToAttendance = onNavigateToAttendance,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToStudentDetail = onNavigateToStudentDetail
    )
}

@Composable
fun StudentProgressView(
    onNavigateToHome: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStudentDetail: (String, String, String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    val mockStudents = listOf(
        com.gma.tsunjo.school.ui.components.StudentItem("1", "Alex Rivers", "White Sash", WhiteSash, false),
        com.gma.tsunjo.school.ui.components.StudentItem("2", "Alice Morgan", "Blue Sash", BlueSash, false),
        com.gma.tsunjo.school.ui.components.StudentItem("3", "Bob Chen", "Green Sash", GreenSash, false),
        com.gma.tsunjo.school.ui.components.StudentItem("4", "Charlie Davis", "Brown Sash", BrownSash, false),
        com.gma.tsunjo.school.ui.components.StudentItem("5", "Diana Evans", "Black Sash", com.gma.tsunjo.school.theme.BlackSash, false)
    )
    
    val filteredStudents = remember(mockStudents, searchQuery) {
        if (searchQuery.isBlank()) {
            mockStudents
        } else {
            mockStudents.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

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
                    student = student,
                    onToggle = {
                        val colorName = when(student.rankColor) {
                            WhiteSash -> "White"
                            BlueSash -> "Blue"
                            GreenSash -> "Green"
                            BrownSash -> "Brown"
                            else -> "Black"
                        }
                        onNavigateToStudentDetail(student.id, student.name, student.rankBadge, colorName)
                    },
                    showCheckbox = false
                )
            }
        }
    }
}

@Composable
private fun StudentProgressPreviewContent() {
    androidx.compose.material3.Surface {
        StudentProgressView(
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
