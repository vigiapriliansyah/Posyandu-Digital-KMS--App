package com.skripsi.posyandudigital.ui.pengukuran

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skripsi.posyandudigital.data.remote.dto.CreatePengukuranRequest
import com.skripsi.posyandudigital.data.remote.dto.PengukuranDetailDto
import com.skripsi.posyandudigital.data.repository.PengukuranRepository
import com.skripsi.posyandudigital.utils.ResultWrapper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

// State untuk menyimpan data yang akan ditampilkan di UI
data class PengukuranState(
    val isLoading: Boolean = false,
    val riwayatList: List<PengukuranDetailDto> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

// Kelas ViewModel-nya
class PengukuranViewModel(private val repository: PengukuranRepository) : ViewModel() {
    private val _state = mutableStateOf(PengukuranState())
    val state: State<PengukuranState> = _state

    fun loadRiwayat(anakId: Int) {
        repository.getRiwayat(anakId).onEach { result ->
            when (result) {
                is ResultWrapper.Loading -> _state.value = _state.value.copy(isLoading = true, error = null)
                is ResultWrapper.Success -> _state.value = _state.value.copy(isLoading = false, riwayatList = result.data)
                is ResultWrapper.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    fun simpanPengukuran(request: CreatePengukuranRequest) {
        repository.createPengukuran(request).onEach { result ->
            when (result) {
                is ResultWrapper.Loading -> _state.value = _state.value.copy(isLoading = true, error = null)
                is ResultWrapper.Success -> {
                    // Tampilkan pesan sukses dari backend beserta info gizi
                    val msg = "Berhasil! Gizi: ${result.data.analisis?.bbU ?: "-"}"
                    _state.value = _state.value.copy(isLoading = false, successMessage = msg)

                    // Langsung refresh data riwayat setelah berhasil simpan
                    loadRiwayat(request.anakId)
                }
                is ResultWrapper.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    // Fungsi untuk menghilangkan notifikasi Snackbar setelah muncul
    fun dismissMessage() {
        _state.value = _state.value.copy(successMessage = null, error = null)
    }
}

// Factory wajib ada jika ViewModel butuh parameter (Repository)
class PengukuranViewModelFactory(private val repository: PengukuranRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PengukuranViewModel(repository) as T
    }
}