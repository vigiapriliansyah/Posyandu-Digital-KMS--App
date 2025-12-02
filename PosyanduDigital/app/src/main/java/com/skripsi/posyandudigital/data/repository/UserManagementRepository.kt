package com.skripsi.posyandudigital.data.repository

import com.skripsi.posyandudigital.data.remote.api.ApiService
import com.skripsi.posyandudigital.data.remote.dto.*
import com.skripsi.posyandudigital.data.session.SessionManager
import com.skripsi.posyandudigital.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

// --- 1. INTERFACE (KONTRAK) ---
// Pastikan fungsi 'createPosyandu' ada di sini!
interface UserManagementRepository {
    // User CRUD
    fun getUsers(role: String?): Flow<ResultWrapper<List<UserDto>>>
    fun createUser(user: CreateUserRequest): Flow<ResultWrapper<UserDto>>
    fun updateUser(id: Int, user: UpdateUserRequest): Flow<ResultWrapper<UserDto>>
    fun deleteUser(id: Int): Flow<ResultWrapper<Unit>>

    // Wilayah
    fun getKecamatan(): Flow<ResultWrapper<List<KecamatanDto>>>
    fun getDesa(kecamatanId: Int): Flow<ResultWrapper<List<DesaDto>>>
    fun getPosyandu(desaId: Int): Flow<ResultWrapper<List<PosyanduDto>>>

    // Create Posyandu (INI YANG SEBELUMNYA HILANG/ERROR)
    fun createPosyandu(nama: String, desaId: Int): Flow<ResultWrapper<PosyanduDto>>
    fun getCurrentUser(): Flow<ResultWrapper<UserDto>>

    
}

// --- 2. IMPLEMENTASI (LOGIKA) ---

