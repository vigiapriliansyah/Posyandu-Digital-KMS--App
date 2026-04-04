package com.skripsi.posyandudigital.ui.pengukuran

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skripsi.posyandudigital.data.local.AppDatabase
import com.skripsi.posyandudigital.data.remote.api.RetrofitClient
import com.skripsi.posyandudigital.data.remote.dto.CreatePengukuranRequest
import com.skripsi.posyandudigital.data.remote.dto.PengukuranDetailDto
import com.skripsi.posyandudigital.data.repository.PengukuranRepository
import com.skripsi.posyandudigital.data.session.SessionManager
import com.skripsi.posyandudigital.ui.theme.HealthyGreen
import com.skripsi.posyandudigital.ui.theme.PrimaryBlue
import com.skripsi.posyandudigital.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ====================================================================
// DATA STANDAR WHO FULL (BULAN 0 - 24)
// ====================================================================
val whoBoys = listOf(
    listOf(0f, 2.1f, 2.5f, 3.3f, 3.9f, 4.4f), listOf(1f, 2.9f, 3.4f, 4.5f, 5.1f, 5.8f), listOf(2f, 3.8f, 4.3f, 5.6f, 6.3f, 7.1f),
    listOf(3f, 4.4f, 5.0f, 6.4f, 7.2f, 8.0f), listOf(4f, 4.9f, 5.6f, 7.0f, 7.8f, 8.7f), listOf(5f, 5.3f, 6.0f, 7.5f, 8.4f, 9.3f),
    listOf(6f, 5.7f, 6.4f, 7.9f, 8.8f, 9.8f), listOf(7f, 6.0f, 6.7f, 8.3f, 9.2f, 10.3f), listOf(8f, 6.3f, 7.1f, 8.6f, 9.6f, 10.7f),
    listOf(9f, 6.5f, 7.3f, 8.9f, 9.9f, 11.0f), listOf(10f, 6.6f, 7.4f, 9.2f, 10.2f, 11.4f), listOf(11f, 6.8f, 7.6f, 9.4f, 10.5f, 11.7f),
    listOf(12f, 6.9f, 7.7f, 9.6f, 10.8f, 12.0f), listOf(13f, 7.1f, 7.9f, 9.9f, 11.0f, 12.3f), listOf(14f, 7.2f, 8.1f, 10.1f, 11.3f, 12.6f),
    listOf(15f, 7.4f, 8.3f, 10.3f, 11.5f, 12.8f), listOf(16f, 7.5f, 8.4f, 10.5f, 11.7f, 13.1f), listOf(17f, 7.7f, 8.6f, 10.7f, 12.0f, 13.4f),
    listOf(18f, 7.7f, 8.8f, 10.9f, 12.2f, 13.7f), listOf(19f, 8.0f, 8.9f, 11.1f, 12.5f, 13.9f), listOf(20f, 8.1f, 9.1f, 11.3f, 12.7f, 14.2f),
    listOf(21f, 8.2f, 9.2f, 11.5f, 12.9f, 14.5f), listOf(22f, 8.4f, 9.4f, 11.8f, 13.2f, 14.7f), listOf(23f, 8.5f, 9.5f, 12.0f, 13.4f, 15.0f),
    listOf(24f, 8.6f, 9.7f, 12.2f, 13.6f, 15.3f)
)

