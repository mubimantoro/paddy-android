package com.example.sipaddy.data

sealed class ResultState<out R> private constructor() {
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val error: String, val isTokenExpired: Boolean = false) :
        ResultState<Nothing>()
    data object Loading : ResultState<Nothing>()
}