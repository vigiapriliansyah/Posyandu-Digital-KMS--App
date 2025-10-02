package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skripsi.posyandudigital.data.remote.dto.OrangTuaDashboardDto
import com.skripsi.posyandudigital.ui.theme.*

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
