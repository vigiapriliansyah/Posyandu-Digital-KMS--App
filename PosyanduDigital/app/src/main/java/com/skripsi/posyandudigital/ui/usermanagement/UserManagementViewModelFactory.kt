package com.skripsi.posyandudigital.ui.usermanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skripsi.posyandudigital.data.repository.UserManagementRepository

/**
 * Pabrik ini bertugas membuat UserManagementViewModel
 * dengan menyediakan repository yang dibutuhkannya.
 */
class UserManagementViewModelFactory(
    private val repository: UserManagementRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserManagementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserManagementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
