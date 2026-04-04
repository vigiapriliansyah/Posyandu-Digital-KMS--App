package com.skripsi.posyandudigital.ui.anak

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skripsi.posyandudigital.data.local.AppDatabase
import com.skripsi.posyandudigital.data.remote.api.RetrofitClient
import com.skripsi.posyandudigital.data.remote.dto.AnakDetailDto
import com.skripsi.posyandudigital.data.remote.dto.CreateAnakRequest
import com.skripsi.posyandudigital.data.remote.dto.OrangTuaSimpleDto
import com.skripsi.posyandudigital.data.repository.AnakRepository
import com.skripsi.posyandudigital.data.session.SessionManager
import com.skripsi.posyandudigital.ui.theme.PrimaryBlue
import com.skripsi.posyandudigital.ui.theme.TextSecondary
import com.skripsi.posyandudigital.ui.usermanagement.SearchableDropdown
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarAnakScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTambah: () -> Unit,
    onNavigateToDetail: (Int, String, Int, String) -> Unit
) {
    val context = LocalContext.current

    val factory = remember {
        val db = AppDatabase.getDatabase(context)
        AnakViewModelFactory(AnakRepository(RetrofitClient.instance, SessionManager(context), db.anakDao()))
    }
    val viewModel: AnakViewModel = viewModel(factory = factory)
    val state = viewModel.state.value

    // --- STATE UNTUK FILTER ---
    var selectedFilter by remember { mutableStateOf("Semua") }
    val filterOptions = listOf("Semua", "Gizi Baik", "Gizi Kurang", "Gizi Buruk", "Risiko Gizi Lebih")

    LaunchedEffect(Unit) {
        viewModel.loadAnakList()
    }

    // --- LOGIKA FILTERING ---
    val filteredList = if (selectedFilter == "Semua") {
        state.anakList
    } else {
        state.anakList.filter { anak ->
            // Pastikan properti ini ada di AnakDetailDto Anda
            val status = anak.statusGiziTerakhir ?: ""
            status.contains(selectedFilter, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Balita") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Kembali") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToTambah, containerColor = PrimaryBlue) {
                Icon(Icons.Default.Add, "Tambah Balita", tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // --- KOMPONEN FILTER BAR (CHIPS) ---
            LazyRow(
                modifier = Modifier.fillMaxWidth().background(Color.White),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))

            // --- KONTEN DAFTAR BALITA ---
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (state.isLoading && state.anakList.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (filteredList.isEmpty()) {
                    val msg = if (state.anakList.isEmpty()) "Belum ada data balita." else "Tidak ada balita dengan status $selectedFilter."
                    Text(msg, modifier = Modifier.align(Alignment.Center), color = TextSecondary)
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredList) { anak ->
                            AnakItemCard(
                                anak = anak,
                                onClick = { onNavigateToDetail(anak.id, anak.namaAnak, anak.umurBulan ?: 0, anak.jenisKelamin) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnakItemCard(anak: AnakDetailDto, onClick: () -> Unit) {
    // --- PENENTUAN WARNA STATUS GIZI ---
    val statusGizi = anak.statusGiziTerakhir ?: "Belum ada data KMS"
    val colorStatus = when {
        statusGizi.contains("Baik", true) -> Color(0xFF4CAF50) // Hijau
        statusGizi.contains("Kurang", true) -> Color(0xFFFBC02D) // Kuning
        statusGizi.contains("Buruk", true) -> Color.Red // Merah
        statusGizi.contains("Lebih", true) -> Color(0xFFFF9800) // Orange
        else -> Color.Gray
    }

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            val icon = if (anak.jenisKelamin == "L") Icons.Default.Male else Icons.Default.Female
            val color = if (anak.jenisKelamin == "L") Color.Blue else Color(0xFFE91E63)

            Icon(icon, null, tint = color, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(anak.namaAnak, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Ibu: ${anak.orangTua?.namaIbu ?: "-"}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Text("Umur: ${anak.umurBulan ?: 0} Bulan", style = MaterialTheme.typography.bodySmall, color = TextSecondary)

                // Tampilan Status Gizi (Berwarna)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = statusGizi,
                    style = MaterialTheme.typography.labelMedium,
                    color = colorStatus,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(colorStatus.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahAnakScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userRoleRaw by sessionManager.getRole().collectAsState(initial = "")
    val userRole = userRoleRaw?.lowercase() ?: ""
    val isKaderOrAdmin = userRole == "kader" || userRole == "admin"
    val isOrangTua = userRole == "orangtua"

    val factory = remember {
        val db = AppDatabase.getDatabase(context)
        AnakViewModelFactory(AnakRepository(RetrofitClient.instance, sessionManager, db.anakDao()))
    }
    val viewModel: AnakViewModel = viewModel(factory = factory)
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }

    var namaAnak by remember { mutableStateOf("") }
    var nikAnak by remember { mutableStateOf("") }
    var tempatLahir by remember { mutableStateOf("") }
    var tanggalLahir by remember { mutableStateOf("") }
    var jenisKelamin by remember { mutableStateOf("L") }
    var beratLahir by remember { mutableStateOf("") }
    var tinggiLahir by remember { mutableStateOf("") }
    var selectedOrangTua by remember { mutableStateOf<OrangTuaSimpleDto?>(null) }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedMonth = String.format("%02d", month + 1)
            val formattedDay = String.format("%02d", dayOfMonth)
            tanggalLahir = "$year-$formattedMonth-$formattedDay"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(isKaderOrAdmin) {
        if (isKaderOrAdmin) viewModel.loadOrangTuaList()
    }

    LaunchedEffect(state.successMessage, state.error) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissMessage()
            kotlinx.coroutines.delay(1000)
            onNavigateBack()
        }
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Tambah Data Balita") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Kembali") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Informasi Dasar Anak", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = namaAnak, onValueChange = { namaAnak = it }, label = { Text("Nama Lengkap Anak") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = nikAnak, onValueChange = { nikAnak = it }, label = { Text("NIK Anak (Opsional)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = tempatLahir, onValueChange = { tempatLahir = it }, label = { Text("Tempat Lahir") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = tanggalLahir, onValueChange = {}, label = { Text("Tanggal Lahir (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() }, readOnly = true, enabled = false, colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant))
            Text("Jenis Kelamin", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = jenisKelamin == "L", onClick = { jenisKelamin = "L" }, label = { Text("Laki-Laki") })
                FilterChip(selected = jenisKelamin == "P", onClick = { jenisKelamin = "P" }, label = { Text("Perempuan") })
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Data Saat Lahir (Opsional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = beratLahir, onValueChange = { beratLahir = it }, label = { Text("Berat (kg)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(value = tinggiLahir, onValueChange = { tinggiLahir = it }, label = { Text("Tinggi (cm)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            }
            if (isKaderOrAdmin) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Hubungkan dengan Orang Tua", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                SearchableDropdown(label = "Pilih Ibu Kandung", options = state.orangTuaList, selectedOption = selectedOrangTua, optionToString = { it.namaIbu }, onOptionSelected = { selectedOrangTua = it })
            } else if (isOrangTua) {
                Card(colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.1f)), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = "Info", tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Data balita ini akan otomatis ditambahkan dan dihubungkan ke profil akun Anda.", style = MaterialTheme.typography.bodySmall, color = PrimaryBlue)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            val isFormValid = namaAnak.isNotBlank() && tanggalLahir.isNotBlank() && (!isKaderOrAdmin || selectedOrangTua != null)
            Button(
                onClick = {
                    val request = CreateAnakRequest(namaAnak = namaAnak, nikAnak = nikAnak, tempatLahir = tempatLahir, tanggalLahir = tanggalLahir, jenisKelamin = jenisKelamin, beratLahir = beratLahir.toDoubleOrNull() ?: 0.0, tinggiLahir = tinggiLahir.toDoubleOrNull() ?: 0.0, orangTuaId = selectedOrangTua?.id ?: 0)
                    viewModel.createAnak(request)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp), enabled = !state.isLoading && isFormValid, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (state.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Simpan Data Anak", fontSize = 16.sp)
            }
        }
    }
}