package com.gma.tsunjo.school.di

import com.gma.school.database.data.dao.AttendanceDao
import com.gma.school.database.data.dao.LevelDao
import com.gma.school.database.data.dao.MemberTypeDao
import com.gma.school.database.data.dao.MoveCategoryDao
import com.gma.school.database.data.dao.MoveDao
import com.gma.school.database.data.dao.RoleDao
import com.gma.school.database.data.dao.StudentDao
import com.gma.school.database.data.dao.UserDao
import com.gma.tsunjo.school.domain.repositories.AttendanceRepository
import com.gma.tsunjo.school.domain.repositories.LevelRepository
import com.gma.tsunjo.school.domain.repositories.MemberTypeRepository
import com.gma.tsunjo.school.domain.repositories.MoveCategoryRepository
import com.gma.tsunjo.school.domain.repositories.MoveRepository
import com.gma.tsunjo.school.domain.repositories.RoleRepository
import com.gma.tsunjo.school.domain.repositories.StudentRepository
import com.gma.tsunjo.school.domain.repositories.UserRepository
import com.gma.tsunjo.school.domain.services.StudentService
import org.koin.dsl.module

val appModule = module {
    // Include common module
    includes(commonModule)

    // DAOs
    single { UserDao() }
    single { RoleDao() }
    single { StudentDao() }
    single { MemberTypeDao() }
    single { LevelDao() }
    single { MoveCategoryDao() }
    single { MoveDao() }
    single { AttendanceDao() }

    // Repositories
    single { UserRepository(get(), get()) }
    single { RoleRepository(get()) }
    single { StudentRepository(get(), get()) }
    single { MemberTypeRepository(get()) }
    single { LevelRepository(get()) }
    single { MoveCategoryRepository(get()) }
    single { MoveRepository(get()) }
    single { AttendanceRepository(get()) }

    // Services
    single { StudentService(get(), get()) }
}
