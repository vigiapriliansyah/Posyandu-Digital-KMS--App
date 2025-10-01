package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Palet warna yang konsisten
val PrimaryBlue = Color(0xFF4A90E2)
val TextPrimary = Color(0xFF212121)
val TextSecondary = Color(0xFF757575)
val BackgroundLight = Color(0xFFF7F9FC)
val CardBackground = Color.White
val HealthyGreen = Color(0xFF50B878)
val WarningYellow = Color(0xFFF5A623)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrangTuaDashboardScreen() {
    var selectedChild by remember { mutableStateOf("Ananda Budi") }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Tumbuh Kembang Anak", fontWeight = FontWeight.Bold) },
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
            // Dropdown untuk memilih anak jika ada lebih dari satu
            ChildSelector(
                currentChild = selectedChild,
                onChildSelected = { selectedChild = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Placeholder untuk Grafik Garis Pertumbuhan
            GrowthChartPlaceholder()

            Spacer(modifier = Modifier.height(24.dp))

            // Kartu Ringkasan Kunjungan Terakhir
            LastVisitSummaryCard()

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Aksi Sekunder
            OutlinedButton(
                onClick = { /* Navigasi ke Riwayat Lengkap */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, PrimaryBlue)
            ) {
                Text("Lihat Riwayat Penimbangan Lengkap", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ChildSelector(currentChild: String, onChildSelected: (String) -> Unit) {
    // Implementasi dropdown sederhana
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentChild,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Icon(Icons.Default.ArrowDropDown, contentDescription = "Pilih Anak", tint = TextSecondary)
    }
}

@Composable
fun GrowthChartPlaceholder() {
    // Di aplikasi nyata, di sini akan ada komponen grafik dari library
    // seperti MPAndroidChart atau Vico.
    // Placeholder ini meniru tampilan area grafik KMS.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Gray.copy(alpha = 0.1f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "[Grafik Garis Tren Berat Badan Anak]\nArea hijau, kuning, dan merah akan digambar di latar belakang.",
            color = TextSecondary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun LastVisitSummaryCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                "Ringkasan Kunjungan Terakhir",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            SummaryItem(Icons.Default.CalendarToday, "Tanggal", "25 September 2025")
            Divider(modifier = Modifier.padding(vertical = 8.dp), color = BackgroundLight)
            SummaryItem(Icons.Default.MonitorWeight, "Berat Badan", "10.5 kg")
            Divider(modifier = Modifier.padding(vertical = 8.dp), color = BackgroundLight)
            SummaryItem(Icons.Default.Star, "Status Gizi", "Gizi Baik", highlightColor = HealthyGreen)
            Divider(modifier = Modifier.padding(vertical = 8.dp), color = BackgroundLight)
            SummaryItem(Icons.Default.Note, "Catatan Petugas", "Ananda sangat aktif dan sehat. Pertahankan pola makan bergizi.")
        }
    }
}

@Composable
fun SummaryItem(icon: ImageVector, label: String, value: String, highlightColor: Color? = null) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = PrimaryBlue,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = highlightColor ?: TextPrimary,
                fontWeight = if (highlightColor != null) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
fun OrangTuaDashboardPreview() {
    OrangTuaDashboardScreen()
}