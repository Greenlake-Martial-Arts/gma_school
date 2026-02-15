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
import com.gma.tsunjo.school.features.attendance.ui.viewmodel.AttendanceViewModel
import com.gma.tsunjo.school.theme.GMATheme
import com.gma.tsunjo.school.ui.components.BottomNavigationBar
import com.gma.tsunjo.school.ui.components.SearchableTopBar
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToNewAttendance: () -> Unit,
    onNavigateToAttendanceDetail: (Long, String) -> Unit,
    viewModel: AttendanceViewModel = koinViewModel()
) {
    val listUiState by viewModel.listUiState.collectAsState()
    val selectedStartDate by viewModel.selectedStartDate.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(Unit) {
        viewModel.loadAttendancesForToday()
    }

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            // Convert milliseconds to LocalDate without using Instant
            val seconds = millis / 1000
            val days = seconds / 86400 // seconds per day
            val epochDays = days.toInt()
            val date = kotlinx.datetime.LocalDate.fromEpochDays(epochDays)
            viewModel.loadAttendancesForDateRange(date, date)
        }
    }

    AttendanceView(
        listUiState = listUiState,
        selectedDate = selectedStartDate.toString(),
        onNavigateToHome = onNavigateToHome,
        onNavigateToProgress = onNavigateToProgress,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToNewAttendance = onNavigateToNewAttendance,
        onNavigateToAttendanceDetail = onNavigateToAttendanceDetail,
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
    listUiState: com.gma.tsunjo.school.features.attendance.ui.viewmodel.AttendanceListUiState,
    selectedDate: String,
    onNavigateToHome: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToNewAttendance: () -> Unit,
    onNavigateToAttendanceDetail: (Long, String) -> Unit,
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
                onClick = onNavigateToNewAttendance,
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

            when (listUiState) {
                is com.gma.tsunjo.school.features.attendance.ui.viewmodel.AttendanceListUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is com.gma.tsunjo.school.features.attendance.ui.viewmodel.AttendanceListUiState.Success -> {
                    val attendances = (listUiState as com.gma.tsunjo.school.features.attendance.ui.viewmodel.AttendanceListUiState.Success).attendances

                    if (attendances.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No attendance records for this day",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = "ATTENDANCE RECORDS",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(attendances) { attendance ->
                                AttendanceItem(
                                    attendance = attendance,
                                    onClick = {
                                        onNavigateToAttendanceDetail(
                                            attendance.id,
                                            attendance.classDate.toString()
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                is com.gma.tsunjo.school.features.attendance.ui.viewmodel.AttendanceListUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = (listUiState as com.gma.tsunjo.school.features.attendance.ui.viewmodel.AttendanceListUiState.Error).message,
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
private fun AttendanceItem(
    attendance: com.gma.tsunjo.school.domain.models.Attendance,
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
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = attendance.classDate.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                attendance.notes?.let { notes ->
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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
            listUiState = com.gma.tsunjo.school.features.attendance.ui.viewmodel.AttendanceListUiState.Success(
                listOf(
                    com.gma.tsunjo.school.domain.models.Attendance(
                        id = 1L,
                        classDate = kotlinx.datetime.LocalDate(2026, 1, 28),
                        notes = "Regular class",
                        createdAt = kotlinx.datetime.LocalDateTime(2026, 1, 28, 12, 0)
                    ),
                    com.gma.tsunjo.school.domain.models.Attendance(
                        id = 2L,
                        classDate = kotlinx.datetime.LocalDate(2026, 1, 28),
                        notes = null,
                        createdAt = kotlinx.datetime.LocalDateTime(2026, 1, 28, 12, 0)
                    )
                )
            ),
            selectedDate = "2026-01-28",
            onNavigateToHome = {},
            onNavigateToProgress = {},
            onNavigateToSettings = {},
            onNavigateToNewAttendance = {},
            onNavigateToAttendanceDetail = { _, _ -> },
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
