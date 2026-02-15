// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.attendance.data.repository

import com.gma.tsunjo.school.api.requests.CreateAttendanceRequest
import com.gma.tsunjo.school.api.responses.AttendanceWithStudents
import com.gma.tsunjo.school.auth.AuthenticationHandler
import com.gma.tsunjo.school.data.remote.HttpErrorMapper
import com.gma.tsunjo.school.domain.models.Attendance
import com.gma.tsunjo.school.features.attendance.data.remote.AttendanceApi
import kotlinx.datetime.LocalDate

class AttendanceRepository(
    private val attendanceApi: AttendanceApi,
    private val authHandler: AuthenticationHandler
) {
    suspend fun getAttendancesByDateRange(startDate: LocalDate, endDate: LocalDate): Result<List<Attendance>> {
        return try {
            val attendances = attendanceApi.getAttendancesByDateRange(startDate.toString(), endDate.toString())
            Result.success(attendances)
        } catch (e: Exception) {
            authHandler.handleError(e)
            Result.failure(HttpErrorMapper.mapException(e))
        }
    }

    suspend fun getAttendanceWithStudents(attendanceId: Long): Result<AttendanceWithStudents> {
        return try {
            val attendance = attendanceApi.getAttendanceWithStudents(attendanceId)
            Result.success(attendance)
        } catch (e: Exception) {
            authHandler.handleError(e)
            Result.failure(HttpErrorMapper.mapException(e))
        }
    }

    suspend fun createAttendance(classDate: LocalDate, notes: String?): Result<Attendance> {
        return try {
            val request = CreateAttendanceRequest(classDate, notes)
            val attendance = attendanceApi.createAttendance(request)
            Result.success(attendance)
        } catch (e: Exception) {
            authHandler.handleError(e)
            Result.failure(HttpErrorMapper.mapException(e))
        }
    }

    suspend fun addStudentsToAttendance(attendanceId: Long, studentIds: List<Long>): Result<Unit> {
        return try {
            attendanceApi.addStudentsToAttendance(attendanceId, studentIds)
            Result.success(Unit)
        } catch (e: Exception) {
            authHandler.handleError(e)
            Result.failure(HttpErrorMapper.mapException(e))
        }
    }
}
