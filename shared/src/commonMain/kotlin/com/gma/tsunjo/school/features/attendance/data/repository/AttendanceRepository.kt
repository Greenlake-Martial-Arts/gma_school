// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.attendance.data.repository

import com.gma.tsunjo.school.features.attendance.data.remote.AttendanceApi
import com.gma.tsunjo.school.features.attendance.domain.model.AttendanceClass
import com.gma.tsunjo.school.features.attendance.domain.model.AttendanceRecord
import com.gma.tsunjo.school.features.attendance.domain.model.CreateAttendanceRequest

class AttendanceRepository(
    private val attendanceApi: AttendanceApi
) {
    suspend fun getClassesForDate(date: String): Result<List<AttendanceClass>> {
        return try {
            val classes = attendanceApi.getClassesForDate(date)
            Result.success(classes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAttendanceRecord(classId: String, date: String): Result<AttendanceRecord> {
        return try {
            val record = attendanceApi.getAttendanceRecord(classId, date)
            Result.success(record)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAttendance(classId: String, studentIds: List<String>, date: String): Result<AttendanceRecord> {
        return try {
            val request = CreateAttendanceRequest(classId, studentIds, date)
            val record = attendanceApi.createAttendance(request)
            Result.success(record)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAttendance(recordId: String, studentIds: List<String>): Result<AttendanceRecord> {
        return try {
            val record = attendanceApi.updateAttendance(recordId, studentIds)
            Result.success(record)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
