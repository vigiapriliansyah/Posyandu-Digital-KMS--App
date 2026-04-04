package com.skripsi.posyandudigital.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.skripsi.posyandudigital.data.local.KmsDao
import com.skripsi.posyandudigital.data.local.KmsEntity
import com.skripsi.posyandudigital.data.remote.api.ApiService
import com.skripsi.posyandudigital.data.remote.dto.*
import com.skripsi.posyandudigital.data.session.SessionManager
import com.skripsi.posyandudigital.utils.ResultWrapper
import com.skripsi.posyandudigital.worker.SyncKmsWorker
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class PengukuranRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    private val dao: KmsDao,
    private val context: Context
) {
    private suspend fun getToken(): String? = sessionManager.getToken().firstOrNull()

    private fun mapEntityToDto(entities: List<KmsEntity>): List<PengukuranDetailDto> {
        return entities.map {
            PengukuranDetailDto(
                id = it.apiId ?: -it.localId,
                tanggalPencatatan = it.tanggalPencatatan,
                umurBulan = it.umurBulan,
                beratBadan = it.beratBadan,
                tinggiBadan = it.tinggiBadan,
                kbm = it.kbm,
                statusNaikTurun = it.statusNaikTurun,
                asiEksklusif = it.asiEksklusif,
                statusGiziRaw = it.statusGiziRaw,
                catatan = it.catatan
            )
        }
    }

    fun getRiwayat(anakId: Int): Flow<ResultWrapper<List<PengukuranDetailDto>>> = flow {
        emit(ResultWrapper.Loading)

        try {
            // 1. Tampilkan data dari database lokal dulu (OFFLINE FIRST - Super Cepat)
            val localData = dao.getRiwayatByAnak(anakId)
            if (localData.isNotEmpty()) {
                emit(ResultWrapper.Success(mapEntityToDto(localData)))
            }

            // 2. Sinkronisasi background dengan server
            val token = getToken() ?: throw Exception("Sesi habis")
            val response = apiService.getRiwayatPengukuran("Bearer $token", anakId)

            if (response.isSuccessful && response.body() != null) {
                val apiData = response.body()!!

                // Hapus data tersinkron lama, simpan data terbaru
                dao.deleteSyncedRecordsByAnak(anakId)
                val entitiesToInsert = apiData.map { dto ->
                    KmsEntity(
                        apiId = dto.id, anakId = anakId,
                        tanggalPencatatan = dto.tanggalPencatatan ?: "",
                        umurBulan = dto.umurBulan, beratBadan = dto.beratBadan, tinggiBadan = dto.tinggiBadan,
                        kbm = dto.kbm, statusNaikTurun = dto.statusNaikTurun, asiEksklusif = dto.asiEksklusif,
                        statusGiziRaw = dto.statusGiziRaw, catatan = dto.catatan, isSynced = true
                    )
                }
                dao.insertAll(entitiesToInsert)

                // Refresh UI dengan data gabungan (Data API + Data Offline yang belum terkirim)
                val updatedLocalData = dao.getRiwayatByAnak(anakId)
                emit(ResultWrapper.Success(mapEntityToDto(updatedLocalData)))

            } else if (localData.isEmpty()) {
                emit(ResultWrapper.Error("Gagal mengambil data dari server: ${response.code()}"))
            }
        } catch (e: Exception) {
            // Abaikan jika coroutine dibatalkan (misal user pindah halaman)
            if (e is CancellationException) throw e

            // Tangkap error jaringan (Offline)
            val checkLocal = dao.getRiwayatByAnak(anakId)
            if (checkLocal.isEmpty()) {
                emit(ResultWrapper.Error("Anda sedang Offline. Belum ada riwayat KMS tersimpan di perangkat untuk balita ini."))
            }
        }
    }

    fun createPengukuran(request: CreatePengukuranRequest): Flow<ResultWrapper<PengukuranResponse>> = flow {
        emit(ResultWrapper.Loading)
        try {
            // 1. Simpan ke database lokal HP
            val entity = KmsEntity(
                anakId = request.anakId, tanggalPencatatan = request.tanggalPencatatan,
                umurBulan = request.umurBulan ?: 0, beratBadan = request.beratBadan,
                tinggiBadan = request.tinggiBadan, kbm = request.kbm,
                statusNaikTurun = request.statusNaikTurun, asiEksklusif = request.asiEksklusif,
                statusGiziRaw = null, catatan = request.catatan, isSynced = false
            )
            dao.insert(entity)

            // 2. Bangunkan Kurir (WorkManager) untuk mengirim data di background
            val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val syncWork = OneTimeWorkRequestBuilder<SyncKmsWorker>()
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context).enqueue(syncWork)

            // 3. Beri respon sukses instan ke layar UI
            val fakeResponse = PengukuranResponse("Disimpan di perangkat.", null, null)
            emit(ResultWrapper.Success(fakeResponse))

        } catch (e: Exception) {
            emit(ResultWrapper.Error("Gagal menyimpan ke perangkat: ${e.message}"))
        }
    }
}