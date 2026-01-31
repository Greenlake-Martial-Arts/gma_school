// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.home.data.remote

import com.gma.tsunjo.school.data.remote.HttpErrorMapper
import com.gma.tsunjo.school.features.home.domain.model.HomeData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess

class HomeApi(
    private val client: HttpClient,
    private val endpoint: String
) {
    suspend fun getHomeData(): HomeData {
        val response = client.get("$endpoint/home")

        return when {
            response.status.isSuccess() -> response.body()
            else -> throw HttpErrorMapper.mapError(response.status)
        }
    }
}
