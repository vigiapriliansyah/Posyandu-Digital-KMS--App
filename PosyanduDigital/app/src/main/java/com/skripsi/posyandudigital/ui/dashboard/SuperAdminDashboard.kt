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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skripsi.posyandudigital.data.remote.dto.SuperAdminDashboardDto
import com.skripsi.posyandudigital.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminDashboardScreen(
    data: SuperAdminDashboardDto,
    onLogout: () -> Unit,
    onNavigateToKelolaAdmin: () -> Unit,
    onNavigateToKelolaKader: () -> Unit
) {
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
            ActionButtons(
                onKelolaAdminClick = onNavigateToKelolaAdmin,
                onKelolaKaderClick = onNavigateToKelolaKader
            )
        }
    }
}


@Composable
fun ActionButtons(
    onKelolaAdminClick: () -> Unit,
    onKelolaKaderClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onKelolaAdminClick, // Menggunakan parameter
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Kelola Akun Admin Desa", fontSize = 16.sp)
        }
        OutlinedButton(
            onClick = onKelolaKaderClick, // Menggunakan parameter
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

