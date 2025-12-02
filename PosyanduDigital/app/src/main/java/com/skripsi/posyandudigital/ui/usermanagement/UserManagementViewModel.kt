package com.skripsi.posyandudigital.ui.usermanagement

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.posyandudigital.data.remote.dto.*
import com.skripsi.posyandudigital.data.repository.UserManagementRepository
import com.skripsi.posyandudigital.utils.ResultWrapper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class UserManagementState(
    val isLoading: Boolean = false,
    val users: List<UserDto> = emptyList(),
    val error: String? = null,
    val isAddEditDialogShown: Boolean = false,
    val isAddPosyanduDialogShown: Boolean = false,
    val editingUser: UserDto? = null,
    val userToDelete: UserDto? = null,

    val kecamatanList: List<KecamatanDto> = emptyList(),
    val desaList: List<DesaDto> = emptyList(),
    val posyanduList: List<PosyanduDto> = emptyList(),

    val selectedKecamatan: KecamatanDto? = null,
    val selectedDesa: DesaDto? = null,
    val selectedPosyandu: PosyanduDto? = null,

    val isAdminDesaLocked: Boolean = false
)

class UserManagementViewModel(
    private val repository: UserManagementRepository
) : ViewModel() {

    private val _state = mutableStateOf(UserManagementState())
    val state: State<UserManagementState> = _state

    fun loadInitialData(role: String?) {
        loadUsers(role)
        loadKecamatan()
        checkMyProfileAndAutoSelect()
    }

    private fun checkMyProfileAndAutoSelect() {
        repository.getCurrentUser().onEach { result ->
            if (result is ResultWrapper.Success) {
                val me = result.data
                // Cek apakah saya Admin Desa dan punya data wilayah
                if (me.role == "admin" && me.adminProfile?.desa != null) {
                    val myDesa = me.adminProfile.desa
                    val myKecamatan = myDesa.kecamatan

                    if (myKecamatan != null) {
                        // PERBAIKAN UTAMA: Gunakan ID asli dari database (bukan hashCode)
                        // Karena DTO sudah diupdate, sekarang field .id sudah tersedia
                        val kecDto = KecamatanDto(myKecamatan.id, myKecamatan.namaKecamatan)
                        val desaDto = DesaDto(myDesa.id, myDesa.namaDesa)

                        _state.value = _state.value.copy(
                            isAdminDesaLocked = true,
                            selectedKecamatan = kecDto,
                            selectedDesa = desaDto
                        )

                        // Load posyandu menggunakan ID desa yang BENAR
                        loadPosyanduByDesa(myDesa.id)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    // --- SISA FUNGSI (SAMA SEPERTI SEBELUMNYA) ---
    // Pastikan tidak ada fungsi yang terhapus saat copy-paste

    fun loadUsers(role: String?) {
        repository.getUsers(role).onEach { result ->
            when (result) {
                is ResultWrapper.Loading -> _state.value = _state.value.copy(isLoading = true)
                is ResultWrapper.Success -> _state.value = _state.value.copy(isLoading = false, users = result.data ?: emptyList(), error = null)
                is ResultWrapper.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    private fun loadKecamatan() {
        // PERBAIKAN: Tambahkan 'result ->' agar variabel result dikenali
        repository.getKecamatan().onEach { result ->
            if (result is ResultWrapper.Success) _state.value = _state.value.copy(kecamatanList = result.data ?: emptyList())
        }.launchIn(viewModelScope)
    }

    fun onKecamatanSelected(kecamatan: KecamatanDto) {
        _state.value = _state.value.copy(
            selectedKecamatan = kecamatan,
            selectedDesa = null, selectedPosyandu = null,
            desaList = emptyList(), posyanduList = emptyList()
        )
        repository.getDesa(kecamatan.id).onEach {
            if (it is ResultWrapper.Success) _state.value = _state.value.copy(desaList = it.data ?: emptyList())
        }.launchIn(viewModelScope)
    }

    fun onDesaSelected(desa: DesaDto) {
        _state.value = _state.value.copy(
            selectedDesa = desa, selectedPosyandu = null,
            posyanduList = emptyList()
        )
        loadPosyanduByDesa(desa.id)
    }

    private fun loadPosyanduByDesa(desaId: Int) {
        repository.getPosyandu(desaId).onEach {
            if (it is ResultWrapper.Success) _state.value = _state.value.copy(posyanduList = it.data ?: emptyList())
        }.launchIn(viewModelScope)
    }

    fun onPosyanduSelected(posyandu: PosyanduDto) {
        _state.value = _state.value.copy(selectedPosyandu = posyandu)
    }

    // --- DIALOG LOGIC ---
    fun onShowAddEditDialog(user: UserDto?) {
        if (_state.value.isAdminDesaLocked) {
            _state.value = _state.value.copy(isAddEditDialogShown = true, editingUser = user, selectedPosyandu = null)
            // Pastikan list posyandu termuat ulang saat dialog dibuka
            _state.value.selectedDesa?.let { loadPosyanduByDesa(it.id) }
        } else {
            _state.value = _state.value.copy(isAddEditDialogShown = true, editingUser = user, selectedKecamatan = null, selectedDesa = null, selectedPosyandu = null)
        }
    }

    fun onDismissAddEditDialog() { _state.value = _state.value.copy(isAddEditDialogShown = false, editingUser = null) }

    fun onShowAddPosyanduDialog() { _state.value = _state.value.copy(isAddPosyanduDialogShown = true) }
    fun onDismissAddPosyanduDialog() { _state.value = _state.value.copy(isAddPosyanduDialogShown = false) }

    fun createPosyandu(nama: String) {
        val desa = _state.value.selectedDesa ?: return
        repository.createPosyandu(nama, desa.id).onEach { result ->
            when(result) {
                is ResultWrapper.Loading -> _state.value = _state.value.copy(isLoading = true)
                is ResultWrapper.Success -> {
                    onDismissAddPosyanduDialog()
                    loadPosyanduByDesa(desa.id) // Refresh list
                    _state.value = _state.value.copy(isLoading = false)
                }
                is ResultWrapper.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    fun createUser(request: CreateUserRequest, originalRole: String) {
        val finalRequest = if (_state.value.isAdminDesaLocked && request.desaId == null) {
            request.copy(desaId = _state.value.selectedDesa?.id)
        } else {
            request
        }

        repository.createUser(finalRequest).onEach { result ->
            when(result) {
                is ResultWrapper.Loading -> _state.value = _state.value.copy(isLoading = true)
                is ResultWrapper.Success -> {
                    onDismissAddEditDialog()
                    loadUsers(originalRole)
                    _state.value = _state.value.copy(isLoading = false)
                }
                is ResultWrapper.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }.launchIn(viewModelScope)
    }

    fun onDeleteUserClick(user: UserDto) { _state.value = _state.value.copy(userToDelete = user) }
    fun onDismissDeleteDialog() { _state.value = _state.value.copy(userToDelete = null) }
    fun onConfirmDelete(originalRole: String) {
        val user = _state.value.userToDelete ?: return
        repository.deleteUser(user.id).onEach {
            if (it is ResultWrapper.Success) { onDismissDeleteDialog(); loadUsers(originalRole) }
        }.launchIn(viewModelScope)
    }
}