val whoGirls = listOf(
    listOf(0f, 2.0f, 2.4f, 3.2f, 3.7f, 4.2f), listOf(1f, 2.7f, 3.2f, 4.2f, 4.8f, 5.5f), listOf(2f, 3.4f, 3.9f, 5.1f, 5.8f, 6.6f),
    listOf(3f, 4.0f, 4.5f, 5.8f, 6.6f, 7.5f), listOf(4f, 4.4f, 5.0f, 6.4f, 7.3f, 8.2f), listOf(5f, 4.8f, 5.4f, 6.9f, 7.8f, 8.8f),
    listOf(6f, 5.1f, 5.7f, 7.3f, 8.2f, 9.3f), listOf(7f, 5.3f, 6.0f, 7.6f, 8.6f, 9.8f), listOf(8f, 5.6f, 6.2f, 8.0f, 9.0f, 10.2f),
    listOf(9f, 5.8f, 6.5f, 8.2f, 9.3f, 10.5f), listOf(10f, 6.0f, 6.6f, 8.5f, 9.6f, 10.9f), listOf(11f, 6.1f, 6.8f, 8.7f, 9.9f, 11.2f),
    listOf(12f, 6.3f, 7.0f, 8.9f, 10.1f, 11.5f), listOf(13f, 6.4f, 7.2f, 9.2f, 10.4f, 11.7f), listOf(14f, 6.6f, 7.4f, 9.4f, 10.6f, 12.0f),
    listOf(15f, 6.7f, 7.6f, 9.6f, 10.9f, 12.2f), listOf(16f, 6.9f, 7.7f, 9.8f, 11.1f, 12.5f), listOf(17f, 7.0f, 7.9f, 10.0f, 11.4f, 12.8f),
    listOf(18f, 7.0f, 8.1f, 10.2f, 11.6f, 13.2f), listOf(19f, 7.3f, 8.2f, 10.4f, 11.8f, 13.5f), listOf(20f, 7.4f, 8.4f, 10.6f, 12.1f, 13.7f),
    listOf(21f, 7.6f, 8.6f, 10.9f, 12.4f, 14.0f), listOf(22f, 7.7f, 8.7f, 11.1f, 12.6f, 14.3f), listOf(23f, 7.8f, 8.9f, 11.3f, 12.8f, 14.6f),
    listOf(24f, 7.9f, 9.0f, 11.5f, 13.0f, 14.8f)
)

fun hitungGiziLokal(jenisKelamin: String, umurBulan: Int, beratBadan: Double): String {
    val dataBaku = if (jenisKelamin == "P") whoGirls else whoBoys
    val refData = dataBaku.find { it[0].toInt() == umurBulan } ?: dataBaku.last()

    val sd3neg = refData[1]
    val sd2neg = refData[2]
    val sd2pos = refData[4]

    return when {
        beratBadan < sd3neg -> "Gizi Buruk"
        beratBadan >= sd3neg && beratBadan < sd2neg -> "Gizi Kurang"
        beratBadan >= sd2neg && beratBadan <= sd2pos -> "Gizi Baik (Normal)"
        else -> "Risiko Gizi Lebih"
    }
}

// ====================================================================
// 1. LAYAR RIWAYAT KMS
// ====================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAnakScreen(
    anakId: Int,
    namaAnak: String,
    umurBulan: Int,
    jenisKelamin: String,
    onNavigateBack: () -> Unit,
    onNavigateToInput: (Int, String, Int, String) -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val userRole by sessionManager.getRole().collectAsState(initial = "")
    val isKader = userRole?.lowercase() == "kader"

    val factory = remember {
        val db = AppDatabase.getDatabase(context)
        PengukuranViewModelFactory(
            PengukuranRepository(RetrofitClient.instance, sessionManager, db.kmsDao(), context)
        )
    }
    val viewModel: PengukuranViewModel = viewModel(factory = factory)
    val state = viewModel.state.value

    val lifecycleOwner = LocalLifecycleOwner.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissMessage()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadRiwayat(anakId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(namaAnak, fontWeight = FontWeight.Bold)
                        Text("Riwayat KMS", style = MaterialTheme.typography.labelMedium)
                    }
                },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Kembali") } }
            )
        },
        floatingActionButton = {
            if (isKader) {
                ExtendedFloatingActionButton(
                    onClick = { onNavigateToInput(anakId, namaAnak, umurBulan, jenisKelamin) },
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Pencatatan KMS")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF5F5F5))) {

            // --- PERBAIKAN UI OFFLINE FIRST ---
            // Layar akan selalu memuat daftar (atau layar kosong) tanpa di-block oleh loading berputar
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    val jkText = if (jenisKelamin == "L") "Laki-laki (Biru)" else "Perempuan (Pink)"
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Grafik Pertumbuhan KMS", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            Text(jkText, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth().height(260.dp)
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)).padding(4.dp)
                            ) {
                                KmsChart(riwayatList = state.riwayatList, jenisKelamin = jenisKelamin)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Riwayat Penimbangan:", fontWeight = FontWeight.Bold)
                }

                if (state.riwayatList.isEmpty()) {
                    item {
                        Text("Belum ada riwayat KMS yang tersimpan di perangkat ini.", color = TextSecondary)
                    }
                } else {
                    items(state.riwayatList) { item ->
                        RiwayatItem(item = item, jenisKelamin = jenisKelamin)
                    }
                }
            }

            // --- LOADING BAR PINDAH KE ATAS ---
            // Saat offline/loading lambat, indikator hanya muncul kecil di atas layar,
            // sehingga pengguna masih bisa melihat grafik dan riwayat lama
            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                    color = PrimaryBlue
                )
            }
        }
    }
}

