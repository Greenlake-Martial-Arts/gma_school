// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.school.database.data.dao

import com.gma.school.database.data.tables.StudentsTable
import com.gma.tsunjo.school.domain.models.Student
import java.time.LocalDate
import java.time.LocalDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class StudentDao {

    fun findAll(): List<Student> = transaction {
        StudentsTable.selectAll().map { rowToStudent(it) }
    }

    fun findAllActive(): List<Student> = transaction {
        StudentsTable.select { StudentsTable.isActive eq true }.map { rowToStudent(it) }
    }

    fun findById(id: Long): Student? = transaction {
        StudentsTable.select { StudentsTable.id eq id }
            .map { rowToStudent(it) }
            .singleOrNull()
    }

    fun findByEmail(email: String): Student? = transaction {
        StudentsTable.select { StudentsTable.email eq email }
            .map { rowToStudent(it) }
            .singleOrNull()
    }

    fun findByExternalCode(externalCode: String): Student? = transaction {
        StudentsTable.select { StudentsTable.externalCode eq externalCode }
            .map { rowToStudent(it) }
            .singleOrNull()
    }

    fun insert(
        userId: Long,
        externalCode: String?,
        firstName: String,
        lastName: String,
        email: String,
        phone: String?,
        address: String?,
        memberTypeId: Long,
        signupDate: LocalDate?
    ): Student? = transaction {
        val insertStatement = StudentsTable.insert {
            it[StudentsTable.userId] = userId
            it[StudentsTable.externalCode] = externalCode
            it[StudentsTable.firstName] = firstName
            it[StudentsTable.lastName] = lastName
            it[StudentsTable.email] = email
            it[StudentsTable.phone] = phone
            it[StudentsTable.address] = address
            it[StudentsTable.memberTypeId] = memberTypeId
            it[StudentsTable.signupDate] = signupDate
            it[createdAt] = LocalDateTime.now()
            it[updatedAt] = LocalDateTime.now()
        }

        val id = insertStatement[StudentsTable.id].value
        findById(id)
    }

    fun update(
        id: Long,
        externalCode: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        phone: String? = null,
        address: String? = null,
        memberTypeId: Long? = null,
        signupDate: LocalDate? = null,
        isActive: Boolean? = null
    ): Student? = transaction {
        val updateCount = StudentsTable.update({ StudentsTable.id eq id }) {
            externalCode?.let { ec -> it[StudentsTable.externalCode] = ec }
            firstName?.let { fn -> it[StudentsTable.firstName] = fn }
            lastName?.let { ln -> it[StudentsTable.lastName] = ln }
            email?.let { e -> it[StudentsTable.email] = e }
            phone?.let { p -> it[StudentsTable.phone] = p }
            address?.let { a -> it[StudentsTable.address] = a }
            memberTypeId?.let { mt -> it[StudentsTable.memberTypeId] = mt }
            signupDate?.let { sd -> it[StudentsTable.signupDate] = sd }
            isActive?.let { active -> it[StudentsTable.isActive] = active }
            it[updatedAt] = LocalDateTime.now()
        }

        if (updateCount > 0) findById(id) else null
    }

    private fun rowToStudent(row: ResultRow): Student = Student(
        id = row[StudentsTable.id].value,
        userId = row[StudentsTable.userId].value,
        externalCode = row[StudentsTable.externalCode],
        firstName = row[StudentsTable.firstName],
        lastName = row[StudentsTable.lastName],
        email = row[StudentsTable.email],
        phone = row[StudentsTable.phone],
        address = row[StudentsTable.address],
        memberTypeId = row[StudentsTable.memberTypeId].value,
        signupDate = row[StudentsTable.signupDate]?.toString(),
        isActive = row[StudentsTable.isActive],
        createdAt = row[StudentsTable.createdAt].toString(),
        updatedAt = row[StudentsTable.updatedAt].toString()
    )
}
