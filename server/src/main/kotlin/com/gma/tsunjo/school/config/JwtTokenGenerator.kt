// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.gma.tsunjo.school.api.requests.LoginRequest
import java.util.Date
import org.slf4j.LoggerFactory

class JwtTokenGenerator(private val jwtConfig: JwtConfig) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun generateAccessToken(credentials: LoginRequest): String {
        logger.debug("<<<< JWT - generateAccessToken()")
        return JWT.create()
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.issuer)
            .withSubject(jwtConfig.subject)
            .withClaim("username", credentials.username)
            .withClaim("password", credentials.password)
            .withExpiresAt(Date(System.currentTimeMillis() + getTokenExpirationMillis()))
            .sign(Algorithm.HMAC256(jwtConfig.secret))
    }

    private fun getTokenExpirationMillis(): Long {
        return jwtConfig.tokenExpirationHours * 60 * 60 * 1000
    }
}
