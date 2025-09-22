package com.skripsi.posyandudigital.data.remote.dto

data class LoginResponse(
    val message: String,
    val user: UserDto,
    val token: String
)

data class UserDto(
    val id: Int,
    val username: String,
    val role: String
)