@Composable
fun RiwayatItem(item: PengukuranDetailDto, jenisKelamin: String) {
    val statusGiziDisplay = if (item.id < 0) {
        "Menunggu Sinkronisasi"
    } else {
        hitungGiziLokal(jenisKelamin, item.umurBulan, item.beratBadan)
    }

    val colorDisplay = when {
        statusGiziDisplay.contains("Baik", true) -> HealthyGreen
        statusGiziDisplay.contains("Kurang", true) || statusGiziDisplay.contains("Lebih", true) -> Color(0xFFFBC02D)
        statusGiziDisplay.contains("Buruk", true) -> Color.Red
        else -> Color.Gray
    }

    Card(elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(item.tanggalPencatatan ?: "-", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                Text("${item.umurBulan} Bulan", style = MaterialTheme.typography.bodyMedium)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Berat Badan", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text("${item.beratBadan} kg", fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("KBM", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text("${item.kbm ?: "-"} gr", fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("N/T", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    val statusText = when(item.statusNaikTurun) { "N" -> "Naik"; "T" -> "Tidak Naik"; "B" -> "Baru"; else -> "-" }
                    Text(statusText, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Gizi", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(statusGiziDisplay, fontWeight = FontWeight.Bold, color = colorDisplay)
                }
            }

            if (item.id < 0) {
                Text(
                    text = "⚠️ Disimpan Offline. Akan otomatis disinkronkan saat ada internet.",
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 8.dp).background(Color(0xFFFFF9C4), RoundedCornerShape(4.dp)).padding(4.dp).fillMaxWidth()
                )
            }
        }
    }
}

