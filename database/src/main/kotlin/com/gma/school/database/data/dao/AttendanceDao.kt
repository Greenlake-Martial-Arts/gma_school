// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.AttendanceEntriesTable
import com.gma.school.database.data.tables.AttendancesTable
import com.gma.tsunjo.school.domain.models.Attendance
import java.time.LocalDate
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class AttendanceDao {
    fun create(classDate: LocalDate, notes: String?): Attendance = transaction {
        val id = AttendancesTable.insertAndGetId {
            it[AttendancesTable.classDate] = classDate
            it[AttendancesTable.notes] = notes
        }.value

        Attendance(id, classDate.toString(), notes)
    }

    fun findById(id: Long): Attendance? = transaction {
        AttendancesTable.select { AttendancesTable.id eq id }
            .singleOrNull()
            ?.let(::toAttendance)
    }

    fun findAll(): List<Attendance> = transaction {
        AttendancesTable.selectAll().map(::toAttendance)
    }

    fun findByDateRange(startDate: LocalDate, endDate: LocalDate): List<Attendance> = transaction {
        AttendancesTable.select {
            (AttendancesTable.classDate greaterEq startDate) and (AttendancesTable.classDate lessEq endDate)
        }.map(::toAttendance)
    }

    fun update(id: Long, notes: String?): Boolean = transaction {
        AttendancesTable.update({ AttendancesTable.id eq id }) {
            it[AttendancesTable.notes] = notes
        } > 0
    }

    fun delete(id: Long): Boolean = transaction {
        AttendanceEntriesTable.deleteWhere { AttendanceEntriesTable.attendanceId eq id }
        AttendancesTable.deleteWhere { AttendancesTable.id eq id } > 0
    }

    // Attendance Entry operations
    fun addStudent(attendanceId: Long, studentId: Long): Boolean = transaction {
        try {
            AttendanceEntriesTable.insert {
                it[AttendanceEntriesTable.attendanceId] = attendanceId
                it[AttendanceEntriesTable.studentId] = studentId
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun removeStudent(attendanceId: Long, studentId: Long): Boolean = transaction {
        AttendanceEntriesTable.deleteWhere {
            (AttendanceEntriesTable.attendanceId eq attendanceId) and
                    (AttendanceEntriesTable.studentId eq studentId)
        } > 0
    }

    fun getStudents(attendanceId: Long): List<Long> = transaction {
        AttendanceEntriesTable.select { AttendanceEntriesTable.attendanceId eq attendanceId }
            .map { it[AttendanceEntriesTable.studentId].value }
    }

    fun getAttendancesByStudent(studentId: Long): List<Long> = transaction {
        AttendanceEntriesTable.select { AttendanceEntriesTable.studentId eq studentId }
            .map { it[AttendanceEntriesTable.attendanceId].value }
    }

    fun registerAttendance(classDate: LocalDate, studentIds: List<Long>, notes: String? = null): Attendance =
        transaction {
            val attendance = create(classDate, notes)
            studentIds.forEach { studentId ->
                addStudent(attendance.id, studentId)
            }
            attendance
        }

    private fun toAttendance(row: ResultRow) = Attendance(
        id = row[AttendancesTable.id].value,
        classDate = row[AttendancesTable.classDate].toString(),
        notes = row[AttendancesTable.notes]
    )
}
