// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.MemberTypeDao
import com.gma.school.database.data.dao.StudentDao
import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.models.Student
import kotlinx.datetime.LocalDate

class StudentRepository(
    private val studentDao: StudentDao,
    private val memberTypeDao: MemberTypeDao
) {

    fun getAllStudents(): List<Student> = studentDao.findAll()

    fun getActiveStudents(): List<Student> = studentDao.findAllActive()

    fun getStudentById(id: Long): Student? = studentDao.findById(id)

    fun getStudentByEmail(email: String): Student? = studentDao.findByEmail(email)

    fun createStudent(
        userId: Long,
        externalCode: String?,
        firstName: String,
        lastName: String,
        email: String,
        phone: String?,
        address: String?,
        memberTypeId: Long,
        signupDate: LocalDate?
    ): Result<Student> {
        return try {
            // Validate member type exists
            memberTypeDao.findById(memberTypeId)
                ?: return Result.failure(AppException.MemberTypeNotFound(memberTypeId))

            // Check for duplicate email
            email?.let { e ->
                if (studentDao.findByEmail(e) != null) {
                    return Result.failure(AppException.StudentAlreadyExists("Email already exists: $e"))
                }
            }

            // Check for duplicate external code
            externalCode?.let { ec ->
                if (studentDao.findByExternalCode(ec) != null) {
                    return Result.failure(AppException.StudentAlreadyExists("External code already exists: $ec"))
                }
            }

            val student = studentDao.insert(
                userId,
                externalCode,
                firstName,
                lastName,
                email,
                phone,
                address,
                memberTypeId,
                signupDate
            )
                ?: return Result.failure(AppException.DatabaseError("Failed to create student"))

            Result.success(student)
        } catch (e: AppException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(AppException.DatabaseError("Database error during student creation", e))
        }
    }

    fun updateStudent(
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
    ): Student? {
        // Validate member type if provided
        memberTypeId?.let { mt ->
            memberTypeDao.findById(mt) ?: return null
        }

        return studentDao.update(
            id,
            externalCode,
            firstName,
            lastName,
            email,
            phone,
            address,
            memberTypeId,
            signupDate,
            isActive
        )
    }

    fun setStudentActiveStatus(id: Long, isActive: Boolean): Boolean {
        val updated = studentDao.update(id, isActive = isActive)
        return updated != null
    }

    fun getStudentUserId(studentId: Long): Long? {
        return studentDao.findById(studentId)?.userId
    }
}
