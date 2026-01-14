// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.exceptions

sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class UserAlreadyExists(email: String) : AppException("User with email $email already exists")
    class UserNotFound(id: Long) : AppException("User with id $id not found")
    class RoleNotFound(name: String) : AppException("Role '$name' not found")
    class ValidationError(message: String) : AppException(message)
    class DatabaseError(message: String, cause: Throwable? = null) : AppException(message, cause)
    class StudentAlreadyExists(message: String) : AppException(message)
    class StudentNotFound(id: Long) : AppException("Student with id $id not found")
    class MemberTypeNotFound(id: Long) : AppException("Member type with id '$id' not found")
    class MemberTypeAlreadyExists(message: String) : AppException(message)
    class LevelNotFound(id: Long) : AppException("Level with id $id not found")
    class LevelAlreadyExists(message: String) : AppException(message)
    class MoveNotFound(id: Long) : AppException("Move with id $id not found")
    class MoveAlreadyExists(message: String) : AppException(message)
    class MoveCategoryNotFound(id: Long) : AppException("Move category with id $id not found")
    class MoveCategoryAlreadyExists(message: String) : AppException(message)
    class AttendanceAlreadyExists(message: String) : AppException(message)
    class LevelRequirementNotFound(id: Long) : AppException("Level requirement with id $id not found")
    class LevelRequirementAlreadyExists(message: String) : AppException(message)
    class StudentLevelAlreadyExists(message: String) : AppException(message)
}
