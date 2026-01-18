// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.config

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val subject: String,
    val tokenExpirationHours: Long
)
