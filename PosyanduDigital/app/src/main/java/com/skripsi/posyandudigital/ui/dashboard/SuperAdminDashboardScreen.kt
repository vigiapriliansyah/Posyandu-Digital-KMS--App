package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skripsi.posyandudigital.data.remote.dto.SuperAdminDashboardDto
import com.skripsi.posyandudigital.ui.theme.*

// Data class untuk kartu statistik, tidak perlu diubah
data class StatCardInfo(
    val title: String,
    val value: String,
    val icon: ImageVector
)

// Signature diubah untuk menerima data DTO dan lambda onLogout
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminDashboardScreen(data: SuperAdminDashboardDto, onLogout: () -> Unit) {
    val stats = listOf(
        StatCardInfo("Total Admin Aktif", data.totalAdminAktif.toString(), Icons.Default.AdminPanelSettings),
        StatCardInfo("Total Kader Aktif", data.totalKaderAktif.toString(), Icons.Default.Groups),
        StatCardInfo("Total Desa Terdaftar", data.totalDesaTerdaftar.toString(), Icons.Default.HolidayVillage),
        StatCardInfo("Total Posyandu Aktif", data.totalPosyanduAktif.toString(), Icons.Default.LocalHospital),
        StatCardInfo("Total Balita Terpantau", data.totalBalitaTerpantau.toString(), Icons.Default.ChildCare),
        StatCardInfo("Total Orang Tua", data.totalOrangTuaTerverifikasi.toString(), Icons.Default.FamilyRestroom)
    )

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Super Admin", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight,
                    titleContentColor = TextPrimary
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp) // Padding horizontal dipindahkan ke sini
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Menambahkan scroll
        ) {
            Text(
                text = "Kabupaten Subang",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            StatsGrid(stats = stats)

            Spacer(modifier = Modifier.height(32.dp))

            ActionButtons()
        }
    }
}

// Komponen-komponen di bawah ini tidak perlu diubah
@Composable
fun StatsGrid(stats: List<StatCardInfo>) {
    // LazyVerticalGrid tidak boleh berada di dalam Column yang bisa di-scroll
    // tanpa memberikan tinggi yang tetap. Kita akan menggunakan Column biasa.
    // Jika jumlah item banyak, pendekatan lain diperlukan, tapi untuk 6 item ini cukup.
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        mainAxisSpacing = 16.dp,
        crossAxisSpacing = 16.dp,
        maxItemsInEachRow = 2
    ) {
        stats.forEach { stat ->
            Box(modifier = Modifier.weight(1f)) {
                StatCard(info = stat)
            }
        }
    }
}

@Composable
fun StatCard(info: StatCardInfo) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = info.icon,
                contentDescription = info.title,
                tint = PrimaryBlue,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = info.value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = info.title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ActionButtons() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { /* Navigasi ke Kelola Akun Admin */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Kelola Akun Admin Desa", fontSize = 16.sp)
        }
        OutlinedButton(
            onClick = { /* Navigasi ke Kelola Akun Kader */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, PrimaryBlue),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
        ) {
            Text("Kelola Akun Kader", fontSize = 16.sp)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SuperAdminDashboardPreview() {
    SuperAdminDashboardScreen(
        data = SuperAdminDashboardDto(
            totalAdminAktif = 12,
            totalKaderAktif = 150,
            totalDesaTerdaftar = 35,
            totalPosyanduAktif = 78,
            totalBalitaTerpantau = 2453,
            totalOrangTuaTerverifikasi = 1890
        ),
        onLogout = {} // Dummy lambda untuk preview
    )
}