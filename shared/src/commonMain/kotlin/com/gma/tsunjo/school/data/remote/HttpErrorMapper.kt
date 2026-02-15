// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.data.remote

import co.touchlab.kermit.Logger
import com.gma.tsunjo.school.domain.exceptions.AppException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.*

object HttpErrorMapper {
    private val log = Logger.withTag("HttpErrorMapper")
    
    fun mapError(statusCode: HttpStatusCode, message: String = ""): AppException {
        return when (statusCode) {
            HttpStatusCode.Unauthorized -> AppException.InvalidCredentials()
            HttpStatusCode.Forbidden -> AppException.Unauthorized()
            HttpStatusCode.BadRequest -> AppException.BadRequest(message.ifEmpty { "Invalid request format" })
            HttpStatusCode.NotFound -> AppException.ValidationError(message.ifEmpty { "Not found" })
            HttpStatusCode.Conflict -> AppException.ValidationError(message.ifEmpty { "Conflict" })
            in HttpStatusCode.InternalServerError..HttpStatusCode.GatewayTimeout -> 
                AppException.ServerError(message.ifEmpty { "Server error" })
            else -> AppException.Unknown("HTTP ${statusCode.value}: ${statusCode.description}")
        }
    }
    
    fun mapException(e: Exception): AppException {
        log.e { "<< Mapping exception: ${e::class.simpleName} - ${e.message}" }
        
        return when (e) {
            is HttpRequestTimeoutException -> AppException.Timeout()
            is ConnectTimeoutException -> AppException.ServerError("Server not responding")
            is SocketTimeoutException -> AppException.Timeout()
            else -> {
                val message = e.message?.lowercase() ?: ""
                val exceptionName = e::class.simpleName?.lowercase() ?: ""
                
                when {
                    // Server not available / not responding
                    message.contains("connection refused") ||
                    message.contains("failed to connect") ||
                    message.contains("could not connect") ||
                    exceptionName.contains("connectexception") -> 
                        AppException.ServerError("Server not available")
                    
                    // Network/IO errors
                    message.contains("network is unreachable") ||
                    message.contains("no route to host") ||
                    exceptionName.contains("ioexception") ||
                    exceptionName.contains("unknownhostexception") -> 
                        AppException.NetworkError()
                    
                    // Web/Browser specific
                    message.contains("fail to fetch") ||
                    message.contains("err_connection_refused") ||
                    message.contains("networkerror") -> 
                        AppException.ServerError("Server not available")
                    
                    else -> AppException.Unknown(e.message ?: "Unknown error", e)
                }
            }
        }
    }
}
