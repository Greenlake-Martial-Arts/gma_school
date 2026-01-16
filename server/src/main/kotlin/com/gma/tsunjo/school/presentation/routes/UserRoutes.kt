// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.api.requests.CreateUserRequest
import com.gma.tsunjo.school.api.requests.UpdateUserRequest
import com.gma.tsunjo.school.domain.repositories.UserRepository
import com.gma.tsunjo.school.presentation.extensions.respondWithError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
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
            authenticate("auth-jwt") {
                getUsers(logger)
                getActiveUsers(logger)
                getUserById(logger)
                updateUser(logger)
                deactivateUser(logger)
                activateUser(logger)
            }
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
            username = request.username,
            password = request.password,
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
            username = request.username,
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

        val deactivated = userRepository.setUserActiveStatus(id, false)
        if (deactivated) {
            call.respond(HttpStatusCode.OK, "User deactivated")
        } else {
            call.respond(HttpStatusCode.NotFound, "User not found")
        }
    }
}

fun Route.activateUser(logger: Logger) {
    val userRepository by inject<UserRepository>()

    patch("/{id}/activate") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("PATCH /users/$id/activate")

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
            return@patch
        }

        val activated = userRepository.setUserActiveStatus(id, true)
        if (activated) {
            call.respond(HttpStatusCode.OK, "User activated")
        } else {
            call.respond(HttpStatusCode.NotFound, "User not found")
        }
    }
}
