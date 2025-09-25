package com.skripsi.posyandudigital.data.remote.api

import com.skripsi.posyandudigital.data.remote.dto.LoginRequest
import com.skripsi.posyandudigital.data.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
interface ApiService {
    @POST("api/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @GET("api/dashboard/kader")
    suspend fun getKaderDashboard(@Header("Authorization") token: String): Response<Any>

    @GET("api/dashboard/admin")
    suspend fun getAdminDashboard(@Header("Authorization") token: String): Response<Any>

    @GET("api/dashboard/superadmin")
    suspend fun getSuperAdminDashboard(@Header("Authorization") token: String): Response<Any>

    @GET("api/dashboard/orangtua")
    suspend fun getOrangTuaDashboard(@Header("Authorization") token: String): Response<Any>
}