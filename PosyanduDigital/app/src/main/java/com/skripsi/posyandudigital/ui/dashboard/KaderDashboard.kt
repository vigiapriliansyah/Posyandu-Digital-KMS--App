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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skripsi.posyandudigital.data.remote.dto.KaderDashboardDto
import com.skripsi.posyandudigital.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaderDashboardScreen(
    data: KaderDashboardDto,
    onLogout: () -> Unit
    // Nanti kita tambahkan parameter navigasi di sini:
    // onNavigateToVerifikasi: () -> Unit,
    // onNavigateToInput: () -> Unit
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

            // Header Info Posyandu
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = data.namaPosyandu ?: "Posyandu",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Lokasi: ${data.namaDesa ?: "-"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Grid Statistik
            StatsGridForAdmin(stats = kaderStats)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Menu Utama",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Aksi Kader
            KaderActionButtons(
                verifCount = data.totalOrangTuaMenungguVerifikasi
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun KaderActionButtons(verifCount: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // Tombol Verifikasi (Highlight jika ada yang menunggu)
        Button(
            onClick = { /* Navigasi ke Verifikasi Screen */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (verifCount > 0) WarningYellow else PrimaryBlue
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.HowToReg, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Verifikasi Orang Tua ($verifCount)", fontSize = 16.sp)
            }
        }

        // Tombol Input Data
        OutlinedButton(
            onClick = { /* Navigasi ke Input Screen */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, PrimaryBlue)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EditNote, contentDescription = null, tint = PrimaryBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Input Penimbangan Balita", fontSize = 16.sp, color = PrimaryBlue)
            }
        }

        // Tombol Daftar Balita
        OutlinedButton(
            onClick = { /* Navigasi ke Daftar Balita */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, TextSecondary)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.List, contentDescription = null, tint = TextSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lihat Daftar Balita", fontSize = 16.sp, color = TextSecondary)
            }
        }
    }
}