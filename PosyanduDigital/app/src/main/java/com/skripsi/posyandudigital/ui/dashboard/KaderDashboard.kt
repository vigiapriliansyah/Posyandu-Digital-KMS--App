package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skripsi.posyandudigital.data.remote.dto.*
import com.skripsi.posyandudigital.ui.theme.*

// ... (SuperAdminDashboardScreen & AdminDesaDashboardScreen tetap sama) ...
// --- HANYA MENAMPILKAN KADER SCREEN YANG DIUPDATE ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaderDashboardScreen(
    data: KaderDashboardDto,
    onLogout: () -> Unit,
    onNavigateToVerifikasi: () -> Unit,
    // --- TAMBAHAN PARAMETER ---
    onNavigateToDaftarBalita: () -> Unit
) {
    val kaderStats = listOf(
        StatCardInfoWithColor("Total Balita", data.totalBalitaDiPosyandu.toString(), Icons.Default.ChildCare, iconColor = PrimaryBlue),
        StatCardInfoWithColor("Menunggu Verifikasi", data.totalOrangTuaMenungguVerifikasi.toString(), Icons.Default.HowToReg, iconColor = WarningYellow),
        StatCardInfoWithColor("Gizi Buruk", data.totalGiziBuruk.toString(), Icons.Default.Warning, iconColor = CriticalRed),
        StatCardInfoWithColor("Gizi Kurang", data.totalGiziKurang.toString(), Icons.Default.SentimentVeryDissatisfied, iconColor = WarningYellow),
        StatCardInfoWithColor("Gizi Baik", data.totalGiziBaik.toString(), Icons.Default.SentimentSatisfied, iconColor = HealthyGreen),
        StatCardInfoWithColor("Gizi Lebih", data.totalGiziLebih.toString(), Icons.Default.SentimentSatisfiedAlt, iconColor = WarningYellow)
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
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = data.namaPosyandu ?: "Posyandu", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = "Lokasi: ${data.namaDesa ?: "-"}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                }
            }
            StatsGridForAdmin(stats = kaderStats)
            Spacer(modifier = Modifier.height(32.dp))
            Text("Menu Utama", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
            Spacer(modifier = Modifier.height(16.dp))

            // --- UPDATE: Meneruskan fungsi klik ke komponen tombol ---
            KaderActionButtons(
                verifCount = data.totalOrangTuaMenungguVerifikasi,
                onVerifikasiClick = onNavigateToVerifikasi,
                onDaftarBalitaClick = onNavigateToDaftarBalita
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- (OrangTuaDashboardScreen dan lainnya tetap sama, pastikan file ini lengkap) ---