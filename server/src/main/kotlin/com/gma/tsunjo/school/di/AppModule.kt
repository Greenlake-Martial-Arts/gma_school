package com.gma.tsunjo.school.di

import com.gma.school.database.data.dao.RoleDao
import com.gma.school.database.data.dao.UserDao
import com.gma.tsunjo.school.domain.repositories.RoleRepository
import com.gma.tsunjo.school.domain.repositories.UserRepository
import org.koin.dsl.module

val appModule = module {
    // Include common module
    includes(commonModule)

    // Server-specific dependencies
    single { UserDao() }
    single { RoleDao() }
    single { UserRepository(get(), get()) }
    single { RoleRepository(get()) }
}
