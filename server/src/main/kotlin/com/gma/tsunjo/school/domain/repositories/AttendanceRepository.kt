// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.AttendanceDao
import com.gma.school.database.data.dao.StudentDao
import com.gma.tsunjo.school.api.responses.AttendanceWithStudents
import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.models.Attendance
import kotlinx.datetime.LocalDate

class AttendanceRepository(
    private val attendanceDao: AttendanceDao,
    private val studentDao: StudentDao
) {

    fun getAllAttendances(): List<Attendance> = attendanceDao.findAll()

    fun getAttendanceById(id: Long): Attendance? = attendanceDao.findById(id)

    fun getAttendancesByDateRange(startDate: LocalDate, endDate: LocalDate): List<Attendance> =
        attendanceDao.findByDateRange(startDate, endDate)

    fun createAttendance(classDate: LocalDate, notes: String?): Result<Attendance> {
        return try {
            val attendance = attendanceDao.create(classDate, notes)
            Result.success(attendance)
        } catch (e: Exception) {
            Result.failure(AppException.DatabaseError("Database error during attendance creation", e))
        }
    }

    fun updateAttendance(id: Long, notes: String?): Boolean {
        return attendanceDao.update(id, notes)
    }

    fun deleteAttendance(id: Long): Boolean {
        return attendanceDao.delete(id)
    }

    fun addStudentToAttendance(attendanceId: Long, studentId: Long): Boolean {
        return attendanceDao.addStudent(attendanceId, studentId)
    }

    fun addStudentsToAttendance(attendanceId: Long, studentIds: List<Long>): Int {
        var count = 0
        studentIds.forEach { studentId ->
            if (attendanceDao.addStudent(attendanceId, studentId)) {
                count++
            }
        }
        return count
    }

    fun removeStudentFromAttendance(attendanceId: Long, studentId: Long): Boolean {
        return attendanceDao.removeStudent(attendanceId, studentId)
    }

    fun getStudentsInAttendance(attendanceId: Long): List<Long> {
        return attendanceDao.getStudents(attendanceId)
    }

    fun getAttendanceWithStudents(attendanceId: Long): AttendanceWithStudents? {
        val attendance = attendanceDao.findById(attendanceId) ?: return null
        val studentIds = attendanceDao.getStudents(attendanceId)
        val students = studentIds.mapNotNull { studentDao.findById(it) }
        return AttendanceWithStudents(attendance, students)
    }
}
