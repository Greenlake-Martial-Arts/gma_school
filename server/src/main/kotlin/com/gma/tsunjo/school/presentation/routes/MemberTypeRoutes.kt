// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.repositories.MemberTypeRepository
import com.gma.tsunjo.school.presentation.extensions.handleException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.memberTypeRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    val memberTypeRepository = get<MemberTypeRepository>()
    routing {
        logger.debug("<<<< memberTypeRoutes")
        authenticate("auth-jwt") {
            route("/member-types") {
                getMemberTypes(logger, memberTypeRepository)
                getMemberTypeById(logger, memberTypeRepository)
            }
        }
    }
}

fun Route.getMemberTypes(logger: Logger, memberTypeRepository: MemberTypeRepository) {
    get {
        try {
            logger.debug("GET /member-types")
            val memberTypes = memberTypeRepository.getAllMemberTypes()
            call.respond(memberTypes)
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}

fun Route.getMemberTypeById(logger: Logger, memberTypeRepository: MemberTypeRepository) {
    get("/{id}") {
        try {
            val id = call.parameters["id"]?.toLongOrNull()
            logger.debug("GET /member-types/$id")

            if (id == null) {
                throw AppException.BadRequest("Invalid ID")
                
            }

            val memberType = memberTypeRepository.getMemberTypeById(id)
            if (memberType != null) {
                call.respond(memberType)
            } else {
                throw AppException.MemberTypeNotFound(id)
            }
        } catch (e: Exception) {
            call.handleException(e, logger)
        }
    }
}
