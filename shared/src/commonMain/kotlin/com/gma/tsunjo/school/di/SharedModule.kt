// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.di

import com.gma.tsunjo.school.auth.AuthenticationHandler
import com.gma.tsunjo.school.auth.TokenManager
import com.gma.tsunjo.school.auth.TokenManagerImpl
import com.gma.tsunjo.school.auth.createSettings
import com.gma.tsunjo.school.data.remote.AuthApi
import com.gma.tsunjo.school.data.remote.HttpClientFactory
import com.gma.tsunjo.school.data.repository.LoginRepository
import com.gma.tsunjo.school.features.attendance.data.remote.AttendanceApi
import com.gma.tsunjo.school.features.attendance.data.repository.AttendanceRepository
import com.gma.tsunjo.school.features.home.data.remote.HomeApi
import com.gma.tsunjo.school.features.home.data.repository.HomeRepository
import com.gma.tsunjo.school.features.students.data.remote.StudentsApi
import com.gma.tsunjo.school.features.students.data.repository.StudentsRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val BASE_URL = "BASE_URL"

fun sharedModule(): Module = module {
    // Settings and Token Manager
    single { createSettings() }
    single<TokenManager> { TokenManagerImpl(get()) }
    single { AuthenticationHandler(get()) }

    // HTTP Client with auth
    single { HttpClientFactory.create(get()) }

    // APIs
    single { AuthApi(get(), get(named(BASE_URL))) }
    single { HomeApi(get(), get(named(BASE_URL))) }
    single { StudentsApi(get(), get(named(BASE_URL))) }
    single { AttendanceApi(get(), get(named(BASE_URL))) }

    // Repositories
    singleOf(::LoginRepository)
    singleOf(::HomeRepository)
    singleOf(::StudentsRepository)
    singleOf(::AttendanceRepository)
}
