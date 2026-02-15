// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Login : Screen
    
    @Serializable
    data object Dashboard : Screen
    
    @Serializable
    data object Home : Screen
    
    @Serializable
    data object Attendance : Screen
    
    @Serializable
    data object Progress : Screen
    
    @Serializable
    data class StudentProgressRecord(
        val studentId: String,
        val studentName: String,
        val studentRank: String,
        val studentRankColor: String
    ) : Screen
    
    @Serializable
    data object Settings : Screen
    
    @Serializable
    data object NewAttendance : Screen
    
    @Serializable
    data class AttendanceDetail(
        val attendanceId: Long,
        val className: String
    ) : Screen
}
