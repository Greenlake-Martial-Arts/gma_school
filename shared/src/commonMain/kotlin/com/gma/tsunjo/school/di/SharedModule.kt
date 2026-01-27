// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.di

import com.gma.tsunjo.school.data.remote.AuthApi
import com.gma.tsunjo.school.data.remote.HttpClientFactory
import com.gma.tsunjo.school.data.repository.LoginRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val BASE_URL = "BASE_URL"

fun sharedModule(): Module = module {
    single { HttpClientFactory.create() }
    single { AuthApi(get(), get(named(BASE_URL))) }
    singleOf(::LoginRepository)
}
