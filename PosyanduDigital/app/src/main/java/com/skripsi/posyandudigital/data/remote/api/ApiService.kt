package com.skripsi.posyandudigital.data.remote.api

import androidx.compose.ui.semantics.Role
import com.skripsi.posyandudigital.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @GET("api/dashboard/kader")
    suspend fun getKaderDashboard(@Header("Authorization") token: String): Response<KaderDashboardDto>

    @GET("api/dashboard/admin")
    suspend fun getAdminDashboard(@Header("Authorization") token: String): Response<AdminDashboardDto>

    @GET("api/dashboard/superadmin")
    suspend fun getSuperAdminDashboard(@Header("Authorization") token: String): Response<SuperAdminDashboardDto>

    @GET("api/dashboard/orangtua")
    suspend fun getOrangTuaDashboard(@Header("Authorization") token: String): Response<OrangTuaDashboardDto>

    @GET("api/users")
    suspend fun getUsers(
        @Header("Authorization") token: String,
        @Query("role") role: String? = null
    ): Response<List<UserDto>>

    @POST("api/users")
    suspend fun createUser(
        @Header("Authorization") token: String,
        @Body createUserRequest: CreateUserRequest
    ): Response<UserDto>

    @PUT("api/users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body updateUserRequest: UpdateUserRequest
    ): Response<UserDto>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<Unit>
}