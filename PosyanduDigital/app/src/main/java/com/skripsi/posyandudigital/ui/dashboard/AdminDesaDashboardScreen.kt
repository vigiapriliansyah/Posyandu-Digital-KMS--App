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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skripsi.posyandudigital.data.remote.dto.AdminDashboardDto
import com.skripsi.posyandudigital.ui.theme.*

// Data class untuk kartu statistik, tidak perlu diubah
data class StatCardInfoWithColor(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val iconColor: Color = PrimaryBlue
)

// Signature diubah untuk menerima data DTO dan lambda onLogout
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDesaDashboardScreen(data: AdminDashboardDto, onLogout: () -> Unit) {
    val stats = listOf(
        StatCardInfoWithColor("Total Balita Terpantau", data.totalBalitaTerpantau.toString(), Icons.Default.ChildCare),
        StatCardInfoWithColor("Gizi Buruk", data.giziBuruk.toString(), Icons.Default.Warning, iconColor = CriticalRed),
        StatCardInfoWithColor("Gizi Kurang", data.giziKurang.toString(), Icons.Default.SentimentVeryDissatisfied, iconColor = WarningYellow),
        StatCardInfoWithColor("Gizi Baik", data.giziBaik.toString(), Icons.Default.SentimentSatisfied, iconColor = HealthyGreen),
        StatCardInfoWithColor("Gizi Lebih", data.giziLebih.toString(), Icons.Default.SentimentVerySatisfied),
        StatCardInfoWithColor("Total Kader Aktif", data.totalKaderAktif.toString(), Icons.Default.Groups)
    )

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Admin Desa", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = data.namaDesa ?: "Nama Desa Tidak Ditemukan",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            StatsGridForAdmin(stats = stats)

            Spacer(modifier = Modifier.height(32.dp))

            ActionButtonsForAdmin()
        }
    }
}

// Komponen di bawah ini tidak perlu diubah
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatsGridForAdmin(stats: List<StatCardInfoWithColor>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        mainAxisSpacing = 16.dp,
        crossAxisSpacing = 16.dp,
        maxItemsInEachRow = 2
    ) {
        stats.forEach { stat ->
            Box(modifier = Modifier.weight(1f)) {
                StatCardWithColor(info = stat)
            }
        }
    }
}

@Composable
fun StatCardWithColor(info: StatCardInfoWithColor) {
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
                tint = info.iconColor,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = info.value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (info.iconColor != PrimaryBlue && info.iconColor != HealthyGreen) info.iconColor else TextPrimary
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
fun ActionButtonsForAdmin() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { /* Navigasi ke Kelola Kader */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Kelola Kader", fontSize = 16.sp)
        }
        OutlinedButton(
            onClick = { /* Navigasi ke Input Pencatatan */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, PrimaryBlue),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
        ) {
            Text("Input Pencatatan & Penimbangan", fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun AdminDesaDashboardPreview() {
    AdminDesaDashboardScreen(
        data = AdminDashboardDto(
            namaDesa = "Desa Jayagiri",
            totalBalitaTerpantau = 312,
            giziBuruk = 5,
            giziKurang = 21,
            giziBaik = 275,
            giziLebih = 11,
            totalKaderAktif = 8
        ),
        onLogout = {}
    )
}