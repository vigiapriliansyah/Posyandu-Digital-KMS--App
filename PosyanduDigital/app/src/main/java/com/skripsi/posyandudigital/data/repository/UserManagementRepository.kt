package com.skripsi.posyandudigital.data.repository

import com.skripsi.posyandudigital.data.remote.dto.CreateUserRequest
import com.skripsi.posyandudigital.data.remote.dto.UpdateUserRequest
import com.skripsi.posyandudigital.data.remote.dto.UserDto
import com.skripsi.posyandudigital.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow


interface UserManagementRepository {
    fun getUsers(role: String?): Flow<ResultWrapper<List<UserDto>>>
    fun createUser(request: CreateUserRequest): Flow<ResultWrapper<UserDto>>

    fun updateUser(id: Int, user: UpdateUserRequest): Flow<ResultWrapper<UserDto>>

    fun deleteUser(id: Int): Flow<ResultWrapper<Unit>>
}
