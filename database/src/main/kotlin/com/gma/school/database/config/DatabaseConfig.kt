package com.gma.school.database.config

data class DatabaseConfig(
    val host: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
    val maxPoolSize: Int,
    val minIdleConnections: Int
) {
    val jdbcUrl: String
        get() = "jdbc:mysql://$host:$port/$database"
}
