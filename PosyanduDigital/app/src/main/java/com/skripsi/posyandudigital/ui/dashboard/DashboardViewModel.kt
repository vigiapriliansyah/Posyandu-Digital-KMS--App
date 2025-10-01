package com.skripsi.posyandudigital.ui.dashboard

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.posyandudigital.data.remote.api.RetrofitClient
import com.skripsi.posyandudigital.data.remote.dto.*
import com.skripsi.posyandudigital.data.session.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Sealed class sekarang menggunakan DTO yang spesifik
sealed class DashboardState {
    object Loading : DashboardState()
    data class SuperAdminData(val data: SuperAdminDashboardDto) : DashboardState()
    data class AdminData(val data: AdminDashboardDto) : DashboardState()
    data class KaderData(val data: KaderDashboardDto) : DashboardState()
    data class OrangTuaData(val data: OrangTuaDashboardDto) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(getApplication())

    private val _dashboardState = mutableStateOf<DashboardState>(DashboardState.Loading)
    val dashboardState: State<DashboardState> = _dashboardState

    private val _logoutCompleted = mutableStateOf(false)
    val logoutCompleted = _logoutCompleted
    fun loadDashboardData(role: String) {
        viewModelScope.launch {
            _dashboardState.value = DashboardState.Loading
            try {
                val token = sessionManager.getToken().first()
                if (token == null) {
                    _dashboardState.value = DashboardState.Error("Sesi tidak valid. Silakan login ulang.")
                    return@launch
                }

                val authToken = "Bearer $token"
                val cleanRole = role.trim().lowercase()

                when (cleanRole) {
                    "superadmin" -> {
                        val response = RetrofitClient.instance.getSuperAdminDashboard(authToken)
                        if (response.isSuccessful && response.body() != null) {
                            _dashboardState.value = DashboardState.SuperAdminData(response.body()!!)
                        } else {
                            _dashboardState.value = DashboardState.Error("Gagal memuat data SuperAdmin. Kode: ${response.code()}")
                        }
                    }
                    "admin" -> {
                        val response = RetrofitClient.instance.getAdminDashboard(authToken)
                        if (response.isSuccessful && response.body() != null) {
                            _dashboardState.value = DashboardState.AdminData(response.body()!!)
                        } else {
                            _dashboardState.value = DashboardState.Error("Gagal memuat data Admin. Kode: ${response.code()}")
                        }
                    }
                    "kader" -> {
                        val response = RetrofitClient.instance.getKaderDashboard(authToken)
                        if (response.isSuccessful && response.body() != null) {
                            _dashboardState.value = DashboardState.KaderData(response.body()!!)
                        } else {
                            _dashboardState.value = DashboardState.Error("Gagal memuat data Kader. Kode: ${response.code()}")
                        }
                    }
                    "orangtua" -> {
                        val response = RetrofitClient.instance.getOrangTuaDashboard(authToken)
                        if (response.isSuccessful && response.body() != null) {
                            _dashboardState.value = DashboardState.OrangTuaData(response.body()!!)
                        } else {
                            _dashboardState.value = DashboardState.Error("Gagal memuat data Orang Tua. Kode: ${response.code()}")
                        }
                    }
                    else -> _dashboardState.value = DashboardState.Error("Peran pengguna tidak valid: $role")
                }
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error("Error jaringan: ${e.message}")
            }
        }
    }
    fun logout(){
        viewModelScope.launch {
            sessionManager.clearSession()
            _logoutCompleted.value = true
        }
    }
}

