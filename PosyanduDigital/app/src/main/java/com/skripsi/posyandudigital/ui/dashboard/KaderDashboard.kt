package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skripsi.posyandudigital.data.remote.dto.KaderDashboardDto
import com.skripsi.posyandudigital.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun KaderDashboardScreen(data: KaderDashboardDto, onLogout: () -> Unit) {
    // Data untuk statistik dengan ikon dan warna yang sesuai
    val kaderStats = listOf(
        StatCardInfoWithColor("Total Balita", data.totalBalitaDiPosyandu.toString(), Icons.Default.ChildCare, iconColor = PrimaryBlue),
        StatCardInfoWithColor("Gizi Buruk", data.totalGiziBuruk.toString(), Icons.Default.Warning, iconColor = CriticalRed),
        StatCardInfoWithColor("Gizi Kurang", data.totalGiziKurang.toString(), Icons.Default.SentimentVeryDissatisfied, iconColor = WarningYellow),
        StatCardInfoWithColor("Gizi Baik", data.totalGiziBaik.toString(), Icons.Default.SentimentSatisfied, iconColor = HealthyGreen),
        StatCardInfoWithColor("Gizi Lebih", data.totalGiziLebih.toString(), Icons.Default.SentimentSatisfiedAlt, iconColor = WarningYellow),
        StatCardInfoWithColor("Verifikasi Ortu", data.totalOrangTuaMenungguVerifikasi.toString(), Icons.Default.HowToReg, iconColor = WarningYellow)
    )

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Kader", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${data.namaPosyandu ?: "Nama Posyandu"} - ${data.namaDesa ?: "Nama Desa"}",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Menggunakan komponen StatsGridForAdmin yang sudah ada untuk konsistensi
            StatsGridForAdmin(stats = kaderStats)

            Spacer(modifier = Modifier.height(32.dp))
            KaderActionButtons()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

