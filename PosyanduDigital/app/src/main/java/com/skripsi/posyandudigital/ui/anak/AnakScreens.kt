package com.skripsi.posyandudigital.ui.anak

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

// --- SCREEN 1: DAFTAR ANAK ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarAnakScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTambah: () -> Unit
) {
    val context = LocalContext.current
    // Inisialisasi ViewModel dengan Factory
    val factory = remember {
        AnakViewModelFactory(AnakRepository(RetrofitClient.instance, SessionManager(context)))
    }
    val viewModel: AnakViewModel = viewModel(factory = factory)
    val state = viewModel.state.value

    // Load data saat pertama kali dibuka
    LaunchedEffect(Unit) {
        viewModel.loadAnakList()
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
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.anakList.isEmpty()) {
                Text(
                    "Belum ada data balita.",
                    modifier = Modifier.align(Alignment.Center),
                    color = TextSecondary
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.anakList) { anak ->
                        AnakItemCard(anak)
                    }
                }
            }
        }
    }
}

@Composable
fun AnakItemCard(anak: AnakDetailDto) {
    Card(elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            // Icon Gender (Biru = Laki-laki, Pink = Perempuan)
            val icon = if (anak.jenisKelamin == "L") Icons.Default.Male else Icons.Default.Female
            val color = if (anak.jenisKelamin == "L") Color.Blue else Color(0xFFE91E63) // Pink

            Icon(icon, null, tint = color, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(anak.namaAnak, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Ibu: ${anak.orangTua?.namaIbu ?: "-"}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Text("Tgl Lahir: ${anak.tanggalLahir}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

// --- SCREEN 2: TAMBAH ANAK ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahAnakScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val factory = remember {
        AnakViewModelFactory(AnakRepository(RetrofitClient.instance, SessionManager(context)))
    }
    val viewModel: AnakViewModel = viewModel(factory = factory)
    val state = viewModel.state.value

    // State Form
    var namaAnak by remember { mutableStateOf("") }
    var nikAnak by remember { mutableStateOf("") }
    var tempatLahir by remember { mutableStateOf("") }
    var tanggalLahir by remember { mutableStateOf("") } // YYYY-MM-DD
    var jenisKelamin by remember { mutableStateOf("L") }
    var beratLahir by remember { mutableStateOf("") }
    var tinggiLahir by remember { mutableStateOf("") }
    var selectedOrangTua by remember { mutableStateOf<OrangTuaSimpleDto?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Date Picker Logic
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val formattedMonth = (month + 1).toString().padStart(2, '0')
            val formattedDay = dayOfMonth.toString().padStart(2, '0')
            tanggalLahir = "$year-$formattedMonth-$formattedDay"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(Unit) {
        viewModel.loadOrangTuaList()
    }

    LaunchedEffect(state.successMessage, state.error) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissMessage()
            onNavigateBack() // Kembali setelah sukses
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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Bagian 1: Data Anak
            OutlinedTextField(value = namaAnak, onValueChange = { namaAnak = it }, label = { Text("Nama Lengkap Anak") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = nikAnak, onValueChange = { nikAnak = it }, label = { Text("NIK Anak (Opsional)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = tempatLahir, onValueChange = { tempatLahir = it }, label = { Text("Tempat Lahir") }, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = tanggalLahir,
                    onValueChange = {},
                    label = { Text("Tgl Lahir") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) { Icon(Icons.Default.CalendarToday, null) }
                    },
                    modifier = Modifier.weight(1f).clickable { datePickerDialog.show() }
                )
            }

            Text("Jenis Kelamin", fontWeight = FontWeight.Bold)
            Row {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = jenisKelamin == "L", onClick = { jenisKelamin = "L" })
                    Text("Laki-laki")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = jenisKelamin == "P", onClick = { jenisKelamin = "P" })
                    Text("Perempuan")
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = beratLahir, onValueChange = { beratLahir = it }, label = { Text("Berat Lahir (Kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(value = tinggiLahir, onValueChange = { tinggiLahir = it }, label = { Text("Tinggi Lahir (Cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
            }

            Divider()

            // Bagian 2: Pilih Orang Tua
            Text("Data Orang Tua", fontWeight = FontWeight.Bold, color = PrimaryBlue)
            SearchableDropdown(
                label = "Cari Ibu Kandung",
                options = state.orangTuaList,
                selectedOption = selectedOrangTua,
                optionToString = { it.namaIbu },
                onOptionSelected = { selectedOrangTua = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (namaAnak.isNotBlank() && tanggalLahir.isNotBlank() && selectedOrangTua != null) {
                        viewModel.createAnak(
                            CreateAnakRequest(
                                namaAnak = namaAnak,
                                nikAnak = nikAnak,
                                tempatLahir = tempatLahir,
                                tanggalLahir = tanggalLahir,
                                jenisKelamin = jenisKelamin,
                                beratLahir = beratLahir.toDoubleOrNull() ?: 0.0,
                                tinggiLahir = tinggiLahir.toDoubleOrNull() ?: 0.0,
                                orangTuaId = selectedOrangTua!!.id
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading && selectedOrangTua != null
            ) {
                if (state.isLoading) CircularProgressIndicator(color = Color.White)
                else Text("Simpan Data Anak")
            }
        }
    }
}