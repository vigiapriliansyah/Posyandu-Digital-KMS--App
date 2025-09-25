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

    // --- STATES UNTUK UI ---
    private val _username = mutableStateOf("")
    val username: State<String> = _username

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _passwordVisible = mutableStateOf(false)
    val passwordVisible: State<Boolean> = _passwordVisible

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _loggedInUser = mutableStateOf<UserDto?>(null)
    val loggedInUser: State<UserDto?> = _loggedInUser


    // --- FUNGSI UNTUK UI ---
    fun onUsernameChange(newUsername: String) { _username.value = newUsername }
    fun onPasswordChange(newPassword: String) { _password.value = newPassword }
    fun togglePasswordVisibility() { _passwordVisible.value = !_passwordVisible.value }
    fun onNavigationDone() { _loggedInUser.value = null }


    // --- LOGIKA UTAMA ---
    fun login() {
        if (_isLoading.value) return
        _errorMessage.value = null

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = LoginRequest(username = _username.value, password = _password.value)
                val response = RetrofitClient.instance.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    sessionManager.saveSession(loginResponse.token, loginResponse.user.role)
                    _loggedInUser.value = loginResponse.user
                    println("Login & Sesi Berhasil Disimpan! Role: ${loginResponse.user.role}")
                } else {
                    _errorMessage.value = "Login Gagal: Username atau Password salah."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: Tidak bisa terhubung ke server."
            } finally {
                _isLoading.value = false
            }
        }
    }
}

