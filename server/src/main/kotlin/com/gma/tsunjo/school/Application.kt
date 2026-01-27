// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school

import com.gma.school.database.DatabaseFactory
import com.gma.school.database.config.DatabaseConfig
import com.gma.tsunjo.school.config.configureJwtAuthentication
import com.gma.tsunjo.school.di.appModule
import com.gma.tsunjo.school.presentation.routes.attendanceRoutes
import com.gma.tsunjo.school.presentation.routes.authRoutes
import com.gma.tsunjo.school.presentation.routes.levelRoutes
import com.gma.tsunjo.school.presentation.routes.memberTypeRoutes
import com.gma.tsunjo.school.presentation.routes.moveCategoryRoutes
import com.gma.tsunjo.school.presentation.routes.moveRoutes
import com.gma.tsunjo.school.presentation.routes.studentProgressRoutes
import com.gma.tsunjo.school.presentation.routes.studentRoutes
import com.gma.tsunjo.school.presentation.routes.userRoutes
import com.typesafe.config.ConfigFactory
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.File
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val logger = LoggerFactory.getLogger(javaClass)

    configurePlugins()
    setupConfig(logger)
    configureJwtAuthentication()
    configureRouting()
}

fun Application.configurePlugins() {
    install(CORS) {
        // FIXME: Remove this or upload to the same server
        allowHost("localhost:8081")
        allowHost("127.0.0.1:8081")
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowCredentials = true
    }

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

    install(CallId) {
        header(HttpHeaders.XRequestId)
        replyToHeader(HttpHeaders.XRequestId)
        verify { callId: String ->
            callId.isNotEmpty()
        }
    }

    install(CallLogging) {
        level = Level.INFO
        callIdMdc("call-id")

        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val uri = call.request.uri
            val userAgent = call.request.headers["User-Agent"]
            val contentType = call.request.headers["Content-Type"]
            val authorization = call.request.headers["Authorization"]?.let {
                if (it.startsWith("Bearer")) "Bearer ***" else "***"
            }
            val accept = call.request.headers["Accept"]
            val xForwardedFor = call.request.headers["X-Forwarded-For"]
            val host = call.request.headers["Host"]
            val origin = call.request.headers["Origin"]
            val referer = call.request.headers["Referer"]

            buildString {
                append("$status: $httpMethod $uri")
                userAgent?.let { append(" | User-Agent: $it") }
                contentType?.let { append(" | Content-Type: $it") }
                authorization?.let { append(" | Authorization: $it") }
                accept?.let { append(" | Accept: $it") }
                host?.let { append(" | Host: $it") }
                xForwardedFor?.let { append(" | X-Forwarded-For: $it") }
                origin?.let { append(" | Origin: $it") }
                referer?.let { append(" | Referer: $it") }

                // Add any custom headers that start with X-
                call.request.headers.entries().forEach { (name, values) ->
                    if (name.startsWith("X-") && name != "X-Forwarded-For") {
                        append(" | $name: ${values.joinToString(", ")}")
                    }
                }
            }
        }

        filter { call ->
            !call.request.uri.startsWith("/static")
        }
    }
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("GMA School API: ${Greeting().greet()}")
        }
    }

    authRoutes()
    userRoutes()
    studentRoutes()
    studentProgressRoutes()
    memberTypeRoutes()
    levelRoutes()
    moveCategoryRoutes()
    moveRoutes()
    attendanceRoutes()
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
