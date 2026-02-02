// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
    val studentsViewModel: StudentsViewModel = koinInject()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Login
    ) {
        composable<Screen.Login> {
            LoginScreen(
                onLoginSuccess = {
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
                onNavigateToNewAttendance = { classTime ->
                    navController.navigate(Screen.NewAttendance)
                },
                onNavigateToAttendanceDetail = { classId, className, date ->
                    navController.navigate(Screen.AttendanceDetail(classId, className, date))
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
                classId = detail.classId,
                className = detail.className,
                date = detail.date,
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
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
