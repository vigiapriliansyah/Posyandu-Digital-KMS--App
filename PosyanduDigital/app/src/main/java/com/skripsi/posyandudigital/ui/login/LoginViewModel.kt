package com.skripsi.posyandudigital.ui.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.posyandudigital.data.remote.api.RetrofitClient
import com.skripsi.posyandudigital.data.remote.dto.LoginRequest
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
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
    private val _loginSuccess = mutableStateOf(false)
    val loginSuccess: State<Boolean> = _loginSuccess

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun login() {
        if (_isLoading.value) return
        // Reset pesan error setiap kali login
        _errorMessage.value = null

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = LoginRequest(username = _username.value, password = _password.value)
                val response = RetrofitClient.instance.login(request)

                if (response.isSuccessful) {
                    // Login Berhasil
                    val loginResponse = response.body()
                    println("Login Berhasil! Token: ${loginResponse?.token}")
                    _loginSuccess.value = true
                } else {
                    // Login Gagal (misal: password salah)
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Login Gagal: Username atau Password salah."
                    println("Login Gagal: $errorBody")
                }
            } catch (e: Exception) {
                // Error Jaringan (misal: server mati, tidak ada internet)
                _errorMessage.value = "Error: Tidak bisa terhubung ke server. Cek koneksi Anda."
                println("Error Jaringan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}