package com.skripsi.posyandudigital.data.remote.api

import com.skripsi.posyandudigital.data.remote.dto.LoginRequest
import com.skripsi.posyandudigital.data.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>
}