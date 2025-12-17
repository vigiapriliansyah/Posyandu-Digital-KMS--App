package com.skripsi.posyandudigital.data.repository

import com.skripsi.posyandudigital.data.remote.api.ApiService
import com.skripsi.posyandudigital.data.remote.dto.*
import com.skripsi.posyandudigital.data.session.SessionManager
import com.skripsi.posyandudigital.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class UserManagementRepositoryImpl(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : UserManagementRepository {

    private suspend fun getToken(): String? = sessionManager.getToken().firstOrNull()

    override fun getUsers(role: String?): Flow<ResultWrapper<List<UserDto>>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = getToken() ?: return@flow emit(ResultWrapper.Error("Sesi berakhir"))
            val response = apiService.getUsers("Bearer $token", role)
            if (response.isSuccessful && response.body() != null) {
                emit(ResultWrapper.Success(response.body()!!))
            } else {
                emit(ResultWrapper.Error("Gagal mengambil data: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(ResultWrapper.Error("Error: ${e.message}"))
        }
    }

    override fun createUser(user: CreateUserRequest): Flow<ResultWrapper<UserDto>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = getToken() ?: return@flow emit(ResultWrapper.Error("Sesi berakhir"))
            val response = apiService.createUser("Bearer $token", user)
            if (response.isSuccessful && response.body() != null) {
                emit(ResultWrapper.Success(response.body()!!))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Gagal membuat user: ${response.code()}"
                emit(ResultWrapper.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(ResultWrapper.Error("Error: ${e.message}"))
        }
    }

    override fun deleteUser(id: Int): Flow<ResultWrapper<Unit>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = getToken() ?: return@flow emit(ResultWrapper.Error("Sesi berakhir"))
            val response = apiService.deleteUser("Bearer $token", id)
            if (response.isSuccessful) emit(ResultWrapper.Success(Unit))
            else emit(ResultWrapper.Error("Gagal hapus: ${response.code()}"))
        } catch (e: Exception) {
            emit(ResultWrapper.Error("Error: ${e.message}"))
        }
    }

    override fun updateUser(id: Int, user: UpdateUserRequest): Flow<ResultWrapper<UserDto>> = flow {
        emit(ResultWrapper.Error("Fitur edit belum diimplementasi"))
    }

    override fun getCurrentUser(): Flow<ResultWrapper<UserDto>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = getToken() ?: return@flow emit(ResultWrapper.Error("Sesi berakhir"))
            val response = apiService.getMe("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                emit(ResultWrapper.Success(response.body()!!))
            } else {
                emit(ResultWrapper.Error("Gagal memuat profil"))
            }
        } catch (e: Exception) { emit(ResultWrapper.Error("Error: ${e.message}")) }
    }

    override fun getKecamatan(): Flow<ResultWrapper<List<KecamatanDto>>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = getToken() ?: return@flow emit(ResultWrapper.Error("Sesi berakhir"))
            val response = apiService.getKecamatan("Bearer $token")
            if (response.isSuccessful && response.body() != null)
                emit(ResultWrapper.Success(response.body()!!))
            else emit(ResultWrapper.Error("Gagal memuat kecamatan"))
        } catch (e: Exception) {
            emit(ResultWrapper.Error(e.message ?: "Error"))
        }
    }

    override fun getDesa(kecamatanId: Int): Flow<ResultWrapper<List<DesaDto>>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = getToken() ?: return@flow emit(ResultWrapper.Error("Sesi berakhir"))
            val response = apiService.getDesa("Bearer $token", kecamatanId)
            if (response.isSuccessful && response.body() != null)
                emit(ResultWrapper.Success(response.body()!!))
            else emit(ResultWrapper.Error("Gagal memuat desa"))
        } catch (e: Exception) {
            emit(ResultWrapper.Error(e.message ?: "Error"))
        }
    }

    override fun getPosyandu(desaId: Int): Flow<ResultWrapper<List<PosyanduDto>>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = getToken() ?: return@flow emit(ResultWrapper.Error("Sesi berakhir"))
            val response = apiService.getPosyandu("Bearer $token", desaId)
            if (response.isSuccessful && response.body() != null)
                emit(ResultWrapper.Success(response.body()!!))
            else emit(ResultWrapper.Error("Gagal memuat posyandu"))
        } catch (e: Exception) {
            emit(ResultWrapper.Error(e.message ?: "Error"))
        }
    }

    override fun createPosyandu(nama: String, desaId: Int): Flow<ResultWrapper<PosyanduDto>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = getToken() ?: return@flow emit(ResultWrapper.Error("Sesi berakhir"))
            val request = CreatePosyanduRequest(nama, desaId)
            val response = apiService.createPosyandu("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                emit(ResultWrapper.Success(response.body()!!))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Gagal: ${response.code()}"
                emit(ResultWrapper.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(ResultWrapper.Error("Error: ${e.message}"))
        }
    }

    override fun getPendingVerifications(): Flow<ResultWrapper<List<PendingOrangTuaDto>>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = getToken() ?: return@flow emit(ResultWrapper.Error("Sesi berakhir"))
            val response = apiService.getPendingVerifikasi("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                emit(ResultWrapper.Success(response.body()!!))
            } else {
                emit(ResultWrapper.Error("Gagal memuat data: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(ResultWrapper.Error("Error koneksi: ${e.message}"))
        }
    }

    // UPDATE: Implementasi Verifikasi via Kode
    override fun verifyByCode(code: String): Flow<ResultWrapper<Unit>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = getToken() ?: return@flow emit(ResultWrapper.Error("Sesi berakhir"))
            // Pastikan Anda sudah update ApiService dengan endpoint 'verifyByCode'
            val response = apiService.verifyByCode("Bearer $token", VerifyCodeRequest(code))

            if (response.isSuccessful) {
                emit(ResultWrapper.Success(Unit))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Verifikasi gagal: ${response.code()}"
                emit(ResultWrapper.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(ResultWrapper.Error("Error koneksi: ${e.message}"))
        }
    }
}