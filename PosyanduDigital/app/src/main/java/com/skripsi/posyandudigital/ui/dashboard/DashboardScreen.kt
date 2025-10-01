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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skripsi.posyandudigital.data.remote.dto.*
import com.skripsi.posyandudigital.ui.theme.*

// Composable utama yang bertindak sebagai router
@Composable
fun DashboardScreen(
    userRole: String,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val logoutCompleted = viewModel.logoutCompleted.value

    LaunchedEffect(logoutCompleted) {
        if (logoutCompleted) {
            onLogout()
            viewModel.resetLogoutState() // Reset state setelah navigasi
        }
    }

    LaunchedEffect(key1 = userRole) {
        viewModel.loadDashboardData(userRole)
    }

    val state = viewModel.dashboardState.value

    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundLight) {
        when (state) {
            is DashboardState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DashboardState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }
            }
            is DashboardState.SuperAdminData -> SuperAdminDashboardScreen(data = state.data, onLogout = { viewModel.logout() })
            is DashboardState.AdminData -> AdminDesaDashboardScreen(data = state.data, onLogout = { viewModel.logout() })
            is DashboardState.KaderData -> KaderDashboardScreen(data = state.data, onLogout = { viewModel.logout() })
            is DashboardState.OrangTuaData -> OrangTuaDashboardScreen(data = state.data, onLogout = { viewModel.logout() })
        }
    }
}

// ========================================================================
// |                  IMPLEMENTASI UI UNTUK SETIAP PERAN                   |
// ========================================================================

// ... (SuperAdminDashboardScreen dan AdminDesaDashboardScreen tidak berubah) ...

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminDashboardScreen(data: SuperAdminDashboardDto, onLogout: () -> Unit) {
    val stats = listOf(
        StatCardInfo("Admin Aktif", data.totalAdminAktif.toString(), Icons.Default.AdminPanelSettings),
        StatCardInfo("Kader Aktif", data.totalKaderAktif.toString(), Icons.Default.Groups),
        StatCardInfo("Desa Terdaftar", data.totalDesaTerdaftar.toString(), Icons.Default.HolidayVillage),
        StatCardInfo("Posyandu Aktif", data.totalPosyanduAktif.toString(), Icons.Default.LocalHospital),
        StatCardInfo("Balita Terpantau", data.totalBalitaTerpantau.toString(), Icons.Default.ChildCare),
        StatCardInfo("Orang Tua", data.totalOrangTuaTerverifikasi.toString(), Icons.Default.FamilyRestroom)
    )

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Super Admin", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
            Text("Kabupaten Subang", style = MaterialTheme.typography.titleMedium, color = TextSecondary, modifier = Modifier.padding(bottom = 24.dp))
            StatsGrid(stats = stats)
            Spacer(modifier = Modifier.height(32.dp))
            ActionButtons()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDesaDashboardScreen(data: AdminDashboardDto, onLogout: () -> Unit) {
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
            StatsGridForAdmin(stats = stats)
            Spacer(modifier = Modifier.height(32.dp))
            ActionButtonsForAdmin()
        }
    }
}


// --- PERUBAHAN UTAMA DIMULAI DI SINI ---

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun KaderDashboardScreen(data: KaderDashboardDto, onLogout: () -> Unit) {
    // Menyiapkan data untuk ditampilkan di grid sesuai desain
    val kaderStats = listOf(
        KaderStatInfo("Total Balita Di Posyandu Anda", data.totalBalitaDiPosyandu.toString(), HealthyGreen),
        KaderStatInfo("Gizi Buruk", data.totalGiziBuruk.toString(), CriticalRed),
        KaderStatInfo("Gizi Kurang", data.totalGiziKurang.toString(), WarningYellow),
        KaderStatInfo("Gizi Baik (Normal)", data.totalGiziBaik.toString(), HealthyGreen),
        KaderStatInfo("Gizi Lebih", data.totalGiziLebih.toString(), WarningYellow),
        // --- PERBAIKAN: Mengganti WarningOrange dengan WarningYellow yang sudah ada ---
        KaderStatInfo("Orang Tua Menunggu Verifikasi", data.totalOrangTuaMenungguVerifikasi.toString(), WarningYellow)
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

            // Grid untuk statistik
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                maxItemsInEachRow = 3 // Menampilkan 3 item per baris
            ) {
                kaderStats.forEach { stat ->
                    // Menggunakan modifier weight agar setiap item memiliki lebar yang sama
                    Box(modifier = Modifier.weight(1f)) {
                        KaderStatCard(info = stat)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol-tombol aksi di bawah
            KaderActionButtons()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ... (OrangTuaDashboardScreen tidak berubah) ...

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrangTuaDashboardScreen(data: OrangTuaDashboardDto, onLogout: () -> Unit) {
    var selectedChild by remember { mutableStateOf(data.anak?.namaAnak ?: "Pilih Anak") }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Tumbuh Kembang Anak", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
            ChildSelector(
                currentChild = selectedChild,
                onChildSelected = { selectedChild = it }
            )
            Spacer(modifier = Modifier.height(24.dp))
            GrowthChartPlaceholder()
            Spacer(modifier = Modifier.height(24.dp))
            LastVisitSummaryCard(data.kmsTerakhir)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { /* Navigasi ke Riwayat Lengkap */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, PrimaryBlue)
            ) {
                Text("Lihat Riwayat Penimbangan Lengkap", fontSize = 16.sp)
            }
        }
    }
}


// ========================================================================
// |             KOMPONEN BANTUAN UNTUK SETIAP DASHBOARD                 |
// ========================================================================

// ... (Komponen Super Admin dan Admin Desa tidak berubah) ...

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


// --- KOMPONEN BARU UNTUK KADER ---
data class KaderStatInfo(val title: String, val value: String, val color: Color)

@Composable
fun KaderStatCard(info: KaderStatInfo) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = info.color),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Membuat card menjadi persegi
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
fun KaderActionButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ActionButton(text = "Kelola Kader", modifier = Modifier.weight(1f))
        ActionButton(text = "Input Pencatatan dan Penimbangan", modifier = Modifier.weight(1f))
        ActionButton(text = "Verifikasi Orang Tua", modifier = Modifier.weight(1f))
    }
}

@Composable
fun ActionButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.LightGray.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            color = TextSecondary
        )
    }
}


// --- Komponen untuk Orang Tua ---
// ... (Komponen Orang Tua tidak berubah) ...

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

