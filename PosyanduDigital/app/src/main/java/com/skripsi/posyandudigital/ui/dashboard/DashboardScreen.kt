package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skripsi.posyandudigital.data.remote.dto.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userRole: String,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val logoutCompleted = viewModel.logoutCompleted.value

    LaunchedEffect(logoutCompleted) {
        if (logoutCompleted) {
            onLogout()
        }
    }

    LaunchedEffect(key1 = userRole) {
        viewModel.loadDashboardData(userRole)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            val state = viewModel.dashboardState.value

            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                when (state) {
                    is DashboardState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is DashboardState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    is DashboardState.SuperAdminData -> SuperAdminDashboard(state.data)
                    is DashboardState.AdminData -> AdminDashboard(state.data)
                    is DashboardState.KaderData -> KaderDashboard(state.data)
                    is DashboardState.OrangTuaData -> OrangTuaDashboard(state.data)
                }
            }
        }
    }
}

@Composable
fun StatsCard(
    label: String,
    value: String,
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SuperAdminDashboard(data: SuperAdminDashboardDto) {
    val stats = listOf(
        Pair("Admin Aktif", data.totalAdminAktif.toString()),
        Pair("Kader Aktif", data.totalKaderAktif.toString()),
        Pair("Desa Terdaftar", data.totalDesaTerdaftar.toString()),
        Pair("Posyandu Aktif", data.totalPosyanduAktif.toString()),
        Pair("Balita Terpantau", data.totalBalitaTerpantau.toString()),
        Pair("Orang Tua Verif.", data.totalOrangTuaTerverifikasi.toString())
    )

    val cardColors = listOf(
        Color(0xFF00C853), Color(0xFF0091EA), Color(0xFFAEEA00),
        Color(0xFFFFAB00), Color(0xFFFF3D00), Color(0xFFD500F9)
    )

    val icons = listOf(
        Icons.Default.Shield, Icons.Default.Groups, Icons.Default.HomeWork,
        Icons.Default.LocalHospital, Icons.Default.ChildCare, Icons.Default.FamilyRestroom
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dashboard Super Admin", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Kabupaten Subang", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(250.dp),
            userScrollEnabled = false
        ) {
            itemsIndexed(stats) { index, (label, value) ->
                StatsCard(
                    label = label,
                    value = value,
                    icon = icons[index],
                    backgroundColor = cardColors[index % cardColors.size]
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Manajemen Sistem", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Icon(imageVector = Icons.Default.ManageAccounts, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("Kelola Akun Admin Desa")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Icon(imageVector = Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("Kelola Akun Kader")
        }
    }
}

@Composable
fun AdminDashboard(data: AdminDashboardDto) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dashboard Admin Desa", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        // PERBAIKAN: Gunakan operator '?:' untuk memberikan nilai default jika null
        Text(data.namaDesa ?: "Nama Desa Tidak Ditemukan", style = MaterialTheme.typography.titleMedium)
        // ... Tampilkan kartu statistik dan tombol untuk Admin
    }
}

@Composable
fun KaderDashboard(data: KaderDashboardDto) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Dashboard Kader", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        // PERBAIKAN: Gunakan '?:' untuk nilai default
        Text("${data.namaPosyandu ?: ""}, ${data.namaDesa ?: ""}", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { /* TODO: Navigasi ke layar pencatatan */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Mulai Pencatatan & Penimbangan", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Balita", style = MaterialTheme.typography.titleMedium)
                    Text(data.totalBalitaDiPosyandu.toString(), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                }
            }
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Verifikasi Ortu", style = MaterialTheme.typography.titleMedium)
                    Text(data.totalOrangTuaMenungguVerifikasi.toString(), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun OrangTuaDashboard(data: OrangTuaDashboardDto) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Tumbuh Kembang Anak", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        // PERBAIKAN: Akses data bertingkat dan gunakan '?:' untuk nilai default
        Text(data.anak?.namaAnak ?: "Nama Anak Tidak Ditemukan", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))

        // Gunakan 'let' untuk keamanan null pada objek kmsTerakhir
        data.kmsTerakhir?.let { kms ->
            Card(modifier = Modifier.fillMaxWidth()){
                Column(modifier = Modifier.padding(16.dp)){
                    Text(
                        "Pencatatan Terakhir (${kms.tanggalPencatatan ?: "-"})",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Berat Badan: ${kms.beratBadan} kg")
                    Text("Tinggi Badan: ${kms.tinggiBadan} cm")
                    Text(
                        "Status Gizi: ${kms.statusGizi ?: "Belum Ada"}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } ?: run {
            Text("Belum ada data pencatatan.")
        }
    }
}

