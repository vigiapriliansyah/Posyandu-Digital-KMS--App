package com.skripsi.posyandudigital.ui.dashboard

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.posyandudigital.data.remote.api.RetrofitClient
import com.skripsi.posyandudigital.data.session.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Sealed class untuk menampung berbagai jenis state data dashboard
sealed class DashboardState {
    object Loading : DashboardState()
    data class KaderData(val data: Any) : DashboardState() // Ganti Any dengan DTO
    data class AdminData(val data: Any) : DashboardState()
    data class SuperAdminData(val data: Any) : DashboardState()
    data class OrangTuaData(val data: Any) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(getApplication())

    private val _dashboardState = mutableStateOf<DashboardState>(DashboardState.Loading)
    val dashboardState: State<DashboardState> = _dashboardState

    fun loadDashboardData(role: String) {
        viewModelScope.launch {
            _dashboardState.value = DashboardState.Loading
            try {
                val token = sessionManager.getToken().first()
                if (token == null) {
                    _dashboardState.value = DashboardState.Error("Sesi tidak valid. Silakan login ulang.")
                    return@launch
                }

                // --- TAMBAHAN DEBUGGING DI SINI ---
                // Baris ini akan mencetak isi pasti dari variabel 'role' ke Logcat
                println("DashboardViewModel received role: '$role'")
                // ------------------------------------

                val authToken = "Bearer $token"

                // Kita gunakan .trim() untuk menghapus spasi yang tidak disengaja
                val cleanRole = role.trim().lowercase()

                val response = when (cleanRole) {
                    "kader" -> RetrofitClient.instance.getKaderDashboard(authToken)
                    "admin" -> RetrofitClient.instance.getAdminDashboard(authToken)
                    "superadmin" -> RetrofitClient.instance.getSuperAdminDashboard(authToken)
                    "orangtua" -> RetrofitClient.instance.getOrangTuaDashboard(authToken)
                    else -> {
                        _dashboardState.value = DashboardState.Error("Peran pengguna tidak valid: $role")
                        null
                    }
                }

                if (response != null && response.isSuccessful) {
                    val data = response.body()
                    _dashboardState.value = when(cleanRole) {
                        "kader" -> DashboardState.KaderData(data!!)
                        "admin" -> DashboardState.AdminData(data!!)
                        "superadmin" -> DashboardState.SuperAdminData(data!!)
                        "orangtua" -> DashboardState.OrangTuaData(data!!)
                        else -> DashboardState.Error("Peran tidak valid setelah response") // Seharusnya tidak pernah terjadi
                    }
                } else if (response != null) {
                    _dashboardState.value = DashboardState.Error("Gagal memuat data. Kode: ${response.code()}")
                }

            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error("Error jaringan: ${e.message}")
            }
        }
    }
}
