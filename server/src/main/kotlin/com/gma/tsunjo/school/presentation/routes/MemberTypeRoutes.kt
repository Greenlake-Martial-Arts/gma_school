// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.domain.repositories.MemberTypeRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.memberTypeRoutes() {
    val logger = LoggerFactory.getLogger(javaClass)
    routing {
        logger.debug("<<<< memberTypeRoutes")
        route("/member-types") {
            getMemberTypes(logger)
            getMemberTypeById(logger)
        }
    }
}

fun Route.getMemberTypes(logger: Logger) {
    val memberTypeRepository by inject<MemberTypeRepository>()

    get {
        logger.debug("GET /member-types")
        val memberTypes = memberTypeRepository.getAllMemberTypes()
        call.respond(memberTypes)
    }
}

fun Route.getMemberTypeById(logger: Logger) {
    val memberTypeRepository by inject<MemberTypeRepository>()

    get("/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        logger.debug("GET /member-types/$id")

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return@get
        }

        val memberType = memberTypeRepository.getMemberTypeById(id)
        if (memberType != null) {
            call.respond(memberType)
        } else {
            call.respond(HttpStatusCode.NotFound, "Member type not found")
        }
    }
}
