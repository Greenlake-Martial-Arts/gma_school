// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.config

enum class Environment {
    LOCAL,
    STAGE,
    PRODUCTION
}

object ApiConfig {
    // Change this to switch environments
    var currentEnvironment = Environment.LOCAL
    
    fun getBaseUrl(localUrl: String): String {
        return when (currentEnvironment) {
            Environment.LOCAL -> localUrl
            Environment.STAGE -> "https://gma-stage-abd1bb3327e8.herokuapp.com"
            Environment.PRODUCTION -> "https://api.greenlakemartialarts.com"
        }
    }
}
