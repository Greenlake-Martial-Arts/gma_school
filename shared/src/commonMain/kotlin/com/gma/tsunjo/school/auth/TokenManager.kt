// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.auth

interface TokenManager {
    suspend fun saveToken(token: String)
    fun getToken(): String?
    suspend fun clearToken()
    fun isAuthenticated(): Boolean
}
