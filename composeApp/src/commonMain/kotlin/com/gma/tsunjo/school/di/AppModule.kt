// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.di

import com.gma.tsunjo.school.features.attendance.ui.viewmodel.AttendanceViewModel
import com.gma.tsunjo.school.features.home.ui.viewmodel.HomeViewModel
import com.gma.tsunjo.school.features.students.ui.viewmodel.StudentsViewModel
import com.gma.tsunjo.school.ui.viewmodel.DashboardViewModel
import com.gma.tsunjo.school.ui.viewmodel.LoginViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
    factoryOf(::LoginViewModel)
    factoryOf(::DashboardViewModel)
    factoryOf(::HomeViewModel)
    factoryOf(::StudentsViewModel)
    factoryOf(::AttendanceViewModel)
}

expect fun platformModule(): Module
