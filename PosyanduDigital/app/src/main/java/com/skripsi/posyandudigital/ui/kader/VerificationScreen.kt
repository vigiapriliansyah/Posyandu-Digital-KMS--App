package com.skripsi.posyandudigital.ui.kader

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skripsi.posyandudigital.data.remote.api.RetrofitClient
import com.skripsi.posyandudigital.data.remote.dto.PendingOrangTuaDto
import com.skripsi.posyandudigital.data.repository.UserManagementRepositoryImpl
import com.skripsi.posyandudigital.data.session.SessionManager
import com.skripsi.posyandudigital.ui.theme.CriticalRed
import com.skripsi.posyandudigital.ui.theme.HealthyGreen
import com.skripsi.posyandudigital.ui.theme.PrimaryBlue
import com.skripsi.posyandudigital.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // PERBAIKAN DI SINI: Factory inline yang otomatis memasukkan Repository
    val factory = remember {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = UserManagementRepositoryImpl(
                    apiService = RetrofitClient.instance,
                    sessionManager = SessionManager(context)
                )
                return VerificationViewModel(repository) as T
            }
        }
    }

    val viewModel: VerificationViewModel = viewModel(factory = factory)
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }

    // Munculkan notifikasi jika sukses/error
    LaunchedEffect(state.successMessage, state.error) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Daftar Tunggu Verifikasi") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Kembali") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF5F5F5))) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.pendingList.isEmpty()) {
                Text(
                    "Tidak ada antrean pendaftaran orang tua.",
                    modifier = Modifier.align(Alignment.Center),
                    color = TextSecondary
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.pendingList) { pendingUser ->
                        PendingUserCard(
                            user = pendingUser,
                            // PERBAIKAN: Lempar kode inputan ke viewModel.verifyByCode
                            onVerify = { kodeInput -> viewModel.verifyByCode(kodeInput) },
                            onReject = { viewModel.rejectUser(pendingUser.user.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PendingUserCard(
    user: PendingOrangTuaDto,
    onVerify: (String) -> Unit, // PERUBAHAN: Menerima String (Kode)
    onReject: () -> Unit
) {
    var showDialogTolak by remember { mutableStateOf(false) }
    var showDialogSetuju by remember { mutableStateOf(false) }
    var inputKode by remember { mutableStateOf("") }

    // Dialog Konfirmasi Penolakan
    if (showDialogTolak) {
        AlertDialog(
            onDismissRequest = { showDialogTolak = false },
            title = { Text("Tolak Pendaftaran?") },
            text = { Text("Anda yakin ingin menolak dan menghapus pendaftaran atas nama Ibu ${user.namaIbu}? Data tidak dapat dikembalikan.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialogTolak = false
                    onReject()
                }) { Text("Ya, Tolak", color = CriticalRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDialogTolak = false }) { Text("Batal") }
            }
        )
    }

    // Dialog Input Kode Verifikasi
    if (showDialogSetuju) {
        AlertDialog(
            onDismissRequest = {
                showDialogSetuju = false
                inputKode = ""
            },
            title = { Text("Verifikasi Kode", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Minta 6 digit kode verifikasi yang ada di layar aplikasi Ibu ${user.namaIbu} dan masukkan di bawah ini:")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = inputKode,
                        onValueChange = { inputKode = it },
                        label = { Text("Kode Verifikasi") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialogSetuju = false
                        onVerify(inputKode)
                        inputKode = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthyGreen),
                    enabled = inputKode.isNotBlank()
                ) { Text("Verifikasi") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialogSetuju = false
                    inputKode = ""
                }) { Text("Batal") }
            }
        )
    }

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ibu ${user.namaIbu}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("No HP: ${user.noHp ?: "-"}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Text("Alamat: ${user.alamat ?: "-"}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Aksi
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(
                    onClick = { showDialogTolak = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CriticalRed),
                    border = BorderStroke(1.dp, CriticalRed)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tolak")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showDialogSetuju = true },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthyGreen)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Setujui")
                }
            }
        }
    }
}