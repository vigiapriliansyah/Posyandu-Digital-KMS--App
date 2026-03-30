package com.skripsi.posyandudigital.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skripsi.posyandudigital.data.local.AppDatabase
import com.skripsi.posyandudigital.data.remote.api.RetrofitClient
import com.skripsi.posyandudigital.data.remote.dto.KmsSimpleDto
import com.skripsi.posyandudigital.data.remote.dto.OrangTuaDashboardDto
import com.skripsi.posyandudigital.data.repository.PengukuranRepository
import com.skripsi.posyandudigital.data.session.SessionManager
import com.skripsi.posyandudigital.ui.pengukuran.KmsChart
import com.skripsi.posyandudigital.ui.pengukuran.PengukuranViewModel
import com.skripsi.posyandudigital.ui.pengukuran.PengukuranViewModelFactory
import com.skripsi.posyandudigital.ui.theme.*
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrangTuaDashboardScreen(
    data: OrangTuaDashboardDto,
    onLogout: () -> Unit,
    onNavigateToTambahAnak: () -> Unit,
    onNavigateToRiwayat: (Int, String, Int, String) -> Unit
) {
    val listAnak = data.daftarAnak ?: emptyList()
    var selectedAnak by remember { mutableStateOf(listAnak.firstOrNull()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // --- VIEWMODEL UNTUK MENGAMBIL DATA KMS (DENGAN OFFLINE SUPPORT) ---
    val context = LocalContext.current
    val factory = remember {
        val db = AppDatabase.getDatabase(context)
        PengukuranViewModelFactory(
            PengukuranRepository(RetrofitClient.instance, SessionManager(context), db.kmsDao(), context)
        )
    }
    val pengukuranViewModel: PengukuranViewModel = viewModel(factory = factory)
    val pengukuranState = pengukuranViewModel.state.value

    // Auto-refresh jika anak yang dipilih berubah
    LaunchedEffect(listAnak) {
        if (listAnak.isNotEmpty() && selectedAnak == null) {
            selectedAnak = listAnak.first()
        } else if (listAnak.isNotEmpty()) {
            selectedAnak = listAnak.find { it.id == selectedAnak?.id } ?: listAnak.first()
        }
    }

    LaunchedEffect(selectedAnak?.id) {
        selectedAnak?.id?.let { id ->
            pengukuranViewModel.loadRiwayat(id) // Ambil data riwayat anak dari backend
        }
    }

    val namaAnak = selectedAnak?.namaAnak ?: ""
    val jenisKelamin = selectedAnak?.jenisKelamin ?: "L"

    // Menggunakan umur real-time dari hitungan Backend berdasarkan Tanggal Lahir
    val umurRealTime = selectedAnak?.umurBulan ?: 0
    val latestVisit = pengukuranState.riwayatList.firstOrNull()

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
            if (listAnak.isEmpty()) {
                // UI JIKA BELUM ADA ANAK
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.ChildCare, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Belum Ada Data Anak", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Silakan hubungi Kader Posyandu untuk mendaftarkan profil balita Anda atau tambahkan secara mandiri.", textAlign = TextAlign.Center, color = TextSecondary, modifier = Modifier.padding(vertical = 8.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToTambahAnak,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Tambah Data Anak Sekarang")
                        }
                    }
                }
            } else {
                // UI JIKA SUDAH ADA ANAK
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ChildCare, contentDescription = "Anak", tint = PrimaryBlue, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Box {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { isDropdownExpanded = true }.padding(vertical = 4.dp)
                            ) {
                                Text(text = namaAnak, style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Pilih", tint = TextPrimary)
                            }
                            DropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                                listAnak.forEach { anakItem ->
                                    DropdownMenuItem(
                                        text = { Text(anakItem.namaAnak ?: "", fontWeight = if (anakItem.id == selectedAnak?.id) FontWeight.Bold else FontWeight.Normal) },
                                        onClick = { selectedAnak = anakItem; isDropdownExpanded = false }
                                    )
                                }
                            }
                        }
                        // MENAMPILKAN UMUR REAL-TIME
                        Text(text = "Umur: $umurRealTime Bulan", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }

                    // TOMBOL "+ TAMBAH"
                    OutlinedButton(
                        onClick = onNavigateToTambahAnak,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, PrimaryBlue),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah Anak", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah", fontSize = 14.sp, color = PrimaryBlue)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- INTEGRASI GRAFIK KMS ASLI ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Grafik Pertumbuhan KMS", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        val jkText = if (jenisKelamin == "L") "Laki-laki (Biru)" else "Perempuan (Pink)"
                        Text(jkText, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth().height(240.dp)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)).padding(4.dp)
                        ) {
                            if (pengukuranState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            } else {
                                // Memanggil grafik KMS
                                KmsChart(riwayatList = pengukuranState.riwayatList, jenisKelamin = jenisKelamin)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- INTEGRASI KUNJUNGAN TERAKHIR ---
                var statusGizi = "-"
                try {
                    if (!latestVisit?.statusGiziRaw.isNullOrBlank()) {
                        val jsonObj = JSONObject(latestVisit!!.statusGiziRaw)
                        statusGizi = jsonObj.optString("bb_u", "-")
                    }
                } catch (e: Exception) {}

                val mappedKms = latestVisit?.let {
                    KmsSimpleDto(
                        tanggalPencatatan = it.tanggalPencatatan,
                        beratBadan = it.beratBadan,
                        tinggiBadan = it.tinggiBadan ?: 0.0,
                        statusGizi = statusGizi
                    )
                }

                LastVisitSummaryCard(mappedKms)

                Spacer(modifier = Modifier.height(16.dp))

                // TOMBOL LIHAT RIWAYAT
                OutlinedButton(
                    onClick = {
                        selectedAnak?.let { anak ->
                            onNavigateToRiwayat(
                                anak.id,
                                anak.namaAnak ?: "",
                                anak.umurBulan ?: 0,
                                anak.jenisKelamin ?: "L"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PrimaryBlue)
                ) {
                    Text("Lihat Riwayat Penimbangan Lengkap", fontSize = 16.sp)
                }
            }
        }
    }
}