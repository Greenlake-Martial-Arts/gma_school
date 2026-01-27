// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.data.remote

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(): HttpClient {
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
        }
    }
}
