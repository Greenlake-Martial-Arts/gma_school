// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.attendance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gma.tsunjo.school.features.attendance.domain.model.AttendanceClass
import com.gma.tsunjo.school.features.attendance.ui.viewmodel.AttendanceUiState
import com.gma.tsunjo.school.features.attendance.ui.viewmodel.AttendanceViewModel
import com.gma.tsunjo.school.theme.GMATheme
import com.gma.tsunjo.school.ui.components.BottomNavigationBar
import com.gma.tsunjo.school.ui.components.SearchableTopBar
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToNewAttendance: (String) -> Unit,
    onNavigateToAttendanceDetail: (String, String, String) -> Unit,
    viewModel: AttendanceViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Load classes for initial date
    LaunchedEffect(Unit) {
        viewModel.loadClassesForDate("2026-01-29") // Today
    }

    // Load classes when date picker selection changes
    LaunchedEffect(datePickerState.selectedDateMillis) {
        // For now, just use hardcoded dates for testing
        // When user picks a date, show the mock data for 2026-01-27
        if (datePickerState.selectedDateMillis != null) {
            viewModel.loadClassesForDate("2026-01-27")
        }
    }

    AttendanceView(
        uiState = uiState,
        selectedDate = selectedDate,
        onNavigateToHome = onNavigateToHome,
        onNavigateToProgress = onNavigateToProgress,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToNewAttendance = onNavigateToNewAttendance,
        onNavigateToAttendanceDetail = onNavigateToAttendanceDetail,
        onDateSelected = { viewModel.loadClassesForDate(it) },
        onShowDatePicker = { showDatePicker = it }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AttendanceView(
    uiState: AttendanceUiState,
    selectedDate: String,
    onNavigateToHome: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToNewAttendance: (String) -> Unit,
    onNavigateToAttendanceDetail: (String, String, String) -> Unit,
    onDateSelected: (String) -> Unit,
    onShowDatePicker: (Boolean) -> Unit
) {
    val displayDate = remember(selectedDate) {
        if (selectedDate.isNotEmpty()) {
            val parts = selectedDate.split("-")
            if (parts.size == 3) {
                val day = parts[2].toIntOrNull() ?: 1
                val month = parts[1].toIntOrNull() ?: 1
                "Today, ${getMonthName(month)} ${day}${getDaySuffix(day)}"
            } else {
                "Today"
            }
        } else {
            "Today"
        }
    }

    Scaffold(
        topBar = {
            SearchableTopBar(
                title = "Attendance",
                actionIcon = Icons.Default.CalendarToday,
                onActionClick = { onShowDatePicker(true) }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 1,
                onHomeClick = onNavigateToHome,
                onAttendanceClick = {},
                onProgressClick = onNavigateToProgress,
                onSettingsClick = onNavigateToSettings
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToNewAttendance("6:00 PM Class") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Text(
                text = displayDate,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            when (uiState) {
                is AttendanceUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is AttendanceUiState.Success -> {
                    val classes = (uiState as AttendanceUiState.Success).classes

                    if (classes.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No classes found for this day",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = "CLASSES FOR TODAY",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(classes) { classItem ->
                                ClassSessionItem(
                                    classItem = classItem,
                                    onClick = {
                                        if (classItem.attendanceCount > 0) {
                                            // Has attendance - go to detail
                                            onNavigateToAttendanceDetail(
                                                classItem.id,
                                                classItem.name,
                                                selectedDate
                                            )
                                        } else {
                                            // No attendance - create new
                                            onNavigateToNewAttendance(classItem.name)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                is AttendanceUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = (uiState as AttendanceUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassSessionItem(
    classItem: AttendanceClass,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (classItem.isCurrentTime) {
                    Box(
                        modifier = Modifier
                            .size(4.dp, 60.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(2.dp)
                            )
                    )
                }

                Text(
                    text = classItem.time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (classItem.isCurrentTime) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Text(
                    text = classItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

//            Row(
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "${classItem.attendanceCount}/${classItem.maxCapacity}",
//                    style = MaterialTheme.typography.titleMedium,
//                    color = if (classItem.isCurrentTime) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
//                )
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
//                    contentDescription = "View details",
//                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                    modifier = Modifier.size(20.dp)
//                )
//            }
        }
    }
}

private fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"
        else -> "Unknown"
    }
}

private fun getDaySuffix(day: Int): String {
    return when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }
}

@Composable
private fun AttendanceScreenPreviewContent() {
    androidx.compose.material3.Surface {
        AttendanceView(
            uiState = AttendanceUiState.Success(
                listOf(
                    AttendanceClass("1", "Tsun Jo Class", "6:15 PM", "2026-01-28", 12, 20),
                    AttendanceClass("2", "Drilss Class", "7:30 PM", "2026-01-28", 8, 15)
                )
            ),
            selectedDate = "2026-01-28",
            onNavigateToHome = {},
            onNavigateToProgress = {},
            onNavigateToSettings = {},
            onNavigateToNewAttendance = { _ -> },
            onNavigateToAttendanceDetail = { _, _, _ -> },
            onDateSelected = { _ -> },
            onShowDatePicker = {}
        )
    }
}

@Preview
@Composable
fun AttendanceScreenPreview() {
    GMATheme {
        AttendanceScreenPreviewContent()
    }
}

@Preview
@Composable
fun AttendanceScreenPreviewDark() {
    GMATheme(darkTheme = true) {
        AttendanceScreenPreviewContent()
    }
}
