// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.tables

import org.jetbrains.exposed.sql.Table

object AttendanceEntriesTable : Table("attendance_entries") {
    val attendanceId = reference("attendance_id", AttendancesTable)
    val studentId = reference("student_id", StudentsTable)

    override val primaryKey = PrimaryKey(attendanceId, studentId)
}
