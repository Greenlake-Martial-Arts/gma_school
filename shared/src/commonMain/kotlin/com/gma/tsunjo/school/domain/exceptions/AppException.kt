// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.exceptions

sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    // Authentication & Authorization
    class InvalidCredentials : AppException("Invalid username or password")
    class Unauthorized : AppException("Unauthorized access")
    class SessionExpired : AppException("Session has expired")
    
    // Network
    class NetworkError(cause: Throwable? = null) : AppException("Network error occurred", cause)
    class Timeout : AppException("Request timed out")
    class ServerError(message: String = "Server error occurred") : AppException(message)
    
    // User
    class UserAlreadyExists(email: String) : AppException("User with email $email already exists")
    class UserNotFound(id: Long) : AppException("User with id $id not found")
    
    // Student
    class StudentAlreadyExists(message: String) : AppException(message)
    class StudentNotFound(id: Long) : AppException("Student with id $id not found")
    
    // Role
    class RoleNotFound(name: String) : AppException("Role '$name' not found")
    
    // Member Type
    class MemberTypeNotFound(id: Long) : AppException("Member type with id '$id' not found")
    class MemberTypeAlreadyExists(message: String) : AppException(message)
    
    // Level
    class LevelNotFound(id: Long) : AppException("Level with id $id not found")
    class LevelAlreadyExists(message: String) : AppException(message)
    class LevelRequirementNotFound(id: Long) : AppException("Level requirement with id $id not found")
    class LevelRequirementAlreadyExists(message: String) : AppException(message)
    class StudentLevelAlreadyExists(message: String) : AppException(message)
    
    // Move
    class MoveNotFound(id: Long) : AppException("Move with id $id not found")
    class MoveAlreadyExists(message: String) : AppException(message)
    class MoveCategoryNotFound(id: Long) : AppException("Move category with id $id not found")
    class MoveCategoryAlreadyExists(message: String) : AppException(message)
    
    // Attendance
    class AttendanceAlreadyExists(message: String) : AppException(message)
    
    // Validation
    class ValidationError(message: String) : AppException(message)
    
    // Database
    class DatabaseError(message: String, cause: Throwable? = null) : AppException(message, cause)
    
    // Unknown
    class Unknown(message: String = "An unknown error occurred", cause: Throwable? = null) : AppException(message, cause)
}
