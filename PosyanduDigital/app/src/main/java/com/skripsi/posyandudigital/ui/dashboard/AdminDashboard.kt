package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skripsi.posyandudigital.data.remote.dto.AdminDashboardDto
import com.skripsi.posyandudigital.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDesaDashboardScreen(
    data: AdminDashboardDto,
    onLogout: () -> Unit,
    onNavigateToKelolaKader: () -> Unit
) {
    val stats = listOf(
        StatCardInfoWithColor("Total Balita", data.totalBalitaTerpantau.toString(), Icons.Default.ChildCare),
        StatCardInfoWithColor("Gizi Buruk", data.totalGiziBuruk.toString(), Icons.Default.Warning, iconColor = CriticalRed),
        StatCardInfoWithColor("Gizi Kurang", data.totalGiziKurang.toString(), Icons.Default.SentimentVeryDissatisfied, iconColor = WarningYellow),
        StatCardInfoWithColor("Gizi Baik", data.totalGiziBaik.toString(), Icons.Default.SentimentSatisfied, iconColor = HealthyGreen),
        StatCardInfoWithColor("Gizi Lebih", data.totalGiziLebih.toString(), Icons.Default.SentimentSatisfied),
        StatCardInfoWithColor("Total Kader", data.totalKaderAktif.toString(), Icons.Default.Groups)
    )

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Admin Desa", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = TextSecondary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(data.namaDesa ?: "Nama Desa", style = MaterialTheme.typography.titleMedium, color = TextSecondary, modifier = Modifier.padding(bottom = 24.dp))

            // Menggunakan komponen yang sudah ada di DashboardComponent.kt
            StatsGridForAdmin(stats = stats)

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Aksi (Saya ganti namanya agar tidak bentrok dengan yang ada di DashboardComponent)
            AdminDesaActionButtons(onKelolaKaderClick = onNavigateToKelolaKader)
        }
    }
}

// --- Komponen Tombol Khusus Admin Desa ---
// Kita buat dengan nama baru agar tidak bentrok dengan 'ActionButtonsForAdmin' di DashboardComponent.kt
@Composable
fun AdminDesaActionButtons(
    onKelolaKaderClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onKelolaKaderClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Kelola Kader", fontSize = 16.sp)
        }

        OutlinedButton(
            onClick = { /* Nanti Saja */ },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, PrimaryBlue),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
        ) {
            Text("Input Pencatatan & Penimbangan", fontSize = 16.sp)
        }
    }
}