// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.home.data.repository

import com.gma.tsunjo.school.features.home.data.remote.HomeApi
import com.gma.tsunjo.school.features.home.domain.model.HomeData

class HomeRepository(
    private val homeApi: HomeApi
) {
    suspend fun getHomeData(): Result<HomeData> {
        return try {
            val response = homeApi.getHomeData()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
