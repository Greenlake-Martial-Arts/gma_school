// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.presentation.routes

import com.gma.tsunjo.school.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication

abstract class BaseIntegrationTest {

    /**
     * Test application module that configures plugins and routing
     * without database initialization to avoid MySQL connection in tests
     */
    fun Application.testModule() {
        // Only configure routing - testApplication handles basic plugins
        configureRouting()
    }

    // Helper function to run tests with the test application
    protected fun withTestApp(test: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            testModule()
        }
        test()
    }
}
