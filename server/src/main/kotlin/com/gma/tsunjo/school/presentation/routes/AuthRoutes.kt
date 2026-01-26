// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.api.requests.CreateUserRequest
import com.gma.tsunjo.school.api.requests.LoginRequest
import com.gma.tsunjo.school.api.responses.LoginResponse
import com.gma.tsunjo.school.api.responses.UserInfo
import com.gma.tsunjo.school.config.JwtTokenGenerator
import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.repositories.UserRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.get
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.authRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    val userRepository = get<UserRepository>()
    val jwtTokenGenerator = get<JwtTokenGenerator>()
    
    routing {
        logger.debug("<<<< authRoutes")
        route("/auth") {
            register(logger, userRepository)
            login(logger, userRepository, jwtTokenGenerator)
        }
    }
}

fun Route.register(logger: Logger, userRepository: UserRepository) {
    post("/register") {
        logger.debug("<<<< POST /auth/register")
        val request = call.receive<CreateUserRequest>()

        userRepository.createUser(
            username = request.username,
            password = request.password,
            roleId = request.roleId
        ).fold(
            onSuccess = { user ->
                call.respond(HttpStatusCode.Created, user)
            },
            onFailure = { error ->
                throw error
            }
        )
    }
}

fun Route.login(
    logger: Logger,
    userRepository: UserRepository,
    jwtTokenGenerator: JwtTokenGenerator
) {
    post("/login") {
        logger.debug("<<<< POST /auth/login")

        val credentials = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) {
            logger.error("<<<< Failed to parse login request: ${e.message}")
            throw AppException.BadRequest("Invalid request format")
        }

        logger.debug("<<<< username: ${credentials.username}")

        val result = userRepository.authenticateUser(credentials.username, credentials.password)

        result.fold(
            onSuccess = { user ->
                logger.debug("<<<< Authentication successful for user: ${user.username}")
                val token = jwtTokenGenerator.generateAccessToken(credentials)
                val response = LoginResponse(
                    token = token,
                    user = UserInfo(
                        id = user.id,
                        username = user.username,
                        isActive = user.isActive
                    )
                )
                call.respond(HttpStatusCode.OK, response)
            },
            onFailure = {
                logger.error("<<<< Authentication failed: Invalid credentials")
                throw AppException.InvalidCredentials()
            }
        )
    }
}
