package com.skripsi.posyandudigital.ui.usermanagement

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.posyandudigital.data.remote.dto.CreateUserRequest
import com.skripsi.posyandudigital.data.remote.dto.UserDto
import com.skripsi.posyandudigital.data.repository.UserManagementRepository
import com.skripsi.posyandudigital.utils.ResultWrapper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

// Perbarui State untuk menangani dialog konfirmasi hapus
data class UserManagementState(
    val isLoading: Boolean = false,
    val users: List<UserDto> = emptyList(),
    val error: String? = null,
    val isAddEditDialogShown: Boolean = false,
    val editingUser: UserDto? = null,
    val userToDelete: UserDto? = null // null berarti dialog hapus tidak tampil
)

class UserManagementViewModel(
    private val repository: UserManagementRepository
) : ViewModel() {

    private val _state = mutableStateOf(UserManagementState())
    val state: State<UserManagementState> = _state

    fun loadUsers(role: String?) {
        repository.getUsers(role).onEach { result ->
            _state.value = when (result) {
                is ResultWrapper.Loading -> _state.value.copy(isLoading = true)
                is ResultWrapper.Success -> _state.value.copy(isLoading = false, users = result.data ?: emptyList(), error = null)
                is ResultWrapper.Error -> _state.value.copy(isLoading = false, error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    // --- Fungsi untuk dialog Tambah/Edit ---
    fun onShowAddEditDialog(user: UserDto?) {
        _state.value = _state.value.copy(isAddEditDialogShown = true, editingUser = user)
    }

    fun onDismissAddEditDialog() {
        _state.value = _state.value.copy(isAddEditDialogShown = false, editingUser = null)
    }

    fun createUser(request: CreateUserRequest, originalRole: String) {
        repository.createUser(request).onEach { result ->
            when(result) {
                is ResultWrapper.Loading -> { /* Tampilkan loading di dialog jika perlu */ }
                is ResultWrapper.Success -> {
                    onDismissAddEditDialog()
                    loadUsers(originalRole) // Muat ulang daftar setelah berhasil
                }
                is ResultWrapper.Error -> {
                    // Tampilkan error di dialog
                }
            }
        }.launchIn(viewModelScope)
    }

    // --- FUNGSI BARU UNTUK MENGELOLA PROSES HAPUS ---

    fun onDeleteUserClick(user: UserDto) {
        _state.value = _state.value.copy(userToDelete = user)
    }

    fun onDismissDeleteDialog() {
        _state.value = _state.value.copy(userToDelete = null)
    }

    fun onConfirmDelete(originalRole: String) {
        val user = state.value.userToDelete ?: return
        repository.deleteUser(user.id).onEach { result ->
            when(result) {
                is ResultWrapper.Loading -> { /* Tampilkan loading */ }
                is ResultWrapper.Success -> {
                    onDismissDeleteDialog()
                    loadUsers(originalRole) // Muat ulang daftar setelah berhasil menghapus
                }
                is ResultWrapper.Error -> {
                    // Tampilkan error
                    onDismissDeleteDialog()
                }
            }
        }.launchIn(viewModelScope)
    }
}

