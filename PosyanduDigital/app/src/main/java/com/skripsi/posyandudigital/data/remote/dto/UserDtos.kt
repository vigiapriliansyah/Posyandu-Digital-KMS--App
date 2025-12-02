package com.skripsi.posyandudigital.data.remote.dto

import com.google.gson.annotations.SerializedName

// --- USER & PROFILE DTOs ---

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("role") val role: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("AdminProfile") val adminProfile: AdminProfileDto? = null,
    @SerializedName("KaderProfile") val kaderProfile: KaderProfileDto? = null
)

data class AdminProfileDto(
    @SerializedName("nama_admin") val namaAdmin: String,
    // PERBAIKAN: Pastikan field ini ada untuk menangkap ID Desa
    @SerializedName("desa_id") val desaId: Int?,
    @SerializedName("Desa") val desa: DesaProfileDto? = null
)

data class KaderProfileDto(
    @SerializedName("nama_kader") val namaKader: String,
    @SerializedName("Posyandu") val posyandu: PosyanduProfileDto? = null
)

// --- HELPER DTOs UNTUK LOKASI ---

data class DesaProfileDto(
    // PERBAIKAN: Field 'id' wajib ada agar tidak error
    @SerializedName("id") val id: Int,
    @SerializedName("nama_desa") val namaDesa: String,
    @SerializedName("Kecamatan") val kecamatan: KecamatanProfileDto? = null
)

data class KecamatanProfileDto(
    // PERBAIKAN: Field 'id' wajib ada
    @SerializedName("id") val id: Int,
    @SerializedName("nama_kecamatan") val namaKecamatan: String
)

data class PosyanduProfileDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_posyandu") val namaPosyandu: String
)

// --- REQUEST DTOs ---

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
    @SerializedName("nama_ibu") val namaIbu: String,
    @SerializedName("no_hp") val noHp: String,
    @SerializedName("alamat") val alamat: String
)

data class RegisterResponse(
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: UserDto?
)

// DTO untuk Create Posyandu
data class CreatePosyanduRequest(
    @SerializedName("nama_posyandu") val namaPosyandu: String,
    @SerializedName("desa_id") val desaId: Int
)