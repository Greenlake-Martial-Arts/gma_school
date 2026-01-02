// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school

import com.gma.school.database.DatabaseFactory
import com.gma.school.database.config.DatabaseConfig
import com.gma.tsunjo.school.di.appModule
import com.gma.tsunjo.school.presentation.routes.studentRoutes
import com.gma.tsunjo.school.presentation.routes.userRoutes
import com.typesafe.config.ConfigFactory
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.File
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val logger = LoggerFactory.getLogger(javaClass)

    configurePlugins()
    setupConfig(logger)
    configureRouting()
}

fun Application.configurePlugins() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    install(ContentNegotiation) {
        json()
    }

    install(Compression) {
        gzip()
    }
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("GMA School API: ${Greeting().greet()}")
        }
    }

    userRoutes()
    studentRoutes()
}

fun Application.setupConfig(logger: Logger) {
    var customConfig: ApplicationConfig = environment.config

    val configFile = File("application.conf")
    if (configFile.exists()) {
        logger.debug("Loading application.conf")
        customConfig = HoconApplicationConfig(ConfigFactory.parseFile(configFile))
    } else {
        logger.debug("<< Using default config")
    }

    // Initialize database from config - no defaults
    val dbConfig = DatabaseConfig(
        host = customConfig.property("database.host").getString(),
        port = customConfig.property("database.port").getString().toInt(),
        database = customConfig.property("database.name").getString(),
        username = customConfig.property("database.username").getString(),
        password = customConfig.property("database.password").getString(),
        maxPoolSize = customConfig.property("database.maxPoolSize").getString().toInt(),
        minIdleConnections = customConfig.property("database.minIdleConnections").getString().toInt()
    )
    DatabaseFactory.init(dbConfig)
}
