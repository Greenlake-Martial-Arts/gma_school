// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.di

import com.gma.tsunjo.school.config.ApiConfig
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual fun platformModule() = module {
    single(named(BASE_URL)) { ApiConfig.getBaseUrl("http://localhost:8080") }
}
