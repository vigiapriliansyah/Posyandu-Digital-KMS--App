package com.skripsi.posyandudigital.data.repository

import com.skripsi.posyandudigital.data.remote.api.ApiService
import com.skripsi.posyandudigital.data.remote.dto.CreateUserRequest
import com.skripsi.posyandudigital.data.remote.dto.UpdateUserRequest
import com.skripsi.posyandudigital.data.remote.dto.UserDto
import com.skripsi.posyandudigital.data.session.SessionManager
import com.skripsi.posyandudigital.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class UserManagementRepositoryImpl(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : UserManagementRepository {

    // ... fungsi getUsers dan createUser tidak berubah ...
    override fun getUsers(role: String?): Flow<ResultWrapper<List<UserDto>>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = sessionManager.getToken().firstOrNull()
            if (token == null) {
                emit(ResultWrapper.Error("Sesi berakhir, silakan login kembali."))
                return@flow
            }
            val response = apiService.getUsers(token = "Bearer $token", role = role)
            if (response.isSuccessful && response.body() != null) {
                emit(ResultWrapper.Success(response.body()!!))
            } else {
                emit(ResultWrapper.Error("Gagal mengambil data: ${response.code()}"))
            }
        } catch (e: HttpException) {
            emit(ResultWrapper.Error("Terjadi kesalahan jaringan."))
        } catch (e: IOException) {
            emit(ResultWrapper.Error("Tidak dapat terhubung ke server."))
        }
    }

    override fun createUser(user: CreateUserRequest): Flow<ResultWrapper<UserDto>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = sessionManager.getToken().firstOrNull() ?: return@flow emit(ResultWrapper.Error("Token tidak ditemukan"))
            val response = apiService.createUser(token = "Bearer $token", createUserRequest = user)
            if (response.isSuccessful && response.body() != null) {
                emit(ResultWrapper.Success(response.body()!!))
            } else {
                emit(ResultWrapper.Error("Gagal membuat user: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(ResultWrapper.Error("Terjadi kesalahan: ${e.message}"))
        }
    }

    override fun updateUser(id: Int, user: UpdateUserRequest): Flow<ResultWrapper<UserDto>> = flow {
        emit(ResultWrapper.Loading)
        emit(ResultWrapper.Error("Fitur edit belum diimplementasi")) // Placeholder
    }

    // --- PERUBAHAN UTAMA: Implementasi fungsi deleteUser ---
    override fun deleteUser(id: Int): Flow<ResultWrapper<Unit>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = sessionManager.getToken().firstOrNull() ?: return@flow emit(ResultWrapper.Error("Token tidak ditemukan"))
            // ---- PERBAIKAN DI SINI: Nama parameter diubah dari 'id' menjadi 'userId' ----
            val response = apiService.deleteUser(token = "Bearer $token", userId = id)
            // --------------------------------------------------------------------------
            if (response.isSuccessful) {
                emit(ResultWrapper.Success(Unit)) // Kirim Success tanpa data
            } else {
                emit(ResultWrapper.Error("Gagal menghapus user: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(ResultWrapper.Error("Terjadi kesalahan: ${e.message}"))
        }
    }
}

