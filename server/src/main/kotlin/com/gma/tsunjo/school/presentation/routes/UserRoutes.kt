// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.api.requests.CreateUserRequest
import com.gma.tsunjo.school.api.requests.UpdateUserRequest
import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.repositories.UserRepository
import com.gma.tsunjo.school.presentation.extensions.handleException

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
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.userRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    val userRepository = get<UserRepository>()
    
    logger.debug("<<<< userRoutes")
    routing {
        route("/users") {
            authenticate("auth-jwt") {
                getUsers(logger, userRepository)
                getActiveUsers(logger, userRepository)
                getUserById(logger, userRepository)
                updateUser(logger, userRepository)
                deactivateUser(logger, userRepository)
                activateUser(logger, userRepository)
            }
        }
    }
}

fun Route.getUsers(logger: Logger, userRepository: UserRepository) {
    get {
        try {
            logger.debug("GET /users")
            val users = userRepository.getAllUsers()
            call.respond(users)
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getActiveUsers(logger: Logger, userRepository: UserRepository) {
    get("/active") {
        try {
            logger.debug("GET /users/active")
            val users = userRepository.getActiveUsers()
            call.respond(users)
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getUserById(logger: Logger, userRepository: UserRepository) {
    get("/{id}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("GET /users/$id")

            if (id == null) {
                throw AppException.BadRequest("Invalid user ID")
            }

            val user = userRepository.getUserById(id)
            if (user != null) {
                call.respond(user)
            } else {
                throw AppException.UserNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.createUser(logger: Logger) {
    val userRepository by inject<UserRepository>()

    post {
        try {
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
                    throw error
                }
            )
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.updateUser(logger: Logger, userRepository: UserRepository) {
    put("/{id}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("PUT /users/$id")

            if (id == null) {
                throw AppException.BadRequest("Invalid user ID")
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
                throw AppException.UserNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.deactivateUser(logger: Logger, userRepository: UserRepository) {
    patch("/{id}/deactivate") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("PATCH /users/$id/deactivate")

            if (id == null) {
                throw AppException.BadRequest("Invalid user ID")
            }

            val deactivated = userRepository.setUserActiveStatus(id, false)
            if (deactivated) {
                call.respond(HttpStatusCode.OK, "User deactivated")
            } else {
                throw AppException.UserNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.activateUser(logger: Logger, userRepository: UserRepository) {
    patch("/{id}/activate") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("PATCH /users/$id/activate")

            if (id == null) {
                throw AppException.BadRequest("Invalid user ID")
            }

            val activated = userRepository.setUserActiveStatus(id, true)
            if (activated) {
                call.respond(HttpStatusCode.OK, "User activated")
            } else {
                throw AppException.UserNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}
