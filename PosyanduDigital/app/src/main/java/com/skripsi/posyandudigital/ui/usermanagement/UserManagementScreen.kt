package com.skripsi.posyandudigital.ui.usermanagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skripsi.posyandudigital.data.remote.api.RetrofitClient
import com.skripsi.posyandudigital.data.remote.dto.CreateUserRequest
import com.skripsi.posyandudigital.data.remote.dto.UserDto
import com.skripsi.posyandudigital.data.repository.UserManagementRepositoryImpl
import com.skripsi.posyandudigital.data.session.SessionManager
import com.skripsi.posyandudigital.ui.theme.TextPrimary
import com.skripsi.posyandudigital.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    roleToDisplay: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: UserManagementViewModel = viewModel(
        factory = UserManagementViewModelFactory(
            UserManagementRepositoryImpl(
                apiService = RetrofitClient.instance,
                sessionManager = SessionManager(context)
            )
        )
    )
    val state = viewModel.state.value

    LaunchedEffect(key1 = roleToDisplay) {
        viewModel.loadUsers(roleToDisplay)
    }

    // Tampilkan dialog Tambah/Edit jika state-nya true
    if (state.isAddEditDialogShown) {
        AddEditUserDialog(
            userToEdit = state.editingUser,
            role = roleToDisplay,
            onDismiss = { viewModel.onDismissAddEditDialog() },
            onSave = { username, password ->
                viewModel.createUser(
                    CreateUserRequest(username, password, roleToDisplay),
                    originalRole = roleToDisplay
                )
            }
        )
    }

    // --- FUNGSI BARU: Tampilkan dialog konfirmasi hapus ---
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
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onShowAddEditDialog(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah User")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading && state.users.isEmpty() -> CircularProgressIndicator()
                state.error != null -> Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                state.users.isEmpty() && !state.isLoading -> Text("Belum ada akun ${roleToDisplay} yang terdaftar.")
                else -> {
                    // --- PERUBAHAN UTAMA: Hubungkan aksi onDeleteClick ke ViewModel ---
                    UserList(
                        users = state.users,
                        onEditClick = { user -> viewModel.onShowAddEditDialog(user) },
                        onDeleteClick = { user -> viewModel.onDeleteUserClick(user) } // <-- Dihubungkan!
                    )
                }
            }
        }
    }
}

// ... Composable UserList dan UserListItem tidak berubah ...
@Composable
private fun UserList(
    users: List<UserDto>,
    onEditClick: (UserDto) -> Unit,
    onDeleteClick: (UserDto) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(users) { user ->
            UserListItem(
                user = user,
                onEditClick = { onEditClick(user) },
                onDeleteClick = { onDeleteClick(user) }
            )
        }
    }
}

@Composable
private fun UserListItem(user: UserDto, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.username, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Dibuat pada: ${user.createdAt.take(10)}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opsi")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            onEditClick()
                            menuExpanded = false
                        },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                    )
                    DropdownMenuItem(
                        text = { Text("Hapus", color = Color.Red) },
                        onClick = {
                            onDeleteClick()
                            menuExpanded = false
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red) }
                    )
                }
            }
        }
    }
}


// ... Composable AddEditUserDialog tidak berubah ...
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditUserDialog(
    userToEdit: UserDto?,
    role: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var username by remember { mutableStateOf(userToEdit?.username ?: "") }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (userToEdit == null) "Tambah Akun $role" else "Edit Akun ${userToEdit.username}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(if (userToEdit == null) "Password" else "Password Baru (Opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(username, password) }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}


// --- COMPOSABLE BARU UNTUK KONFIRMASI HAPUS ---
@Composable
fun DeleteConfirmationDialog(
    userName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Hapus") },
        text = { Text("Apakah Anda yakin ingin menghapus akun '$userName'? Tindakan ini tidak dapat dibatalkan.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Ya, Hapus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

