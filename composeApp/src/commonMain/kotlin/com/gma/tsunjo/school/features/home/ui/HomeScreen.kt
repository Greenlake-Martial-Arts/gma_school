// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gma.tsunjo.school.features.home.ui.viewmodel.HomeUiState
import com.gma.tsunjo.school.features.home.ui.viewmodel.HomeViewModel
import com.gma.tsunjo.school.theme.GMATheme
import com.gma.tsunjo.school.ui.components.BottomNavigationBar
import gma_school.composeapp.generated.resources.Res
import gma_school.composeapp.generated.resources.gma_logo_temp
import gma_school.composeapp.generated.resources.temp_image
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToAttendance: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeView(
        uiState = uiState,
        onNavigateToAttendance = onNavigateToAttendance,
        onNavigateToProgress = onNavigateToProgress,
        onNavigateToSettings = onNavigateToSettings,
        onRosterClick = { viewModel.navigateToRoster() }
    )
}

@Composable
fun HomeView(
    uiState: HomeUiState,
    onNavigateToAttendance: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onRosterClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 0,
                onHomeClick = {},
                onAttendanceClick = onNavigateToAttendance,
                onProgressClick = onNavigateToProgress,
                onSettingsClick = onNavigateToSettings
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when (uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is HomeUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = "${uiState.greeting},\n ${uiState.userName}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    uiState.upcomingClass?.let { upcomingClass ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(MaterialTheme.shapes.large)
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.temp_image),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Text(
                                    text = "COMING UP NEXT",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )

                                Text(
                                    text = "Saturday at ${upcomingClass.time}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = upcomingClass.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${upcomingClass.registeredStudents} Students Registered",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Button(
                                        onClick = onRosterClick,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Roster")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeScreenPreviewContent() {
    HomeView(
        uiState = HomeUiState.Success(
            userName = "Instructor",
            greeting = "Evening",
            upcomingClass = HomeUiState.UpcomingClass(
                name = "Advanced Striking",
                time = "6:00 PM",
                registeredStudents = 12
            )
        ),
        onNavigateToAttendance = {},
        onNavigateToProgress = {},
        onNavigateToSettings = {},
        onRosterClick = {}
    )
}

@Preview
@Composable
fun HomeScreenPreview() {
    GMATheme {
        HomeScreenPreviewContent()
    }
}

@Preview
@Composable
fun HomeScreenPreviewDark() {
    GMATheme(darkTheme = true) {
        HomeScreenPreviewContent()
    }
}
