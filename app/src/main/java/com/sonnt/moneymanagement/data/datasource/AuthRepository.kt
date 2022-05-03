package com.sonnt.moneymanagement.data.datasource

import com.sonnt.moneymanagement.data.dto.request.AuthRequest
import com.sonnt.moneymanagement.data.network.ApiResult
import com.sonnt.moneymanagement.data.network.NetworkModule
import com.sonnt.moneymanagement.data.network.httpRequest
import kotlinx.coroutines.flow.Flow

object AuthRepository {
    private val authService = NetworkModule.authService

    suspend fun login(username: String, password: String): Flow<ApiResult<String>> {
        val request = AuthRequest(username, password)
        return httpRequest {
            authService.login(request)
        }
    }
}