package com.skripsi.posyandudigital.ui.dashboard

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skripsi.posyandudigital.data.local.AppDatabase
import com.skripsi.posyandudigital.data.local.DashboardCacheEntity
import com.skripsi.posyandudigital.data.remote.api.RetrofitClient
import com.skripsi.posyandudigital.data.remote.dto.*
import com.skripsi.posyandudigital.data.session.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

sealed class DashboardState {
    object Loading : DashboardState()
    data class SuperAdminData(val data: SuperAdminDashboardDto) : DashboardState()
    data class AdminData(val data: AdminDashboardDto) : DashboardState()
    data class KaderData(val data: KaderDashboardDto) : DashboardState()
    data class OrangTuaData(val data: OrangTuaDashboardDto) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)
    private val apiService = RetrofitClient.instance
    private val cacheDao = AppDatabase.getDatabase(application).dashboardCacheDao()
    private val gson = Gson()

    private val _dashboardState = mutableStateOf<DashboardState>(DashboardState.Loading)
    val dashboardState: State<DashboardState> = _dashboardState

    private val _logoutCompleted = mutableStateOf(false)
    val logoutCompleted: State<Boolean> = _logoutCompleted

    // --- TAMBAHAN BARU: State untuk fitur sinkronisasi ---
    private val _isSyncing = mutableStateOf(false)
    val isSyncing: State<Boolean> = _isSyncing

    private val _syncMessage = mutableStateOf<String?>(null)
    val syncMessage: State<String?> = _syncMessage

    fun clearSyncMessage() {
        _syncMessage.value = null
    }
    // -----------------------------------------------------

    fun loadDashboardData(role: String) {
        viewModelScope.launch {
            val cleanRole = role.trim().lowercase()

            val cachedJson = cacheDao.getDashboardData(cleanRole)
            if (cachedJson != null) {
                try {
                    when (cleanRole) {
                        "superadmin" -> _dashboardState.value = DashboardState.SuperAdminData(gson.fromJson(cachedJson, SuperAdminDashboardDto::class.java))
                        "admin" -> _dashboardState.value = DashboardState.AdminData(gson.fromJson(cachedJson, AdminDashboardDto::class.java))
                        "kader" -> _dashboardState.value = DashboardState.KaderData(gson.fromJson(cachedJson, KaderDashboardDto::class.java))
                        "orangtua" -> _dashboardState.value = DashboardState.OrangTuaData(gson.fromJson(cachedJson, OrangTuaDashboardDto::class.java))
                    }
                } catch (e: Exception) { }
            } else {
                _dashboardState.value = DashboardState.Loading
            }

            _isSyncing.value = true

            try {
                val token = sessionManager.getToken().firstOrNull()
                if (token.isNullOrBlank()) {
                    if (cachedJson == null) _dashboardState.value = DashboardState.Error("Sesi tidak valid. Silakan login ulang.")
                    _isSyncing.value = false
                    return@launch
                }

                val authToken = "Bearer $token"
                var jsonToCache: String? = null

                when (cleanRole) {
                    "superadmin" -> {
                        val response = apiService.getSuperAdminDashboard(authToken)
                        if (response.isSuccessful && response.body() != null) {
                            jsonToCache = gson.toJson(response.body()!!)
                            _dashboardState.value = DashboardState.SuperAdminData(response.body()!!)
                        }
                    }
                    "admin" -> {
                        val response = apiService.getAdminDashboard(authToken)
                        if (response.isSuccessful && response.body() != null) {
                            jsonToCache = gson.toJson(response.body()!!)
                            _dashboardState.value = DashboardState.AdminData(response.body()!!)
                        }
                    }
                    "kader" -> {
                        val response = apiService.getKaderDashboard(authToken)
                        if (response.isSuccessful && response.body() != null) {
                            jsonToCache = gson.toJson(response.body()!!)
                            _dashboardState.value = DashboardState.KaderData(response.body()!!)
                        }
                    }
                    "orangtua" -> {
                        val response = apiService.getOrangTuaDashboard(authToken)
                        if (response.isSuccessful && response.body() != null) {
                            jsonToCache = gson.toJson(response.body()!!)
                            _dashboardState.value = DashboardState.OrangTuaData(response.body()!!)
                        }
                    }
                    else -> {
                        if (cachedJson == null) _dashboardState.value = DashboardState.Error("Peran pengguna tidak valid: $role")
                    }
                }

                if (jsonToCache != null) {
                    cacheDao.saveDashboardData(DashboardCacheEntity(role = cleanRole, jsonData = jsonToCache))

                    if (cachedJson != null) {
                        _syncMessage.value = "Data terbaru berhasil diunduh untuk mode offline ✅"
                    }
                } else if (cachedJson == null) {
                    _dashboardState.value = DashboardState.Error("Gagal mengambil data dari server")
                }

            } catch (e: Exception) {
                if (cachedJson == null) {
                    _dashboardState.value = DashboardState.Error("Mode Offline. Belum ada data tersimpan di perangkat ini.")
                } else {
                    _syncMessage.value = "Anda sedang dalam Mode Offline ✈️"
                }
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _logoutCompleted.value = true
        }
    }

    fun resetLogoutState() {
        _logoutCompleted.value = false
    }
}