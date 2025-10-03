package com.skripsi.posyandudigital.utils

/**
 * Sealed class generik untuk membungkus hasil dari repository.
 * Ini membantu UI untuk bereaksi terhadap state yang berbeda (Loading, Success, Error).
 */
sealed class ResultWrapper<out T> {
    data object Loading : ResultWrapper<Nothing>()
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data class Error(val message: String) : ResultWrapper<Nothing>()
}
