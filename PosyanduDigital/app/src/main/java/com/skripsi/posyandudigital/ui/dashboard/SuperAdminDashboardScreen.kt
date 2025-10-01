package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.skripsi.posyandudigital.ui.theme.*

// Data class untuk merepresentasikan setiap kartu statistik
data class StatCardInfo(
    val title: String,
    val value: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminDashboardScreen() {
    // Data dummy untuk preview
    val stats = listOf(
        StatCardInfo("Total Admin Aktif", "12", Icons.Default.AdminPanelSettings),
        StatCardInfo("Total Kader Aktif", "150", Icons.Default.Groups),
        StatCardInfo("Total Desa Terdaftar", "35", Icons.Default.HolidayVillage),
        StatCardInfo("Total Posyandu Aktif", "78", Icons.Default.LocalHospital),
        StatCardInfo("Total Balita Terpantau", "2,453", Icons.Default.ChildCare),
        StatCardInfo("Total Orang Tua", "1,890", Icons.Default.FamilyRestroom)
    )

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Super Admin", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            // Subjudul yang memberikan konteks
            Text(
                text = "Kabupaten Subang",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Grid untuk kartu statistik
            StatsGrid(stats = stats)

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Aksi Utama
            ActionButtons()
        }
    }
}

@Composable
fun StatsGrid(stats: List<StatCardInfo>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        content = {
            items(stats) { stat ->
                StatCard(info = stat)
            }
        }
    )
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
        // Tombol Utama (Primary Action)
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

        // Tombol Sekunder (Secondary Action)
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

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SuperAdminDashboardPreview() {
    // Anda perlu setup Theme.kt di proyek asli,
    // tapi untuk preview ini kita bisa langsung panggil screen-nya.
    SuperAdminDashboardScreen()
}