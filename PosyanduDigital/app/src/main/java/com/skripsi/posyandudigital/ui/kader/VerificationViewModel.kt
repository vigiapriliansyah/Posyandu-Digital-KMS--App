package com.skripsi.posyandudigital.ui.kader

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.posyandudigital.data.remote.dto.PendingOrangTuaDto
import com.skripsi.posyandudigital.data.repository.UserManagementRepository
import com.skripsi.posyandudigital.utils.ResultWrapper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class VerificationState(
    val isLoading: Boolean = false,
    val pendingList: List<PendingOrangTuaDto> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class VerificationViewModel(
    private val repository: UserManagementRepository
) : ViewModel() {

    private val _state = mutableStateOf(VerificationState())
    val state: State<VerificationState> = _state

    init {
        loadPendingList()
    }

    fun loadPendingList() {
        repository.getPendingVerifications().onEach { result ->
            when (result) {
                is ResultWrapper.Loading -> _state.value = _state.value.copy(isLoading = true, error = null)
                is ResultWrapper.Success -> _state.value = _state.value.copy(isLoading = false, pendingList = result.data ?: emptyList())
                is ResultWrapper.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    fun verifyUser(userId: Int) {
        repository.verifyOrangTua(userId).onEach { result ->
            when (result) {
                is ResultWrapper.Loading -> _state.value = _state.value.copy(isLoading = true)
                is ResultWrapper.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "User berhasil diverifikasi!"
                    )
                    loadPendingList()
                }
                is ResultWrapper.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    // FUNGSI INI YANG SEBELUMNYA HILANG: Untuk menolak pendaftaran
    fun rejectUser(userId: Int) {
        repository.rejectOrangTua(userId).onEach { result ->
            when (result) {
                is ResultWrapper.Loading -> _state.value = _state.value.copy(isLoading = true)
                is ResultWrapper.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Pendaftaran berhasil ditolak!"
                    )
                    loadPendingList()
                }
                is ResultWrapper.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    // FUNGSI INI YANG SEBELUMNYA HILANG: Untuk verifikasi pakai 6 digit kode
    fun verifyByCode(code: String) {
        repository.verifyByCode(code).onEach { result ->
            when (result) {
                is ResultWrapper.Loading -> _state.value = _state.value.copy(isLoading = true)
                is ResultWrapper.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Verifikasi berhasil menggunakan kode!"
                    )
                    loadPendingList()
                }
                is ResultWrapper.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    // FUNGSI INI YANG SEBELUMNYA HILANG: Untuk membersihkan pesan error di layar
    fun clearMessages() {
        _state.value = _state.value.copy(error = null, successMessage = null)
    }

    fun dismissMessage() {
        clearMessages()
    }
}