package com.skripsi.posyandudigital.data.remote.api

import com.skripsi.posyandudigital.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // --- Auth & Registrasi ---
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun registerOrangTua(@Body request: RegisterOrangTuaRequest): Response<RegisterResponse>

    // --- Dashboard ---
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

    // --- User Management ---
    @GET("api/users")
    suspend fun getUsers(
        @Header("Authorization") token: String,
        @Query("role") role: String?
    ): Response<List<UserDto>>

    @POST("api/users")
    suspend fun createUser(
        @Header("Authorization") token: String,
        @Body createUserRequest: CreateUserRequest
    ): Response<UserDto>

    @PUT("api/users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body updateUserRequest: UpdateUserRequest
    ): Response<UserDto>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<Unit>

    // --- WILAYAH (Membutuhkan Token) ---
    @GET("api/kecamatan")
    suspend fun getKecamatan(@Header("Authorization") token: String): Response<List<KecamatanDto>>

    @GET("api/desa")
    suspend fun getDesa(
        @Header("Authorization") token: String,
        @Query("kecamatan_id") kecamatanId: Int
    ): Response<List<DesaDto>>

    @GET("api/posyandu")
    suspend fun getPosyandu(
        @Header("Authorization") token: String,
        @Query("desa_id") desaId: Int
    ): Response<List<PosyanduDto>>

    @POST("api/posyandu")
    suspend fun createPosyandu(
        @Header("Authorization") token: String,
        @Body request: CreatePosyanduRequest
    ): Response<PosyanduDto>

    // --- WILAYAH PUBLIC (Tanpa Token) ---
    @GET("api/kecamatan")
    suspend fun getKecamatanPublic(): Response<List<KecamatanDto>>

    @GET("api/desa")
    suspend fun getDesaPublic(@Query("kecamatan_id") kecamatanId: Int): Response<List<DesaDto>>

    @GET("api/posyandu")
    suspend fun getPosyanduPublic(@Query("desa_id") desaId: Int): Response<List<PosyanduDto>>

    // --- VERIFIKASI KADER ---
    @GET("api/kader/verifikasi")
    suspend fun getPendingVerifikasi(@Header("Authorization") token: String): Response<List<PendingOrangTuaDto>>

    @PUT("api/kader/verifikasi/{userId}")
    suspend fun verifyOrangTua(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<Unit>

    // PERBAIKAN: Menambahkan rejectOrangTua yang hilang sebelumnya
    @DELETE("api/kader/verifikasi/{userId}")
    suspend fun rejectOrangTua(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<Unit>

    // PERBAIKAN: Mengembalikan parameter verifyByCode yang terpotong
    @POST("api/kader/verifikasi")
    suspend fun verifyByCode(
        @Header("Authorization") token: String,
        @Body request: VerifyCodeRequest
    ): Response<Unit>

    // --- MANAJEMEN BALITA (ANAK) ---
    @GET("api/anak")
    suspend fun getAnakList(
        @Header("Authorization") token: String
    ): Response<List<AnakDetailDto>>

    @POST("api/anak")
    suspend fun createAnak(
        @Header("Authorization") token: String,
        @Body request: CreateAnakRequest
    ): Response<AnakDetailDto>

    @GET("api/anak/orangtua")
    suspend fun getOrangTuaVerified(
        @Header("Authorization") token: String
    ): Response<List<OrangTuaSimpleDto>>

    // --- PENGUKURAN KMS ---
    @POST("api/pengukuran")
    suspend fun createPengukuran(
        @Header("Authorization") token: String,
        @Body request: CreatePengukuranRequest
    ): Response<PengukuranResponse>

    @GET("api/pengukuran/anak/{anakId}")
    suspend fun getRiwayatPengukuran(
        @Header("Authorization") token: String,
        @Path("anakId") anakId: Int
    ): Response<List<PengukuranDetailDto>>
}