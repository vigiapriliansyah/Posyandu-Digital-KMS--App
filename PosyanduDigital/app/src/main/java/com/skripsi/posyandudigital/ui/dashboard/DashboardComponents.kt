package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skripsi.posyandudigital.data.remote.dto.KmsSimpleDto
import com.skripsi.posyandudigital.ui.theme.*

// --- Komponen untuk Super Admin ---
data class StatCardInfo(val title: String, val value: String, val icon: ImageVector)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatsGrid(stats: List<StatCardInfo>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        maxItemsInEachRow = 2
    ) {
        stats.forEach { stat ->
            StatCard(info = stat, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(info: StatCardInfo, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = info.icon, contentDescription = info.title, tint = PrimaryBlue, modifier = Modifier.size(28.dp))
            Text(text = info.value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = info.title, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, lineHeight = 18.sp)
        }
    }
}

@Composable
fun ActionButtons() {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = { /* Navigasi ke Kelola Akun Admin */ }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)) {
            Text("Kelola Akun Admin Desa", fontSize = 16.sp)
        }
        OutlinedButton(onClick = { /* Navigasi ke Kelola Akun Kader */ }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, PrimaryBlue), colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)) {
            Text("Kelola Akun Kader", fontSize = 16.sp)
        }
    }
}

// --- Komponen untuk Admin Desa ---
data class StatCardInfoWithColor(val title: String, val value: String, val icon: ImageVector, val iconColor: Color = PrimaryBlue)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatsGridForAdmin(stats: List<StatCardInfoWithColor>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        maxItemsInEachRow = 2
    ) {
        stats.forEach { stat ->
            StatCardWithColor(info = stat, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCardWithColor(info: StatCardInfoWithColor, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = info.icon, contentDescription = info.title, tint = info.iconColor, modifier = Modifier.size(28.dp))
            Text(text = info.value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = if (info.iconColor != PrimaryBlue && info.iconColor != HealthyGreen) info.iconColor else TextPrimary)
            Text(text = info.title, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, lineHeight = 18.sp)
        }
    }
}

@Composable
fun ActionButtonsForAdmin() {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = { /* Navigasi ke Kelola Kader */ }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)) {
            Text("Kelola Kader", fontSize = 16.sp)
        }
        OutlinedButton(onClick = { /* Navigasi ke Input Pencatatan */ }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, PrimaryBlue), colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)) {
            Text("Input Pencatatan & Penimbangan", fontSize = 16.sp)
        }
    }
}


// --- KOMPONEN UNTUK KADER (DIPERBAIKI) ---
// Perbaikan: Menambahkan parameter yang dibutuhkan oleh KaderDashboardScreen
data class KaderStatInfo(val title: String, val value: String, val color: Color)

@Composable
fun KaderStatCard(info: KaderStatInfo) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = info.color),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = info.value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = info.title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun KaderActionButtons(
    verifCount: Int, // Parameter untuk jumlah verifikasi
    onVerifikasiClick: () -> Unit, // Callback klik verifikasi
    onDaftarBalitaClick: () -> Unit // Callback klik daftar balita
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Tombol Verifikasi
        Button(
            onClick = onVerifikasiClick,
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

        // Tombol Input (Placeholder dulu)
        OutlinedButton(
            onClick = { /* Nanti disambungkan ke Input Screen */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, PrimaryBlue),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EditNote, contentDescription = null, tint = PrimaryBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Input Penimbangan Balita", fontSize = 16.sp)
            }
        }

        // Tombol Daftar Balita
        OutlinedButton(
            onClick = onDaftarBalitaClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, TextSecondary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.List, contentDescription = null, tint = TextSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lihat Daftar Balita", fontSize = 16.sp)
            }
        }
    }
}

// --- Komponen untuk Orang Tua ---
@Composable
fun ChildSelector(currentChild: String, onChildSelected: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = currentChild, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
        Icon(Icons.Default.ArrowDropDown, contentDescription = "Pilih Anak", tint = TextSecondary)
    }
}

@Composable
fun GrowthChartPlaceholder() {
    Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).background(Color.Gray.copy(alpha = 0.1f)).padding(16.dp), contentAlignment = Alignment.Center) {
        Text(text = "[Grafik Garis Tren Berat Badan Anak]", color = TextSecondary, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun LastVisitSummaryCard(kms: KmsSimpleDto?) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text("Ringkasan Kunjungan Terakhir", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))

            if (kms != null) {
                SummaryItem(Icons.Default.CalendarToday, "Tanggal", kms.tanggalPencatatan ?: "-")
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = BackgroundLight)
                SummaryItem(Icons.Default.MonitorWeight, "Berat Badan", "${kms.beratBadan} kg")
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = BackgroundLight)
                SummaryItem(Icons.Default.Star, "Status Gizi", kms.statusGizi ?: "N/A", highlightColor = HealthyGreen)
            } else {
                Text("Belum ada data kunjungan.", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun SummaryItem(icon: ImageVector, label: String, value: String, highlightColor: Color? = null) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(imageVector = icon, contentDescription = label, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelLarge, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, style = MaterialTheme.typography.bodyLarge, color = highlightColor ?: TextPrimary, fontWeight = if (highlightColor != null) FontWeight.Bold else FontWeight.Normal)
        }
    }
}