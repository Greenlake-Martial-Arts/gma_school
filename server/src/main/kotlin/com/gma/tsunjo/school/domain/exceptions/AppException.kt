// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.exceptions

sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class UserAlreadyExists(email: String) : AppException("User with email $email already exists")
    class UserNotFound(id: Long) : AppException("User with id $id not found")
    class RoleNotFound(name: String) : AppException("Role '$name' not found")
    class ValidationError(message: String) : AppException(message)
    class DatabaseError(message: String, cause: Throwable? = null) : AppException(message, cause)
    class StudentAlreadyExists(message: String) : AppException(message)
    class MemberTypeNotFound(id: Long) : AppException("Member type with id '$id' not found")
}
