// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.data.remote

import com.gma.tsunjo.school.api.requests.LoginRequest
import com.gma.tsunjo.school.api.responses.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class AuthApi(
    private val client: HttpClient,
    private val endpoint: String
) {
    suspend fun login(request: LoginRequest): LoginResponse {
        val response = client.post("$endpoint/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        return when {
            response.status.isSuccess() -> response.body()
            else -> throw HttpErrorMapper.mapError(response.status)
        }
    }
}
