package com.gma.tsunjo.school.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(commonModule)
}

// Common module that can be shared across platforms
val commonModule = org.koin.dsl.module {
    // Add shared dependencies here as needed
    // For now, keeping it empty as requested (minimal)
}
