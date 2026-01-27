// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gma.tsunjo.school.resources.Strings
import com.gma.tsunjo.school.theme.GMATheme
import com.gma.tsunjo.school.ui.viewmodel.DashboardUiState
import com.gma.tsunjo.school.ui.viewmodel.DashboardViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    DashboardView(
        uiState = uiState,
        onClearError = viewModel::clearError
    )
}

@Composable
fun DashboardView(
    uiState: DashboardUiState,
    onClearError: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState) {
        if (uiState is DashboardUiState.Error) {
            snackbarHostState.showSnackbar(
                message = uiState.message,
                duration = SnackbarDuration.Short
            )
            onClearError()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
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
                    text = Strings.DASHBOARD_TITLE,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = Strings.DASHBOARD_WELCOME,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = Strings.DASHBOARD_QUICK_ACTIONS,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(Strings.DASHBOARD_VIEW_SCHEDULE)
                        }
                        
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(Strings.DASHBOARD_MY_PROFILE)
                        }
                        
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(Strings.DASHBOARD_ATTENDANCE)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DashboardPreview() {
    GMATheme {
        DashboardView(
            uiState = DashboardUiState.Idle,
            onClearError = {}
        )
    }
}

@Preview
@Composable
fun DashboardPreviewLight() {
    GMATheme(darkTheme = false) {
        DashboardView(
            uiState = DashboardUiState.Idle,
            onClearError = {}
        )
    }
}
