// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.data.repository

import co.touchlab.kermit.Logger
import com.gma.tsunjo.school.api.requests.LoginRequest
import com.gma.tsunjo.school.api.responses.LoginResponse
import com.gma.tsunjo.school.data.remote.AuthApi
import com.gma.tsunjo.school.data.remote.HttpErrorMapper
import com.gma.tsunjo.school.domain.exceptions.AppException

class LoginRepository(
    private val authApi: AuthApi
) {
    private val log = Logger.withTag("LoginRepository")
    
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = authApi.login(LoginRequest(username, password))
            Result.success(response)
        } catch (e: AppException) {
            log.e(e) { "<< Login AppException: ${e::class.simpleName}" }
            Result.failure(e)
        } catch (e: Exception) {
            log.e(e) { "<< Login exception: ${e::class.simpleName}" }
            Result.failure(HttpErrorMapper.mapException(e))
        }
    }
}
