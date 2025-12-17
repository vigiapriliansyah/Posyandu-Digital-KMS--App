package com.skripsi.posyandudigital.data.remote.dto
import com.google.gson.annotations.SerializedName
import com.skripsi.posyandudigital.data.remote.dto.UserDto
data class LoginResponse(
    val message: String,
    val user: UserDto,
    val token: String,
    @SerializedName("require_verification") val requireVerification: Boolean = false
)

