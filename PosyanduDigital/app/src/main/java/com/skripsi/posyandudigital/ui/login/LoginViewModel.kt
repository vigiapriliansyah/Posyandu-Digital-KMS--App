package com.skripsi.posyandudigital.ui.login

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.posyandudigital.data.remote.api.RetrofitClient
import com.skripsi.posyandudigital.data.remote.dto.LoginRequest
import com.skripsi.posyandudigital.data.remote.dto.UserDto
import com.skripsi.posyandudigital.data.session.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(getApplication())

    // --- STATES ---
    val username = mutableStateOf("")
    val password = mutableStateOf("")
    val passwordVisible = mutableStateOf(false)
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    // State Navigasi
    val loggedInUser = mutableStateOf<UserDto?>(null)
    // State baru: Jika butuh verifikasi
    val pendingVerificationUser = mutableStateOf<UserDto?>(null)

    // --- ACTIONS ---
    fun onUsernameChange(newVal: String) { username.value = newVal }
    fun onPasswordChange(newVal: String) { password.value = newVal }
    fun togglePasswordVisibility() { passwordVisible.value = !passwordVisible.value }

    fun onNavigationDone() {
        loggedInUser.value = null
        pendingVerificationUser.value = null
    }

    fun login() {
        if (isLoading.value) return
        errorMessage.value = null

        viewModelScope.launch {
            isLoading.value = true
            try {
                val request = LoginRequest(username.value, password.value)
                val response = RetrofitClient.instance.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    // Cek apakah butuh verifikasi?
                    if (body.requireVerification) {
                        // Jangan simpan sesi login, arahkan ke layar kode
                        pendingVerificationUser.value = body.user
                    } else {
                        // Login Normal
                        sessionManager.saveSession(body.token, body.user.role)
                        loggedInUser.value = body.user
                    }
                } else {
                    errorMessage.value = "Login Gagal: Periksa username/password."
                }
            } catch (e: Exception) {
                errorMessage.value = "Error: Tidak bisa terhubung ke server."
            } finally {
                isLoading.value = false
            }
        }
    }
}