package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skripsi.posyandudigital.data.remote.dto.Anak
import com.skripsi.posyandudigital.data.remote.dto.Kms
import com.skripsi.posyandudigital.data.remote.dto.OrangTuaDashboardDto
import com.skripsi.posyandudigital.ui.theme.*

// Signature diubah untuk menerima data DTO dan lambda onLogout
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrangTuaDashboardScreen(data: OrangTuaDashboardDto, onLogout: () -> Unit) {
    var selectedChild by remember { mutableStateOf(data.anak?.namaAnak ?: "Anak") }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Tumbuh Kembang Anak", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ChildSelector(
                currentChild = selectedChild,
                onChildSelected = { selectedChild = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            GrowthChartPlaceholder()

            Spacer(modifier = Modifier.height(24.dp))

            LastVisitSummaryCard(kms = data.kmsTerakhir)

            Spacer(modifier = Modifier.height(16.dp))

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

// Komponen di bawah ini tidak perlu diubah secara signifikan
@Composable
fun ChildSelector(currentChild: String, onChildSelected: (String) -> Unit) {
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
fun LastVisitSummaryCard(kms: Kms?) {
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

            if (kms != null) {
                SummaryItem(Icons.Default.CalendarToday, "Tanggal", kms.tanggalPencatatan ?: "-")
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = BackgroundLight)
                SummaryItem(Icons.Default.MonitorWeight, "Berat Badan", "${kms.beratBadan ?: 0} kg")
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = BackgroundLight)
                SummaryItem(Icons.Default.Star, "Status Gizi", kms.statusGizi ?: "N/A", highlightColor = HealthyGreen)
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = BackgroundLight)
                SummaryItem(Icons.Default.Note, "Catatan Petugas", kms.catatan ?: "Tidak ada catatan.")
            } else {
                Text("Belum ada data kunjungan terakhir.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
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
    OrangTuaDashboardScreen(
        data = OrangTuaDashboardDto(
            anak = Anak(namaAnak = "Ananda Budi"),
            kmsTerakhir = Kms(
                tanggalPencatatan = "25 September 2025",
                beratBadan = "10.5",
                tinggiBadan = "80",
                statusGizi = "Gizi Baik",
                catatan = "Ananda sangat aktif dan sehat. Pertahankan pola makan bergizi."
            )
        ),
        onLogout = {}
    )
}