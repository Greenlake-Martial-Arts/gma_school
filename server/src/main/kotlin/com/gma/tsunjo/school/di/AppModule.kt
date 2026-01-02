package com.gma.tsunjo.school.di

import com.gma.school.database.data.dao.MemberTypeDao
import com.gma.school.database.data.dao.RoleDao
import com.gma.school.database.data.dao.StudentDao
import com.gma.school.database.data.dao.UserDao
import com.gma.tsunjo.school.domain.repositories.RoleRepository
import com.gma.tsunjo.school.domain.repositories.StudentRepository
import com.gma.tsunjo.school.domain.repositories.UserRepository
import com.gma.tsunjo.school.domain.services.StudentService
import org.koin.dsl.module

val appModule = module {
    // Include common module
    includes(commonModule)

    // Server-specific dependencies
    single { UserDao() }
    single { RoleDao() }
    single { StudentDao() }
    single { MemberTypeDao() }
    single { UserRepository(get(), get()) }
    single { RoleRepository(get()) }
    single { StudentRepository(get(), get()) }
    single { StudentService(get(), get()) }
}
