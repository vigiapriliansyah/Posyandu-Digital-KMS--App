package com.skripsi.posyandudigital.data.remote.api

import com.skripsi.posyandudigital.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // ... (Endpoint Auth, Dashboard, User Management TETAP SAMA) ...

    // --- WILAYAH ---
    @GET("api/kecamatan")
    suspend fun getKecamatan(@Header("Authorization") token: String): Response<List<KecamatanDto>>

    @GET("api/desa")
    suspend fun getDesa(@Header("Authorization") token: String, @Query("kecamatan_id") kecamatanId: Int): Response<List<DesaDto>>

    @GET("api/posyandu")
    suspend fun getPosyandu(@Header("Authorization") token: String, @Query("desa_id") desaId: Int): Response<List<PosyanduDto>>

    @POST("api/posyandu")
    suspend fun createPosyandu(@Header("Authorization") token: String, @Body request: CreatePosyanduRequest): Response<PosyanduDto>

    // --- UPDATE PENTING: VERIFIKASI BY CODE ---

    // Kader tetap bisa lihat list pending (opsional)
    @GET("api/kader/verifikasi")
    suspend fun getPendingVerifikasi(@Header("Authorization") token: String): Response<List<PendingOrangTuaDto>>

    // Endpoint baru: Input Kode
    @POST("api/kader/verifikasi")
    suspend fun verifyByCode(
        @Header("Authorization") token: String,
        @Body request: VerifyCodeRequest
    ): Response<Unit>

    // ... (Endpoint Anak, Register, Public Wilayah TETAP SAMA) ...
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("api/dashboard/kader")
    suspend fun getKaderDashboard(@Header("Authorization") token: String): Response<KaderDashboardDto>

    @GET("api/dashboard/admin")
    suspend fun getAdminDashboard(@Header("Authorization") token: String): Response<AdminDashboardDto>

    @GET("api/dashboard/superadmin")
    suspend fun getSuperAdminDashboard(@Header("Authorization") token: String): Response<SuperAdminDashboardDto>

    @GET("api/dashboard/orangtua")
    suspend fun getOrangTuaDashboard(@Header("Authorization") token: String): Response<OrangTuaDashboardDto>

    @GET("api/auth/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<UserDto>

    @GET("api/users")
    suspend fun getUsers(@Header("Authorization") token: String, @Query("role") role: String?): Response<List<UserDto>>

    @POST("api/users")
    suspend fun createUser(@Header("Authorization") token: String, @Body createUserRequest: CreateUserRequest): Response<UserDto>

    @PUT("api/users/{id}")
    suspend fun updateUser(@Header("Authorization") token: String, @Path("id") id: Int, @Body updateUserRequest: UpdateUserRequest): Response<UserDto>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Header("Authorization") token: String, @Path("id") userId: Int): Response<Unit>

    @GET("api/anak")
    suspend fun getAnakList(@Header("Authorization") token: String): Response<List<AnakDetailDto>>

    @GET("api/anak/orangtua")
    suspend fun getOrangTuaVerified(@Header("Authorization") token: String): Response<List<OrangTuaSimpleDto>>

    @POST("api/anak")
    suspend fun createAnak(@Header("Authorization") token: String, @Body request: CreateAnakRequest): Response<AnakDetailDto>

    @POST("api/auth/register")
    suspend fun registerOrangTua(@Body request: RegisterOrangTuaRequest): Response<RegisterResponse>

    @GET("api/kecamatan")
    suspend fun getKecamatanPublic(): Response<List<KecamatanDto>>

    @GET("api/desa")
    suspend fun getDesaPublic(@Query("kecamatan_id") kecamatanId: Int): Response<List<DesaDto>>

    @GET("api/posyandu")
    suspend fun getPosyanduPublic(@Query("desa_id") desaId: Int): Response<List<PosyanduDto>>
}