package com.skripsi.posyandudigital.data.repository

import com.skripsi.posyandudigital.data.remote.api.ApiService
import com.skripsi.posyandudigital.data.remote.dto.*
import com.skripsi.posyandudigital.data.session.SessionManager
import com.skripsi.posyandudigital.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class AnakRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    private suspend fun getToken(): String? = sessionManager.getToken().firstOrNull()

    // Fungsi mengambil daftar anak
    fun getAnakList(): Flow<ResultWrapper<List<AnakDetailDto>>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = getToken() ?: return@flow emit(ResultWrapper.Error("Sesi habis"))
            val response = apiService.getAnakList("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                emit(ResultWrapper.Success(response.body()!!))
            } else {
                emit(ResultWrapper.Error("Gagal memuat data anak: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(ResultWrapper.Error("Error: ${e.message}"))
        }
    }

    // Fungsi mengambil data orang tua untuk dropdown
    fun getOrangTuaVerified(): Flow<ResultWrapper<List<OrangTuaSimpleDto>>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = getToken() ?: return@flow emit(ResultWrapper.Error("Sesi habis"))
            val response = apiService.getOrangTuaVerified("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                emit(ResultWrapper.Success(response.body()!!))
            } else {
                emit(ResultWrapper.Error("Gagal memuat data orang tua"))
            }
        } catch (e: Exception) {
            emit(ResultWrapper.Error("Error: ${e.message}"))
        }
    }

    // Fungsi mengirim data anak baru ke server
    fun createAnak(request: CreateAnakRequest): Flow<ResultWrapper<AnakDetailDto>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val token = getToken() ?: return@flow emit(ResultWrapper.Error("Sesi habis"))
            val response = apiService.createAnak("Bearer $token", request)
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
}