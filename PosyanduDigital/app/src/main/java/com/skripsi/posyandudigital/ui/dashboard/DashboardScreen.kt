package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DashboardScreen(
    userRole: String,
    onLogout: () -> Unit,
    onNavigateToKelolaAdmin: () -> Unit = {},
    onNavigateToKelolaKader: () -> Unit = {},
    onNavigateToVerifikasi: () -> Unit = {},
    // --- TAMBAHAN: Parameter ini wajib ada agar MainActivity tidak error ---
    onNavigateToDaftarBalita: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel()
) {
    val state = viewModel.dashboardState.value
    val logoutCompleted = viewModel.logoutCompleted.value

    LaunchedEffect(userRole) {
        viewModel.loadDashboardData(userRole)
    }

    LaunchedEffect(logoutCompleted) {
        if (logoutCompleted) {
            onLogout()
            viewModel.resetLogoutState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is DashboardState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is DashboardState.Error -> {
                Text(
                    text = state.message,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is DashboardState.SuperAdminData -> {
                SuperAdminDashboardScreen(
                    data = state.data,
                    onLogout = { viewModel.logout() },
                    onNavigateToKelolaAdmin = onNavigateToKelolaAdmin,
                    onNavigateToKelolaKader = onNavigateToKelolaKader
                )
            }
            is DashboardState.AdminData -> {
                AdminDesaDashboardScreen(
                    data = state.data,
                    onLogout = { viewModel.logout() },
                    onNavigateToKelolaKader = onNavigateToKelolaKader
                )
            }
            is DashboardState.KaderData -> {
                // --- UPDATE: Meneruskan navigasi ke KaderDashboardScreen ---
                KaderDashboardScreen(
                    data = state.data,
                    onLogout = { viewModel.logout() },
                    onNavigateToVerifikasi = onNavigateToVerifikasi,
                    onNavigateToDaftarBalita = onNavigateToDaftarBalita
                )
            }
            is DashboardState.OrangTuaData -> {
                OrangTuaDashboardScreen(
                    data = state.data,
                    onLogout = { viewModel.logout() }
                )
            }
        }
    }
}