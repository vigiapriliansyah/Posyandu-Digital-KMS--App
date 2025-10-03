package com.skripsi.posyandudigital.data.remote.dto
import com.skripsi.posyandudigital.data.remote.dto.UserDto
data class LoginResponse(
    val message: String,
    val user: UserDto,
    val token: String
)

