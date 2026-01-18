// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

fun Application.configureJwtAuthentication() {
    val logger = LoggerFactory.getLogger(javaClass)
    val jwtConfig by inject<JwtConfig>()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtConfig.realm

            verifier(
                JWT.require(Algorithm.HMAC256(jwtConfig.secret))
                    .withAudience(jwtConfig.audience)
                    .withIssuer(jwtConfig.issuer)
                    .build()
            )

            validate { credential ->
                logger.debug("<<<< JWT - validate")
                val username = credential.payload.getClaim("username").asString()
                val password = credential.payload.getClaim("password").asString()

                if (!username.isNullOrBlank() && !password.isNullOrBlank()) {
                    logger.debug("<<<< JWT - VALID")
                    JWTPrincipal(credential.payload)
                } else {
                    logger.debug("<<<< JWT - INVALID")
                    null
                }
            }

            challenge { _, _ ->
                logger.error("<<<< JWT - Token is not valid or has expired")
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("error" to "Token is not valid or has expired")
                )
            }
        }
    }
}
