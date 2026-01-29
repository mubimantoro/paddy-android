package com.example.sipaddy.utils

sealed class ResultState<out R> private constructor() {
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val message: String, val code: Int? = null) :
        ResultState<Nothing>()

    object Loading : ResultState<Nothing>()
}