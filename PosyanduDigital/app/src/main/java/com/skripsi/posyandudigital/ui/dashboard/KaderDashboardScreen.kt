package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Menggunakan kembali palet warna yang sudah didefinisikan
val PrimaryBlue = Color(0xFF4A90E2)
val TextPrimary = Color(0xFF212121)
val TextSecondary = Color(0xFF757575)
val BackgroundLight = Color(0xFFF7F9FC)
val CardBackground = Color.White
val WarningYellow = Color(0xFFF5A623)

data class QuickInfo(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val isHighlighted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaderDashboardScreen() {
    val quickInfoCards = listOf(
        QuickInfo("Total Balita di Posyandu Anda", "42", Icons.Default.ChildCare),
        QuickInfo("Orang Tua Menunggu Verifikasi", "3", Icons.Default.HowToReg, isHighlighted = true)
    )

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Kader", fontWeight = FontWeight.Bold) },
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
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Posyandu Melati 1, Desa Jayagiri",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Kartu Info Cepat di bagian atas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                quickInfoCards.forEach { info ->
                    Box(modifier = Modifier.weight(1f)) {
                        QuickInfoCard(info)
                    }
                }
            }

            // Spacer besar untuk memberikan ruang dan fokus ke tombol utama
            Spacer(modifier = Modifier.weight(1f))

            // Tombol Aksi Utama yang sangat menonjol
            PrimaryActionButton(
                text = "Mulai Pencatatan & Verifikasi",
                onClick = { /* Aksi utama kader */ }
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun QuickInfoCard(info: QuickInfo) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (info.isHighlighted) WarningYellow.copy(alpha = 0.1f) else CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (info.isHighlighted) BorderStroke(1.dp, WarningYellow) else null
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = info.value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = if (info.isHighlighted) WarningYellow else PrimaryBlue
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = info.title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun PrimaryActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp), // Tombol dibuat lebih besar
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 22.sp, // Font lebih besar
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp
        )
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun KaderDashboardPreview() {
    KaderDashboardScreen()
}