package com.sonnt.moneymanagement.data.network

import com.sonnt.moneymanagement.data.dto.request.AuthRequest
import com.sonnt.moneymanagement.data.dto.response.AuthenticationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body body: AuthRequest): Response<AuthenticationResponse>
}