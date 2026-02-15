// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import co.touchlab.kermit.Logger
import com.gma.tsunjo.school.auth.AuthenticationHandler
import com.gma.tsunjo.school.auth.TokenManager
import com.gma.tsunjo.school.features.attendance.ui.AttendanceDetailScreen
import com.gma.tsunjo.school.features.attendance.ui.AttendanceScreen
import com.gma.tsunjo.school.features.attendance.ui.NewAttendanceScreen
import com.gma.tsunjo.school.features.home.ui.HomeScreen
import com.gma.tsunjo.school.features.progress.ui.StudentProgressRecordScreen
import com.gma.tsunjo.school.features.progress.ui.StudentProgressScreen
import com.gma.tsunjo.school.features.settings.ui.SettingsScreen
import com.gma.tsunjo.school.features.students.ui.viewmodel.StudentsViewModel
import com.gma.tsunjo.school.ui.screens.DashboardScreen
import com.gma.tsunjo.school.ui.screens.LoginScreen
import org.koin.compose.koinInject

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val log = Logger.withTag("AppNavigation")
    val studentsViewModel: StudentsViewModel = koinInject()
    val tokenManager: TokenManager = koinInject()
    val authHandler: AuthenticationHandler = koinInject()

    // Observe authentication failures
    LaunchedEffect(Unit) {
        authHandler.authenticationFailed.collect {
            log.w { "<< Authentication failed, navigating to Login" }
            navController.navigate(Screen.Login) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Check if user is already authenticated
    val startDestination = if (tokenManager.isAuthenticated()) {
        log.i { "<< User authenticated, starting at Home" }
        Screen.Home
    } else {
        log.i { "<< User not authenticated, starting at Login" }
        Screen.Login
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Screen.Login> {
            LoginScreen(
                onLoginSuccess = {
                    log.d { "<< Login successful, navigating to Home" }
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Dashboard> {
            DashboardScreen()
        }

        composable<Screen.Home> {
            HomeScreen(
                onNavigateToAttendance = {
                    navController.navigate(Screen.Attendance) {
                        popUpTo(Screen.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToProgress = {
                    navController.navigate(Screen.Progress) {
                        popUpTo(Screen.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings) {
                        popUpTo(Screen.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable<Screen.Attendance> {
            AttendanceScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Home) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToProgress = {
                    navController.navigate(Screen.Progress) {
                        popUpTo(Screen.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings) {
                        popUpTo(Screen.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToNewAttendance = {
                    navController.navigate(Screen.NewAttendance)
                },
                onNavigateToAttendanceDetail = { attendanceId, className ->
                    navController.navigate(Screen.AttendanceDetail(attendanceId, className))
                }
            )
        }

        composable<Screen.NewAttendance> {
            NewAttendanceScreen(
                classTime = "6:00 PM Class",
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.AttendanceDetail> { backStackEntry ->
            val detail = backStackEntry.toRoute<Screen.AttendanceDetail>()
            AttendanceDetailScreen(
                attendanceId = detail.attendanceId,
                className = detail.className,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.Progress> {
            StudentProgressScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Home) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToAttendance = {
                    navController.navigate(Screen.Attendance) {
                        popUpTo(Screen.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings) {
                        popUpTo(Screen.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToStudentDetail = { studentId, studentName, studentRank, studentRankColor ->
                    navController.navigate(Screen.StudentProgressRecord(studentId, studentName, studentRank, studentRankColor))
                }
            )
        }

        composable<Screen.StudentProgressRecord> { backStackEntry ->
            val detail = backStackEntry.toRoute<Screen.StudentProgressRecord>()
            StudentProgressRecordScreen(
                studentId = detail.studentId,
                studentName = detail.studentName,
                studentRank = detail.studentRank,
                studentRankColor = detail.studentRankColor,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.Settings> {
            SettingsScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Home) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToAttendance = {
                    navController.navigate(Screen.Attendance) {
                        popUpTo(Screen.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToProgress = {
                    navController.navigate(Screen.Progress) {
                        popUpTo(Screen.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onLogout = {
                    studentsViewModel.clearSelection()
                    log.i { "<< Navigating to Login after logout" }
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
