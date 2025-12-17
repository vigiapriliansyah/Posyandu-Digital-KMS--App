package com.skripsi.posyandudigital.ui.kader

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.posyandudigital.data.repository.UserManagementRepository
import com.skripsi.posyandudigital.utils.ResultWrapper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class VerificationState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class VerificationViewModel(
    private val repository: UserManagementRepository
) : ViewModel() {

    private val _state = mutableStateOf(VerificationState())
    val state: State<VerificationState> = _state

    // State untuk menampung input kode dari UI
    var verificationCode = mutableStateOf("")

    // Fungsi Verifikasi Baru dengan Kode
    fun verifyUser() {
        val code = verificationCode.value
        if (code.isBlank() || code.length < 6) {
            _state.value = _state.value.copy(error = "Masukkan 6 digit kode verifikasi")
            return
        }

        repository.verifyByCode(code).onEach { result ->
            when (result) {
                is ResultWrapper.Loading -> _state.value = _state.value.copy(isLoading = true, error = null)
                is ResultWrapper.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Akun Orang Tua Berhasil Diverifikasi!",
                        error = null
                    )
                    verificationCode.value = "" // Reset field setelah sukses
                }
                is ResultWrapper.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    fun dismissMessage() {
        _state.value = _state.value.copy(successMessage = null, error = null)
    }
}