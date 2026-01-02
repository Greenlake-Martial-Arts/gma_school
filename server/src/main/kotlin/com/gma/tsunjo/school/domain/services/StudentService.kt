// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.services

import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.models.Student
import com.gma.tsunjo.school.domain.repositories.StudentRepository
import com.gma.tsunjo.school.domain.repositories.UserRepository
import java.util.UUID

class StudentService(
    private val studentRepository: StudentRepository,
    private val userRepository: UserRepository
) {

    fun createStudent(
        externalCode: String?,
        firstName: String,
        lastName: String,
        email: String?,
        phone: String?,
        memberTypeId: Long
    ): Result<Student> {
        return try {
            // Create user first (required for every student)
            val userResult = if (email != null) {
                val tempPassword = UUID.randomUUID().toString().substring(0, 8)
                val fullName = "$firstName $lastName"
                userRepository.createUser(email, tempPassword, fullName)
            } else {
                // Create user without email for students who don't need login
                val tempEmail = "student_${System.currentTimeMillis()}@temp.local"
                val tempPassword = UUID.randomUUID().toString().substring(0, 8)
                val fullName = "$firstName $lastName"
                userRepository.createUser(tempEmail, tempPassword, fullName)
            }

            if (userResult.isFailure) {
                return Result.failure(
                    userResult.exceptionOrNull() ?: AppException.DatabaseError("Failed to create user")
                )
            }

            val user = userResult.getOrThrow()

            // Create student with user reference
            studentRepository.createStudent(user.id, externalCode, firstName, lastName, email, phone, memberTypeId)
        } catch (e: Exception) {
            Result.failure(AppException.DatabaseError("Error creating student with user", e))
        }
    }

    fun updateStudent(
        id: Long,
        externalCode: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        phone: String? = null,
        memberTypeId: Long? = null,
        isActive: Boolean? = null
    ): Student? {
        val updatedStudent =
            studentRepository.updateStudent(id, externalCode, firstName, lastName, email, phone, memberTypeId, isActive)

        // Update associated user's fullName and email if changed
        if (updatedStudent != null && (firstName != null || lastName != null || email != null)) {
            val newFullName = if (firstName != null || lastName != null) {
                "${updatedStudent.firstName} ${updatedStudent.lastName}"
            } else null
            
            val newEmail = email
            
            userRepository.updateUser(updatedStudent.userId, email = newEmail, fullName = newFullName)
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
