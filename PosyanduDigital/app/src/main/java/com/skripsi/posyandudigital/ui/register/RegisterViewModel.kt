package com.skripsi.posyandudigital.ui.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.posyandudigital.data.remote.api.RetrofitClient
import com.skripsi.posyandudigital.data.remote.dto.*
import kotlinx.coroutines.launch

data class RegisterState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val registeredVerificationCode: String? = null,

    val kecamatanList: List<KecamatanDto> = emptyList(),
    val desaList: List<DesaDto> = emptyList(),
    val posyanduList: List<PosyanduDto> = emptyList()
)

class RegisterViewModel : ViewModel() {
    private val apiService = RetrofitClient.instance

    private val _state = mutableStateOf(RegisterState())
    val state: State<RegisterState> = _state

    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var namaIbu = mutableStateOf("")
    var noHp = mutableStateOf("")
    var alamat = mutableStateOf("")

    var selectedKecamatan = mutableStateOf<KecamatanDto?>(null)
    var selectedDesa = mutableStateOf<DesaDto?>(null)
    var selectedPosyandu = mutableStateOf<PosyanduDto?>(null)

    init {
        loadKecamatan()
    }

    private fun loadKecamatan() {
        viewModelScope.launch {
            try {
                val response = apiService.getKecamatanPublic()
                if (response.isSuccessful) {
                    _state.value = _state.value.copy(kecamatanList = response.body() ?: emptyList())
                }
            } catch (e: Exception) {}
        }
    }

    fun onKecamatanSelected(kecamatan: KecamatanDto) {
        selectedKecamatan.value = kecamatan
        selectedDesa.value = null
        selectedPosyandu.value = null
        _state.value = _state.value.copy(desaList = emptyList(), posyanduList = emptyList())

        viewModelScope.launch {
            try {
                val response = apiService.getDesaPublic(kecamatan.id)
                if (response.isSuccessful) {
                    _state.value = _state.value.copy(desaList = response.body() ?: emptyList())
                }
            } catch (e: Exception) {}
        }
    }

    fun onDesaSelected(desa: DesaDto) {
        selectedDesa.value = desa
        selectedPosyandu.value = null
        _state.value = _state.value.copy(posyanduList = emptyList())

        viewModelScope.launch {
            try {
                val response = apiService.getPosyanduPublic(desa.id)
                if (response.isSuccessful) {
                    _state.value = _state.value.copy(posyanduList = response.body() ?: emptyList())
                }
            } catch (e: Exception) {}
        }
    }

    fun register() {
        if (selectedPosyandu.value == null) {
            _state.value = _state.value.copy(error = "Pilih Posyandu terlebih dahulu")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val request = RegisterOrangTuaRequest(
                    username = username.value,
                    password = password.value,
                    namaIbu = namaIbu.value,
                    noHp = noHp.value,
                    alamat = alamat.value,
                    posyanduId = selectedPosyandu.value!!.id
                )

                val response = apiService.registerOrangTua(request)

                if (response.isSuccessful && response.body() != null) {
                    val respBody = response.body()!!
                    // DEBUG LOG: Cek di Logcat apakah kode ini muncul
                    println("DEBUG REGISTER: ${respBody.user?.kodeVerifikasi}")

                    val code = respBody.user?.kodeVerifikasi

                    _state.value = _state.value.copy(
                        isLoading = false,
                        success = true,
                        registeredVerificationCode = code
                    )
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Registrasi Gagal: ${response.code()}"
                    _state.value = _state.value.copy(isLoading = false, error = errorMsg)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Print error stack trace
                _state.value = _state.value.copy(isLoading = false, error = "Error koneksi: ${e.message}")
            }
        }
    }

    fun resetState() {
        _state.value = RegisterState()
    }
}