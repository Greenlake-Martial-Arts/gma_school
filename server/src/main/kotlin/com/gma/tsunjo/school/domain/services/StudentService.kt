// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.services

import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.models.Student
import com.gma.tsunjo.school.domain.repositories.StudentRepository
import com.gma.tsunjo.school.domain.repositories.UserRepository
import java.util.UUID
import kotlinx.datetime.LocalDate

class StudentService(
    private val studentRepository: StudentRepository,
    private val userRepository: UserRepository
) {

    fun createStudent(
        externalCode: String?,
        firstName: String,
        lastName: String,
        email: String,
        phone: String?,
        address: String?,
        memberTypeId: Long,
        signupDate: LocalDate?,
        initialLevelCode: String? = null
    ): Result<Student> {
        return try {
            // Create user first (required for every student)
            val tempPassword = UUID.randomUUID().toString().substring(0, 8)
            val userResult = userRepository.createUser(email, tempPassword)

            if (userResult.isFailure) {
                return Result.failure(
                    userResult.exceptionOrNull() ?: AppException.DatabaseError("Failed to create user")
                )
            }

            val user = userResult.getOrThrow()

            // Create student with user reference
            studentRepository.createStudent(
                user.id,
                externalCode,
                firstName,
                lastName,
                email,
                phone,
                address,
                memberTypeId,
                signupDate,
                initialLevelCode
            )
        } catch (e: Exception) {
            Result.failure(AppException.DatabaseError("Error creating student with user", e))
        }
    }

    fun updateStudent(
        id: Long,
        externalCode: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        email: String,
        phone: String? = null,
        address: String? = null,
        memberTypeId: Long? = null,
        signupDate: LocalDate? = null,
        isActive: Boolean? = null
    ): Student? {
        val updatedStudent =
            studentRepository.updateStudent(
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

        // Update associated user's username if email provided (UI always sends current email)
        if (updatedStudent != null) {
            userRepository.updateUser(updatedStudent.userId, username = email)
        }

        return updatedStudent
    }

    fun deactivateStudent(id: Long): Boolean {
        return try {
            val userId = studentRepository.getStudentUserId(id)
            val studentDeactivated = studentRepository.setStudentActiveStatus(id, false)

            if (studentDeactivated && userId != null) {
                userRepository.setUserActiveStatus(userId, false)
            }

            studentDeactivated
        } catch (e: Exception) {
            false
        }
    }

    fun activateStudent(id: Long): Boolean {
        return try {
            val userId = studentRepository.getStudentUserId(id)
            val studentActivated = studentRepository.setStudentActiveStatus(id, true)

            if (studentActivated && userId != null) {
                userRepository.setUserActiveStatus(userId, true)
            }

            studentActivated
        } catch (e: Exception) {
            false
        }
    }
}
