package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
    onNavigateToDaftarBalita: () -> Unit = {},
    onNavigateToTambahAnak: () -> Unit = {},
    onNavigateToDetailAnak: (Int, String, Int, String) -> Unit = { _, _, _, _ -> },
    viewModel: DashboardViewModel = viewModel()
) {
    val state = viewModel.dashboardState.value
    val logoutCompleted = viewModel.logoutCompleted.value

    // --- KOMPONEN UNTUK NOTIFIKASI SNACKBAR ---
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userRole) {
        viewModel.loadDashboardData(userRole)
    }

    LaunchedEffect(logoutCompleted) {
        if (logoutCompleted) {
            onLogout()
            viewModel.resetLogoutState()
        }
    }

    // --- TRIGGER NOTIFIKASI DARI VIEWMODEL ---
    LaunchedEffect(viewModel.syncMessage.value) {
        viewModel.syncMessage.value?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSyncMessage() // Bersihkan pesan setelah ditampilkan
        }
    }

    // Scaffold Induk untuk menampilkan Snackbar di atas semua layar Role
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            // 1. TAMPILAN KONTEN UTAMA (Sesuai Role)
            when (state) {
                is DashboardState.Loading -> {
                    // Loading utama berbentuk lingkaran di tengah layar (saat data benar-benar kosong)
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
                        onNavigateToKelolaKader = onNavigateToKelolaKader,
                        onNavigateToDaftarBalita = onNavigateToDaftarBalita
                    )
                }
                is DashboardState.KaderData -> {
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
                        onLogout = { viewModel.logout() },
                        onNavigateToTambahAnak = onNavigateToTambahAnak,
                        onNavigateToRiwayat = onNavigateToDetailAnak
                    )
                }
            }

            // 2. INDIKATOR LOADING OFFLINE-SYNC (Garis biru di bagian paling atas)
            // Ini akan overlay (menumpuk) di atas layar dashboard role apa pun
            if (viewModel.isSyncing.value) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter), // Posisikan di pucuk atas
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}