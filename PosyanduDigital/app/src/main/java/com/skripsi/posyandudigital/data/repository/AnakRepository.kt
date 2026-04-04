package com.skripsi.posyandudigital.data.repository

import com.skripsi.posyandudigital.data.local.AnakDao
import com.skripsi.posyandudigital.data.local.AnakEntity
import com.skripsi.posyandudigital.data.remote.api.ApiService
import com.skripsi.posyandudigital.data.remote.dto.*
import com.skripsi.posyandudigital.data.session.SessionManager
import com.skripsi.posyandudigital.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class AnakRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    private val anakDao: AnakDao // Memasukkan DAO Anak untuk akses database lokal (Room)
) {
    private suspend fun getToken(): String? = sessionManager.getToken().firstOrNull()

    // --- FUNGSI MENGAMBIL DAFTAR ANAK (OFFLINE-FIRST ARCHITECTURE) ---
    fun getAnakList(): Flow<ResultWrapper<List<AnakDetailDto>>> = flow {
        emit(ResultWrapper.Loading)

        // 1. Tampilkan data dari Database Lokal (HP) terlebih dahulu agar instan dan bisa offline
        val localData = anakDao.getAllAnak()
        if (localData.isNotEmpty()) {
            val mappedLocal = localData.map {
                AnakDetailDto(
                    id = it.id,
                    namaAnak = it.namaAnak,
                    jenisKelamin = it.jenisKelamin,
                    tanggalLahir = it.tanggalLahir ?: "",
                    umurBulan = it.umurBulan,
                    orangTua = OrangTuaSimpleDto(id = 0, namaIbu = it.namaIbu), // Mapping dummy untuk orang tua
                    statusGiziTerakhir = it.statusGiziTerakhir // PENTING: Membaca status gizi dari database lokal
                )
            }
            emit(ResultWrapper.Success(mappedLocal))
        }

        try {
            // 2. Coba sinkronisasi mengambil data terbaru dari Server di belakang layar
            val token = getToken() ?: throw Exception("Sesi habis")
            val response = apiService.getAnakList("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiData = response.body()!!

                // 3. Hapus cache data lama di HP, lalu simpan data terbaru dari server ke HP
                anakDao.deleteAll()
                val entities = apiData.map {
                    AnakEntity(
                        id = it.id,
                        namaAnak = it.namaAnak,
                        jenisKelamin = it.jenisKelamin,
                        tanggalLahir = it.tanggalLahir,
                        umurBulan = it.umurBulan ?: 0,
                        namaIbu = it.orangTua?.namaIbu ?: "Belum terhubung",
                        statusGiziTerakhir = it.statusGiziTerakhir // PENTING: Menyimpan status gizi ke database lokal
                    )
                }
                anakDao.insertAll(entities)

                // 4. Update tampilan layar dengan data yang paling baru dari server
                emit(ResultWrapper.Success(apiData))
            } else if (localData.isEmpty()) {
                emit(ResultWrapper.Error("Gagal memuat daftar balita dari server"))
            }
        } catch (e: Exception) {
            if (localData.isEmpty()) {
                emit(ResultWrapper.Error("Mode Offline: Belum ada data tersimpan di HP ini."))
            }
        }
    }

    // --- FUNGSI MENGAMBIL DATA ORANG TUA (TETAP ONLINE) ---
    // Fungsi ini tidak di-offline-kan karena butuh data real-time untuk dropdown
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

    // --- FUNGSI MENGIRIM DATA ANAK BARU ---
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