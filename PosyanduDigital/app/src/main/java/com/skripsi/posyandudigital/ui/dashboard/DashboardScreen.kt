package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userRole: String,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val logoutCompleted = viewModel.logoutCompleted.value

    // Effect untuk menangani navigasi setelah logout
    LaunchedEffect(logoutCompleted) {
        if (logoutCompleted) {
            onLogout()
        }
    }

    // Effect untuk memuat data dashboard berdasarkan peran pengguna
    LaunchedEffect(key1 = userRole) {
        viewModel.loadDashboardData(userRole)
    }

    // Scaffold tidak lagi memiliki TopAppBar di sini karena setiap
    // dashboard baru sudah memiliki TopAppBar-nya sendiri.
    // Ini memberikan kontrol yang lebih baik per layar.
    Box(modifier = Modifier.fillMaxSize()) {
        val state = viewModel.dashboardState.value

        when (state) {
            is DashboardState.Loading -> {
                // Tampilkan indikator loading di tengah layar
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DashboardState.Error -> {
                // Tampilkan pesan error
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            // Panggil Composable yang benar berdasarkan state
            is DashboardState.SuperAdminData -> SuperAdminDashboardScreen(state.data)
            is DashboardState.AdminData -> AdminDesaDashboardScreen(state.data)
            is DashboardState.KaderData -> KaderDashboardScreen(state.data)
            is DashboardState.OrangTuaData -> OrangTuaDashboardScreen(state.data)
        }
    }
}