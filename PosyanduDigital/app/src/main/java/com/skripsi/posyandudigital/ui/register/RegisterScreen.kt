package com.skripsi.posyandudigital.ui.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skripsi.posyandudigital.ui.usermanagement.SearchableDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToVerificationCode: (String) -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.success, state.error) {
        if (state.success) {
            // Logika Navigasi: Prioritaskan layar kode
            if (!state.registeredVerificationCode.isNullOrBlank()) {
                onNavigateToVerificationCode(state.registeredVerificationCode)
            } else {
                // Fallback jika kode null (seharusnya tidak terjadi untuk orang tua)
                snackbarHostState.showSnackbar("Registrasi Berhasil! Silakan Login.")
                // Beri jeda sedikit sebelum kembali agar snackbar terbaca
                kotlinx.coroutines.delay(1500)
                onNavigateBack()
            }
            viewModel.resetState()
        }
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Daftar Akun Orang Tua") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Isi data diri untuk mendaftar:", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = viewModel.namaIbu.value,
                onValueChange = { viewModel.namaIbu.value = it },
                label = { Text("Nama Lengkap Ibu") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.username.value,
                onValueChange = { viewModel.username.value = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.password.value,
                onValueChange = { viewModel.password.value = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.noHp.value,
                onValueChange = { viewModel.noHp.value = it },
                label = { Text("No HP / WhatsApp") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.alamat.value,
                onValueChange = { viewModel.alamat.value = it },
                label = { Text("Alamat Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Divider()
            Text("Pilih Lokasi Posyandu:", style = MaterialTheme.typography.titleSmall)

            SearchableDropdown(
                label = "Pilih Kecamatan",
                options = state.kecamatanList,
                selectedOption = viewModel.selectedKecamatan.value,
                optionToString = { it.namaKecamatan },
                onOptionSelected = { viewModel.onKecamatanSelected(it) }
            )

            SearchableDropdown(
                label = "Pilih Desa",
                options = state.desaList,
                selectedOption = viewModel.selectedDesa.value,
                optionToString = { it.namaDesa },
                onOptionSelected = { viewModel.onDesaSelected(it) },
                enabled = viewModel.selectedKecamatan.value != null
            )

            SearchableDropdown(
                label = "Pilih Posyandu",
                options = state.posyanduList,
                selectedOption = viewModel.selectedPosyandu.value,
                optionToString = { it.namaPosyandu },
                onOptionSelected = { viewModel.selectedPosyandu.value = it },
                enabled = viewModel.selectedDesa.value != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.register() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                else Text("Daftar Sekarang")
            }
        }
    }
}