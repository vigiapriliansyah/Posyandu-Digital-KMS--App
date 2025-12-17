package com.skripsi.posyandudigital.data.remote.dto

import com.google.gson.annotations.SerializedName


data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("role") val role: String,
    // PERBAIKAN: Ubah createdAt menjadi nullable (String?) agar tidak error saat Register
    @SerializedName("createdAt") val createdAt: String? = null,
    // Pastikan kode verifikasi ada
    @SerializedName("kode_verifikasi") val kodeVerifikasi: String? = null,

    @SerializedName("AdminProfile") val adminProfile: AdminProfileDto? = null,
    @SerializedName("KaderProfile") val kaderProfile: KaderProfileDto? = null
)

data class AdminProfileDto(
    @SerializedName("nama_admin") val namaAdmin: String,
    @SerializedName("desa_id") val desaId: Int?,
    @SerializedName("Desa") val desa: DesaProfileDto? = null
)

data class KaderProfileDto(
    @SerializedName("nama_kader") val namaKader: String,
    @SerializedName("Posyandu") val posyandu: PosyanduProfileDto? = null
)

// ==========================================
// 3. HELPER DTOs UNTUK LOKASI
// ==========================================

data class DesaProfileDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_desa") val namaDesa: String,
    @SerializedName("Kecamatan") val kecamatan: KecamatanProfileDto? = null
)

data class KecamatanProfileDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_kecamatan") val namaKecamatan: String
)

data class PosyanduProfileDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_posyandu") val namaPosyandu: String
)

// ==========================================
// 4. REQUEST DTOs
// ==========================================

data class CreateUserRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String,
    @SerializedName("nama_lengkap") val namaLengkap: String? = null,
    @SerializedName("desa_id") val desaId: Int? = null,
    @SerializedName("posyandu_id") val posyanduId: Int? = null
)

data class UpdateUserRequest(
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?
)

data class RegisterOrangTuaRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String = "orangtua",
    @SerializedName("nama_ibu") val namaIbu: String,
    @SerializedName("no_hp") val noHp: String,
    @SerializedName("alamat") val alamat: String,
    @SerializedName("posyandu_id") val posyanduId: Int
)

data class RegisterResponse(
    @SerializedName("message") val message: String,
    // PERBAIKAN: Pastikan menggunakan UserDto, bukan UserSimpleDto
    @SerializedName("user") val user: UserDto?
)

data class CreatePosyanduRequest(
    @SerializedName("nama_posyandu") val namaPosyandu: String,
    @SerializedName("desa_id") val desaId: Int
)

data class PendingOrangTuaDto(
    @SerializedName("id") val profileId: Int,
    @SerializedName("nama_ibu") val namaIbu: String,
    @SerializedName("no_hp") val noHp: String?,
    @SerializedName("alamat") val alamat: String?,
    @SerializedName("User") val user: UserSimpleDto
)

data class UserSimpleDto(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("createdAt") val createdAt: String
)

data class VerifyCodeRequest(
    @SerializedName("code") val code: String
)