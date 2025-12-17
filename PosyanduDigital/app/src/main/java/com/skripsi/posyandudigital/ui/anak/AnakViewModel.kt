package com.skripsi.posyandudigital.ui.anak

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skripsi.posyandudigital.data.remote.dto.AnakDetailDto
import com.skripsi.posyandudigital.data.remote.dto.CreateAnakRequest
import com.skripsi.posyandudigital.data.remote.dto.OrangTuaSimpleDto
import com.skripsi.posyandudigital.data.repository.AnakRepository
import com.skripsi.posyandudigital.utils.ResultWrapper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class AnakState(
    val isLoading: Boolean = false,
    val anakList: List<AnakDetailDto> = emptyList(),
    val orangTuaList: List<OrangTuaSimpleDto> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class AnakViewModel(private val repository: AnakRepository) : ViewModel() {

    private val _state = mutableStateOf(AnakState())
    val state: State<AnakState> = _state

    fun loadAnakList() {
        repository.getAnakList().onEach { result ->
            when (result) {
                is ResultWrapper.Loading -> _state.value = _state.value.copy(isLoading = true, error = null)
                is ResultWrapper.Success -> _state.value = _state.value.copy(isLoading = false, anakList = result.data)
                is ResultWrapper.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    fun loadOrangTuaList() {
        repository.getOrangTuaVerified().onEach { result ->
            if (result is ResultWrapper.Success) {
                _state.value = _state.value.copy(orangTuaList = result.data)
            }
        }.launchIn(viewModelScope)
    }

    fun createAnak(request: CreateAnakRequest) {
        repository.createAnak(request).onEach { result ->
            when (result) {
                is ResultWrapper.Loading -> _state.value = _state.value.copy(isLoading = true, error = null)
                is ResultWrapper.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Data Anak Berhasil Ditambahkan!"
                    )
                    loadAnakList() // Refresh list
                }
                is ResultWrapper.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    fun dismissMessage() {
        _state.value = _state.value.copy(successMessage = null, error = null)
    }
}

class AnakViewModelFactory(private val repository: AnakRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnakViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnakViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}