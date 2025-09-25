package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DashboardScreen(
    userRole: String,
    viewModel: DashboardViewModel = viewModel()
) {
    LaunchedEffect(key1 = userRole) {
        viewModel.loadDashboardData(userRole)
    }

    // Ambil state dari ViewModel untuk menentukan apa yang harus ditampilkan
    val state = viewModel.dashboardState.value

    // Gunakan 'when' untuk menampilkan UI yang sesuai dengan state saat ini
    when (state) {
        is DashboardState.Loading -> { // Tampilkan loading indicator di tengah layar
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text("Memuat data...", modifier = Modifier.padding(top = 8.dp))
            }
        }        is DashboardState.Error -> {
            // Tampilkan pesan error
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Error: ${state.message}")
            }
        }
        // Jika data berhasil dimuat, tampilkan dashboard yang sesuai dengan peran
        is DashboardState.KaderData -> KaderDashboard(state.data)
        is DashboardState.AdminData -> AdminDashboard(state.data)
        is DashboardState.SuperAdminData -> SuperAdminDashboard(state.data)
        is DashboardState.OrangTuaData -> OrangTuaDashboard(state.data)
    }
}


// --- Tampilan Spesifik untuk Setiap Peran (dengan data) ---
// Ganti 'Any' dengan DTO yang sesuai setelah Anda membuatnya

@Composable
fun KaderDashboard(data: Any) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dashboard Kader", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        // TODO: Tampilkan data dari 'data'
        Text("Data diterima: $data")
    }
}

@Composable
fun AdminDashboard(data: Any) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dashboard Admin Desa", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        // TODO: Tampilkan data dari 'data'
        Text("Data diterima: $data")
    }
}

@Composable
fun SuperAdminDashboard(data: Any) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dashboard Super Admin", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        // TODO: Tampilkan data dari 'data'
        Text("Data diterima: $data")
    }
}

@Composable
fun OrangTuaDashboard(data: Any) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dashboard Orang Tua", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        // TODO: Tampilkan data dari 'data'
        Text("Data diterima: $data")
    }
}
