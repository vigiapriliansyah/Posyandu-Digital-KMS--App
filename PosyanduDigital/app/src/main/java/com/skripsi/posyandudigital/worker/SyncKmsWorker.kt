package com.skripsi.posyandudigital.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.skripsi.posyandudigital.data.local.AppDatabase
import com.skripsi.posyandudigital.data.remote.api.RetrofitClient
import com.skripsi.posyandudigital.data.remote.dto.CreatePengukuranRequest
import com.skripsi.posyandudigital.data.session.SessionManager
import kotlinx.coroutines.flow.firstOrNull

class SyncKmsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val dao = AppDatabase.getDatabase(applicationContext).kmsDao()
        val sessionManager = SessionManager(applicationContext)
        val apiService = RetrofitClient.instance

        val unsyncedRecords = dao.getUnsyncedRecords()
        if (unsyncedRecords.isEmpty()) {
            return Result.success()
        }

        val token = sessionManager.getToken().firstOrNull() ?: return Result.retry()
        var allSuccess = true

        for (record in unsyncedRecords) {
            try {
                val request = CreatePengukuranRequest(
                    anakId = record.anakId,
                    umurBulan = record.umurBulan,
                    beratBadan = record.beratBadan,
                    tinggiBadan = record.tinggiBadan,
                    kbm = record.kbm,
                    statusNaikTurun = record.statusNaikTurun,
                    asiEksklusif = record.asiEksklusif,
                    tanggalPencatatan = record.tanggalPencatatan,
                    catatan = record.catatan
                )

                val response = apiService.createPengukuran("Bearer $token", request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val apiId = body.data?.id ?: 0

                    val statusGiziJson = body.analisis?.let {
                        """{"bb_u":"${it.bbU}","warna_kms":"${it.warnaKms}"}"""
                    }

                    dao.markAsSynced(record.localId, apiId, statusGiziJson)
                } else {
                    allSuccess = false
                }
            } catch (e: Exception) {
                allSuccess = false
            }
        }

        return if (allSuccess) Result.success() else Result.retry()
    }
}