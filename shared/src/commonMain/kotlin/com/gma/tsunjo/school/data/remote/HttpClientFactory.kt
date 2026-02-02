// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.data.remote

import com.gma.tsunjo.school.auth.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger as KermitLogger

object HttpClientFactory {

    private val log = KermitLogger.withTag("HttpClient")

    fun create(tokenManager: TokenManager): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 30_000
            }

            // Add auth header to all requests
            defaultRequest {
                val token = tokenManager.getToken()
                if (token != null) {
                    header("Authorization", "Bearer $token")
                    log.d { "<< Auth header added to request: ${this.url}" }
                } else {
                    log.d { "<< No token available for request: ${this.url}" }
                }
            }
        }
    }
}
