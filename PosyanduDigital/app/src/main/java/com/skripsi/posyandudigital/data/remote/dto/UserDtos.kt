package com.skripsi.posyandudigital.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) untuk merepresentasikan data User yang diterima dari server.
 * Password tidak disertakan untuk keamanan.
 */
data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("role") val role: String,
    @SerializedName("createdAt") val createdAt: String
)

/**
 * Request body untuk membuat user baru.
 */
data class CreateUserRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String
)

/**
 * Request body untuk memperbarui user.
 * Dibuat nullable agar bisa mengirim hanya username atau hanya password saja.
 */
data class UpdateUserRequest(
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?
)
