// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.api.requests.CreateUserRequest
import com.gma.tsunjo.school.api.requests.LoginRequest
import com.gma.tsunjo.school.api.requests.UpdateUserRequest
import com.gma.tsunjo.school.domain.repositories.UserRepository
import com.gma.tsunjo.school.presentation.extensions.respondWithError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.userRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    routing {
        logger.debug("<<<< userRoutes")
        route("/users") {
            getUsers(logger)
            getActiveUsers(logger)
            getUserById(logger)
            createUser(logger)
            updateUser(logger)
            deactivateUser(logger)
        }

        route("/auth") {
            loginUser(logger)
        }
    }
}

fun Route.getUsers(logger: Logger) {
    val userRepository by inject<UserRepository>()

    get {
        logger.debug("GET /users")
        val users = userRepository.getAllUsers()
        call.respond(users)
    }
}

fun Route.getActiveUsers(logger: Logger) {
    val userRepository by inject<UserRepository>()

    get("/active") {
        logger.debug("GET /users/active")
        val users = userRepository.getActiveUsers()
        call.respond(users)
    }
}

fun Route.getUserById(logger: Logger) {
    val userRepository by inject<UserRepository>()

    get("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("GET /users/$id")

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
            return@get
        }

        val user = userRepository.getUserById(id)
        if (user != null) {
            call.respond(user)
        } else {
            call.respond(HttpStatusCode.NotFound, "User not found")
        }
    }
}

fun Route.createUser(logger: Logger) {
    val userRepository by inject<UserRepository>()

    post {
        logger.debug("POST /users")
        val request = call.receive<CreateUserRequest>()

        userRepository.createUser(
            email = request.email,
            password = request.password,
            fullName = request.fullName,
            roleId = request.roleId
        ).fold(
            onSuccess = { user ->
                call.respond(HttpStatusCode.Created, user)
            },
            onFailure = { error ->
                call.respondWithError(error)
            }
        )
    }
}

fun Route.updateUser(logger: Logger) {
    val userRepository by inject<UserRepository>()

    put("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("PUT /users/$id")

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
            return@put
        }

        val request = call.receive<UpdateUserRequest>()
        val user = userRepository.updateUser(
            id = id,
            email = request.email,
            fullName = request.fullName,
            isActive = request.isActive
        )

        if (user != null) {
            call.respond(user)
        } else {
            call.respond(HttpStatusCode.NotFound, "User not found")
        }
    }
}

fun Route.deactivateUser(logger: Logger) {
    val userRepository by inject<UserRepository>()

    patch("/{id}/deactivate") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("PATCH /users/$id/deactivate")

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
            return@patch
        }

        val deactivated = userRepository.deactivateUser(id)
        if (deactivated) {
            call.respond(HttpStatusCode.OK, "User deactivated")
        } else {
            call.respond(HttpStatusCode.NotFound, "User not found")
        }
    }
}

fun Route.loginUser(logger: Logger) {
    val userRepository by inject<UserRepository>()

    post("/login") {
        logger.debug("POST /auth/login")
        val request = call.receive<LoginRequest>()

        val user = userRepository.authenticateUser(request.email, request.password)
        if (user != null) {
            call.respond(HttpStatusCode.OK, user)
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials or inactive user")
        }
    }
}