// ====================================================================
// 2. LAYAR INPUT KMS
// ====================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputPengukuranScreen(
    anakId: Int,
    namaAnak: String,
    umurBulan: Int,
    jenisKelamin: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val factory = remember {
        val db = AppDatabase.getDatabase(context)
        PengukuranViewModelFactory(
            PengukuranRepository(RetrofitClient.instance, sessionManager, db.kmsDao(), context)
        )
    }
    val viewModel: PengukuranViewModel = viewModel(factory = factory)
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }

    var inputUmurBulan by remember { mutableStateOf(umurBulan.toString()) }
    var beratBadan by remember { mutableStateOf("") }
    var kbm by remember { mutableStateOf("") }
    var statusNT by remember { mutableStateOf("") }
    var asiEksklusif by remember { mutableStateOf<Boolean?>(null) }

    val tglHariIni = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    LaunchedEffect(anakId) {
        viewModel.loadRiwayat(anakId)
    }

    LaunchedEffect(state.riwayatList) {
        if (state.riwayatList.isNotEmpty()) {
            val umurTerakhir = state.riwayatList.maxOf { it.umurBulan }
            inputUmurBulan = (umurTerakhir + 1).toString()
        }
    }

    LaunchedEffect(state.successMessage, state.error) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissMessage()
            kotlinx.coroutines.delay(500)
            onNavigateBack()
        }
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissMessage()
        }
    }

    val bbDouble = beratBadan.replace(",", ".").toDoubleOrNull()
    val umurInt = inputUmurBulan.toIntOrNull()

    var liveGiziText = "Ketik Umur & BB untuk melihat Gizi"
    var liveGiziColor = TextSecondary

    if (bbDouble != null && umurInt != null) {
        liveGiziText = hitungGiziLokal(jenisKelamin, umurInt, bbDouble)
        liveGiziColor = when(liveGiziText) {
            "Gizi Buruk" -> Color.Red
            "Gizi Kurang", "Risiko Gizi Lebih" -> Color(0xFFFBC02D)
            else -> HealthyGreen
        }
    }

    val liveRiwayat = state.riwayatList.toMutableList()
    if (bbDouble != null && umurInt != null) {
        liveRiwayat.add(
            PengukuranDetailDto(
                id = -1,
                tanggalPencatatan = tglHariIni,
                umurBulan = umurInt,
                beratBadan = bbDouble,
                tinggiBadan = null, kbm = null, statusNaikTurun = null, asiEksklusif = null, statusGiziRaw = null, catatan = null
            )
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Pencatatan KMS") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Kembali") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Grafik Berat Badan", fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                // Selalu tampilkan grafik walaupun belum ada data/sedang loading
                KmsChart(riwayatList = liveRiwayat, jenisKelamin = jenisKelamin)

                // Tambahkan progress bar kecil di atas jika sedang sinkronisasi
                if (state.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = kbm, onValueChange = { kbm = it },
                label = { Text("KBM (Kosongkan agar dihitung sistem)") },
                modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
            )
            OutlinedTextField(
                value = statusNT, onValueChange = { statusNT = it },
                label = { Text("N/T (Kosongkan agar dihitung sistem)") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            OutlinedTextField(
                value = liveGiziText, onValueChange = { },
                label = { Text("Status Gizi (Live)") },
                modifier = Modifier.fillMaxWidth(), readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = liveGiziColor.copy(alpha = 0.1f),
                    unfocusedTextColor = liveGiziColor,
                    focusedTextColor = liveGiziColor
                )
            )

            OutlinedTextField(
                value = namaAnak, onValueChange = { },
                label = { Text("Nama Lengkap Anak") },
                modifier = Modifier.fillMaxWidth(), readOnly = true, colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF5F5F5))
            )

            OutlinedTextField(
                value = inputUmurBulan, onValueChange = { inputUmurBulan = it },
                label = { Text("Umur (Bulan ke-)") },
                modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
            )
            OutlinedTextField(
                value = beratBadan, onValueChange = { beratBadan = it },
                label = { Text("Berat Badan (Kg)") },
                modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true
            )

            Text("ASI Eksklusif", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = asiEksklusif == true, onClick = { asiEksklusif = true }, label = { Text("Ya") })
                FilterChip(selected = asiEksklusif == false, onClick = { asiEksklusif = false }, label = { Text("Tidak") })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (bbDouble != null && umurInt != null) {
                        viewModel.simpanPengukuran(
                            CreatePengukuranRequest(
                                anakId = anakId,
                                umurBulan = umurInt,
                                beratBadan = bbDouble,
                                tinggiBadan = null,
                                kbm = kbm.toIntOrNull(),
                                statusNaikTurun = statusNT.ifBlank { null },
                                asiEksklusif = asiEksklusif,
                                tanggalPencatatan = tglHariIni,
                                catatan = null
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                // --- PERBAIKAN OFFLINE ---
                // Dulu kita pakai: enabled = bbDouble != null && umurInt != null && !state.isLoading
                // Akibatnya, tombol terkunci selama 30 detik saat offline.
                // Sekarang tombol "Simpan KMS" selalu bisa diklik meskipun sedang offline!
                enabled = bbDouble != null && umurInt != null
            ) {
                Text("Simpan KMS", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ====================================================================
// 3. KOMPONEN CANVAS: GRAFIK KMS (VERSI PRESISI SEMUA ANGKA MUNCUL)
// ====================================================================
@Composable
fun KmsChart(
    riwayatList: List<PengukuranDetailDto>,
    jenisKelamin: String,
    modifier: Modifier = Modifier
) {
    val dataBaku = if (jenisKelamin == "P") whoGirls else whoBoys
    val sortedRiwayat = riwayatList.sortedBy { it.umurBulan }

    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Bold)

    val maxBulan = 24f
    val maxBerat = 18f

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val startPadding = 32.dp.toPx()
        val bottomPadding = 28.dp.toPx()
        val topPadding = 16.dp.toPx()
        val endPadding = 16.dp.toPx()

        val w = size.width - startPadding - endPadding
        val h = size.height - topPadding - bottomPadding

        if (w <= 0 || h <= 0) return@Canvas

        fun xPos(umur: Float): Float = startPadding + ((umur / maxBulan) * w)
        fun yPos(berat: Float): Float = topPadding + (h - ((berat / maxBerat) * h))

        // --- 1. MENGGAMBAR ZONA WARNA WHO ---
        val pathKuningAtas = Path().apply {
            moveTo(xPos(dataBaku.first()[0]), yPos(dataBaku.first()[5]))
            dataBaku.forEach { lineTo(xPos(it[0]), yPos(it[5])) }
            dataBaku.reversed().forEach { lineTo(xPos(it[0]), yPos(it[4])) }
            close()
        }
        drawPath(pathKuningAtas, color = Color(0xFFFFF59D))

        val pathHijau = Path().apply {
            moveTo(xPos(dataBaku.first()[0]), yPos(dataBaku.first()[4]))
            dataBaku.forEach { lineTo(xPos(it[0]), yPos(it[4])) }
            dataBaku.reversed().forEach { lineTo(xPos(it[0]), yPos(it[2])) }
            close()
        }
        drawPath(pathHijau, color = Color(0xFFA5D6A7))

        val pathKuningBawah = Path().apply {
            moveTo(xPos(dataBaku.first()[0]), yPos(dataBaku.first()[2]))
            dataBaku.forEach { lineTo(xPos(it[0]), yPos(it[2])) }
            dataBaku.reversed().forEach { lineTo(xPos(it[0]), yPos(it[1])) }
            close()
        }
        drawPath(pathKuningBawah, color = Color(0xFFFFF59D))

        val pathMerahZona = Path().apply {
            moveTo(xPos(dataBaku.first()[0]), yPos(dataBaku.first()[1]))
            dataBaku.forEach { lineTo(xPos(it[0]), yPos(it[1])) }
            lineTo(xPos(dataBaku.last()[0]), yPos(0f))
            lineTo(xPos(dataBaku.first()[0]), yPos(0f))
            close()
        }
        drawPath(pathMerahZona, color = Color(0xFFFFCDD2))

        val pathGarisMerah = Path().apply {
            moveTo(xPos(dataBaku.first()[0]), yPos(dataBaku.first()[1]))
            dataBaku.forEach { lineTo(xPos(it[0]), yPos(it[1])) }
        }
        drawPath(pathGarisMerah, color = Color.Red, style = Stroke(width = 3f))

        // --- 2. MENGGAMBAR GRID PRESISI DAN ANGKA INDIKATOR ---

        for (i in 0..maxBerat.toInt()) {
            val y = Math.round(yPos(i.toFloat())).toFloat()

            drawLine(
                color = Color.Black.copy(alpha = 0.4f),
                start = Offset(startPadding, y),
                end = Offset(startPadding + w, y),
                strokeWidth = if (i % 5 == 0) 2.5f else 1.5f
            )

            val textLayout = textMeasurer.measure(text = "$i", style = textStyle)
            drawText(
                textLayoutResult = textLayout,
                topLeft = Offset(
                    x = startPadding - textLayout.size.width - 6.dp.toPx(),
                    y = y - (textLayout.size.height / 2f)
                )
            )
        }

        for (i in 0..maxBulan.toInt()) {
            val x = Math.round(xPos(i.toFloat())).toFloat()

            drawLine(
                color = Color.Black.copy(alpha = 0.4f),
                start = Offset(x, topPadding),
                end = Offset(x, topPadding + h),
                strokeWidth = 1.5f
            )

            val textLayout = textMeasurer.measure(text = "$i", style = textStyle)
            drawText(
                textLayoutResult = textLayout,
                topLeft = Offset(
                    x = x - (textLayout.size.width / 2f),
                    y = topPadding + h + 6.dp.toPx()
                )
            )
        }

        drawLine(Color.Black, Offset(startPadding, topPadding), Offset(startPadding, topPadding + h), strokeWidth = 2f)
        drawLine(Color.Black, Offset(startPadding, topPadding + h), Offset(startPadding + w, topPadding + h), strokeWidth = 2f)

        // --- 3. MENGGAMBAR TITIK DAN GARIS RIWAYAT ANAK ---
        if (sortedRiwayat.isNotEmpty()) {
            val validRiwayat = sortedRiwayat.filter { it.umurBulan <= maxBulan.toInt() }

            if (validRiwayat.isNotEmpty()) {
                val pathData = Path()

                validRiwayat.forEachIndexed { index, data ->
                    val x = xPos(data.umurBulan.toFloat())
                    val y = yPos(data.beratBadan.toFloat())

                    if (index == 0) {
                        pathData.moveTo(x, y)
                    } else {
                        pathData.lineTo(x, y)
                    }

                    if (data.id == -1) {
                        drawCircle(color = Color.Blue.copy(alpha = 0.4f), radius = 14f, center = Offset(x, y))
                        drawCircle(color = Color.Blue, radius = 6f, center = Offset(x, y))
                    } else {
                        drawCircle(color = Color.Black, radius = 6f, center = Offset(x, y))
                    }
                }

                if (validRiwayat.size > 1) {
                    drawPath(pathData, color = Color.Black, style = Stroke(width = 4f, cap = StrokeCap.Round))
                }
            }
        }
    }
}