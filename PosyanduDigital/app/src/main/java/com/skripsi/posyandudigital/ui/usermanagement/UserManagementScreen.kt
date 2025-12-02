package com.skripsi.posyandudigital.ui.usermanagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skripsi.posyandudigital.data.remote.api.RetrofitClient
import com.skripsi.posyandudigital.data.remote.dto.*
import com.skripsi.posyandudigital.data.repository.UserManagementRepositoryImpl
import com.skripsi.posyandudigital.data.session.SessionManager
import com.skripsi.posyandudigital.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    roleToDisplay: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val factory = remember {
        UserManagementViewModelFactory(
            UserManagementRepositoryImpl(RetrofitClient.instance, SessionManager(context))
        )
    }
    val viewModel: UserManagementViewModel = viewModel(factory = factory)
    val state = viewModel.state.value

    LaunchedEffect(key1 = roleToDisplay) {
        viewModel.loadInitialData(roleToDisplay)
    }

    // --- DIALOG MANAGER ---

    // 1. Dialog Tambah/Edit Admin & Kader
    if (state.isAddEditDialogShown) {
        if (roleToDisplay == "admin") {
            AddEditAdminDialog(
                userToEdit = state.editingUser,
                state = state,
                onKecamatanSelected = { viewModel.onKecamatanSelected(it) },
                onDesaSelected = { viewModel.onDesaSelected(it) },
                onDismiss = { viewModel.onDismissAddEditDialog() },
                onSave = { nama, username, password, desa ->
                    viewModel.createUser(
                        CreateUserRequest(username = username, password = password, role = "admin", namaLengkap = nama, desaId = desa.id),
                        originalRole = roleToDisplay
                    )
                }
            )
        } else {
            AddEditKaderDialog(
                userToEdit = state.editingUser,
                state = state,
                onKecamatanSelected = { viewModel.onKecamatanSelected(it) },
                onDesaSelected = { viewModel.onDesaSelected(it) },
                onPosyanduSelected = { viewModel.onPosyanduSelected(it) },
                onAddPosyanduClick = { viewModel.onShowAddPosyanduDialog() }, // Trigger Buka Dialog Posyandu
                onDismiss = { viewModel.onDismissAddEditDialog() },
                onSave = { nama, username, password, posyandu ->
                    viewModel.createUser(
                        CreateUserRequest(username = username, password = password, role = "kader", namaLengkap = nama, posyanduId = posyandu.id),
                        originalRole = roleToDisplay
                    )
                }
            )
        }
    }

    // 2. Dialog Tambah Posyandu (Nested)
    if (state.isAddPosyanduDialogShown) {
        AddPosyanduDialog(
            desaName = state.selectedDesa?.namaDesa ?: "Desa Terpilih",
            onDismiss = { viewModel.onDismissAddPosyanduDialog() },
            onSave = { namaBaru -> viewModel.createPosyandu(namaBaru) }
        )
    }

    if (state.userToDelete != null) {
        DeleteConfirmationDialog(
            userName = state.userToDelete.username,
            onDismiss = { viewModel.onDismissDeleteDialog() },
            onConfirm = { viewModel.onConfirmDelete(roleToDisplay) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Akun ${roleToDisplay.replaceFirstChar { it.uppercase() }}") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Kembali") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onShowAddEditDialog(null) }) { Icon(Icons.Default.Add, "Tambah") }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), contentAlignment = Alignment.Center) {
            when {
                state.isLoading && state.users.isEmpty() -> CircularProgressIndicator()
                state.error != null -> Text(text = "Error: ${state.error}", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                state.users.isEmpty() && !state.isLoading -> Text("Belum ada data ${roleToDisplay}.")
                else -> {
                    UserList(
                        users = state.users,
                        onEditClick = { viewModel.onShowAddEditDialog(it) },
                        onDeleteClick = { viewModel.onDeleteUserClick(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun UserList(users: List<UserDto>, onEditClick: (UserDto) -> Unit, onDeleteClick: (UserDto) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(users) { user ->
            UserListItem(user, { onEditClick(user) }, { onDeleteClick(user) })
        }
    }
}

@Composable
private fun UserListItem(user: UserDto, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }

    val displayName = when(user.role) {
        "admin" -> user.adminProfile?.namaAdmin
        "kader" -> "Kader: ${user.username}"
        else -> user.username
    } ?: user.username

    val locationInfo = when(user.role) {
        "admin" -> "${user.adminProfile?.desa?.namaDesa ?: "-"}, ${user.adminProfile?.desa?.kecamatan?.namaKecamatan ?: "-"}"
        "kader" -> "${user.kaderProfile?.posyandu?.namaPosyandu ?: "-"}"
        else -> ""
    }

    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = displayName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                if (locationInfo.isNotBlank()) {
                    Text(text = locationInfo, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Text(text = "Role: ${user.role}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) { Icon(Icons.Default.MoreVert, "Opsi") }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(text = { Text("Hapus", color = Color.Red) }, onClick = { onDeleteClick(); menuExpanded = false })
                }
            }
        }
    }
}

// --- DIALOG KOMPONEN ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAdminDialog(
    userToEdit: UserDto?,
    state: UserManagementState,
    onKecamatanSelected: (KecamatanDto) -> Unit,
    onDesaSelected: (DesaDto) -> Unit,
    onDismiss: () -> Unit,
    onSave: (String, String, String, DesaDto) -> Unit
) {
    var namaLengkap by remember { mutableStateOf("") }
    var username by remember { mutableStateOf(userToEdit?.username ?: "") }
    var password by remember { mutableStateOf("") }

    val selectedKecamatan = state.selectedKecamatan
    val selectedDesa = state.selectedDesa

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Admin Desa") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = namaLengkap, onValueChange = { namaLengkap = it }, label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())

                Divider()
                Text("Wilayah Tugas:", fontWeight = FontWeight.Bold)
                SearchableDropdown("Pilih Kecamatan", state.kecamatanList, selectedKecamatan, { it.namaKecamatan }, { onKecamatanSelected(it) })
                SearchableDropdown("Pilih Desa", state.desaList, selectedDesa, { it.namaDesa }, { onDesaSelected(it) }, enabled = selectedKecamatan != null)
            }
        },
        confirmButton = {
            Button(
                onClick = { if(selectedDesa != null) onSave(namaLengkap, username, password, selectedDesa) },
                enabled = namaLengkap.isNotBlank() && username.isNotBlank() && password.isNotBlank() && selectedDesa != null
            ) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditKaderDialog(
    userToEdit: UserDto?,
    state: UserManagementState,
    onKecamatanSelected: (KecamatanDto) -> Unit,
    onDesaSelected: (DesaDto) -> Unit,
    onPosyanduSelected: (PosyanduDto) -> Unit,
    onAddPosyanduClick: () -> Unit,
    onDismiss: () -> Unit,
    onSave: (String, String, String, PosyanduDto) -> Unit
) {
    var namaLengkap by remember { mutableStateOf("") }
    var username by remember { mutableStateOf(userToEdit?.username ?: "") }
    var password by remember { mutableStateOf("") }

    val selectedKecamatan = state.selectedKecamatan
    val selectedDesa = state.selectedDesa
    val selectedPosyandu = state.selectedPosyandu

    // Cek apakah dropdown harus dikunci (karena Admin Desa)
    val isLocked = state.isAdminDesaLocked

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Kader Posyandu") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(namaLengkap, { namaLengkap = it }, label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(username, { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(password, { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())

                Divider()
                Text("Lokasi Posyandu:", fontWeight = FontWeight.Bold)

                // Dropdown Kecamatan (Locked jika Admin Desa)
                SearchableDropdown(
                    label = "Pilih Kecamatan",
                    options = state.kecamatanList,
                    selectedOption = selectedKecamatan,
                    optionToString = { it.namaKecamatan },
                    onOptionSelected = onKecamatanSelected,
                    enabled = !isLocked // Disabled jika locked
                )

                // Dropdown Desa (Locked jika Admin Desa)
                SearchableDropdown(
                    label = "Pilih Desa",
                    options = state.desaList,
                    selectedOption = selectedDesa,
                    optionToString = { it.namaDesa },
                    onOptionSelected = onDesaSelected,
                    enabled = !isLocked && selectedKecamatan != null // Disabled jika locked
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) {
                        SearchableDropdown("Pilih Posyandu", state.posyanduList, selectedPosyandu, { it.namaPosyandu }, onPosyanduSelected, enabled = selectedDesa != null)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    FilledIconButton(onClick = onAddPosyanduClick, enabled = selectedDesa != null, modifier = Modifier.size(48.dp).padding(top = 8.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { if(selectedPosyandu != null) onSave(namaLengkap, username, password, selectedPosyandu) }) { Text("Simpan") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}


// Dialog Kecil untuk Tambah Posyandu
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPosyanduDialog(
    desaName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var namaPosyandu by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Posyandu Baru") },
        text = {
            Column {
                Text("Menambahkan Posyandu di:", style = MaterialTheme.typography.bodySmall)
                Text(desaName, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

                OutlinedTextField(
                    value = namaPosyandu,
                    onValueChange = { namaPosyandu = it },
                    label = { Text("Nama Posyandu (Misal: Mawar 1)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(namaPosyandu) },
                enabled = namaPosyandu.isNotBlank()
            ) { Text("Buat") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableDropdown(label: String, options: List<T>, selectedOption: T?, optionToString: (T) -> String, onOptionSelected: (T) -> Unit, enabled: Boolean = true) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = if (selectedOption != null) optionToString(selectedOption) else ""
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { if (enabled) expanded = !expanded }) {
        OutlinedTextField(value = displayText, onValueChange = {}, readOnly = true, label = { Text(label) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth(), enabled = enabled)
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (options.isEmpty()) DropdownMenuItem(text = { Text("Tidak ada data") }, onClick = {})
            else options.forEach { option -> DropdownMenuItem(text = { Text(optionToString(option)) }, onClick = { onOptionSelected(option); expanded = false }) }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(userName: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Konfirmasi Hapus") }, text = { Text("Hapus akun '$userName'?") }, confirmButton = { Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Hapus") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } })
}