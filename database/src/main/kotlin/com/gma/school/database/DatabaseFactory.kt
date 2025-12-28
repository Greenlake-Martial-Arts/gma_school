package com.gma.school.database

import com.gma.school.database.config.DatabaseConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {

    fun init(config: DatabaseConfig) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.jdbcUrl
            username = config.username
            password = config.password
            maximumPoolSize = config.maxPoolSize
            minimumIdle = config.minIdleConnections
            driverClassName = "com.mysql.cj.jdbc.Driver"
        }

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)
    }
}
