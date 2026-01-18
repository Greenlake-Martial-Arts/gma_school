package com.gma.tsunjo.school.di

import com.gma.school.database.data.dao.AttendanceDao
import com.gma.school.database.data.dao.AuditLogDao
import com.gma.school.database.data.dao.LevelDao
import com.gma.school.database.data.dao.LevelRequirementDao
import com.gma.school.database.data.dao.MemberTypeDao
import com.gma.school.database.data.dao.MoveCategoryDao
import com.gma.school.database.data.dao.MoveDao
import com.gma.school.database.data.dao.RoleDao
import com.gma.school.database.data.dao.StudentDao
import com.gma.school.database.data.dao.StudentLevelDao
import com.gma.school.database.data.dao.StudentProgressDao
import com.gma.school.database.data.dao.UserDao
import com.gma.tsunjo.school.config.JwtConfig
import com.gma.tsunjo.school.config.JwtTokenGenerator
import com.gma.tsunjo.school.domain.repositories.AttendanceRepository
import com.gma.tsunjo.school.domain.repositories.LevelRepository
import com.gma.tsunjo.school.domain.repositories.MemberTypeRepository
import com.gma.tsunjo.school.domain.repositories.MoveCategoryRepository
import com.gma.tsunjo.school.domain.repositories.MoveRepository
import com.gma.tsunjo.school.domain.repositories.RoleRepository
import com.gma.tsunjo.school.domain.repositories.StudentProgressRepository
import com.gma.tsunjo.school.domain.repositories.StudentRepository
import com.gma.tsunjo.school.domain.repositories.UserRepository
import com.gma.tsunjo.school.domain.services.StudentService
import com.typesafe.config.ConfigFactory
import org.koin.dsl.module

val appModule = module {
    // Include common module
    includes(commonModule)

    // JWT Configuration
    single {
        val config = ConfigFactory.load()
        JwtConfig(
            secret = config.getString("jwt.secret"),
            issuer = config.getString("jwt.issuer"),
            audience = config.getString("jwt.audience"),
            realm = config.getString("jwt.realm"),
            subject = config.getString("jwt.subject"),
            tokenExpirationHours = config.getLong("jwt.tokenExpirationHours")
        )
    }
    single { JwtTokenGenerator(get()) }

    // DAOs
    single { UserDao() }
    single { RoleDao() }
    single { StudentDao() }
    single { StudentLevelDao() }
    single { StudentProgressDao() }
    single { LevelRequirementDao() }
    single { MemberTypeDao() }
    single { LevelDao() }
    single { MoveCategoryDao() }
    single { MoveDao() }
    single { AttendanceDao() }
    single { AuditLogDao() }

    // Repositories
    single { UserRepository(get(), get()) }
    single { RoleRepository(get()) }
    single { StudentRepository(get(), get(), get(), get()) }
    single { StudentProgressRepository(get(), get(), get(), get(), get(), get()) }
    single { MemberTypeRepository(get()) }
    single { LevelRepository(get()) }
    single { MoveCategoryRepository(get()) }
    single { MoveRepository(get()) }
    single { AttendanceRepository(get(), get()) }

    // Services
    single { StudentService(get(), get()) }
}
