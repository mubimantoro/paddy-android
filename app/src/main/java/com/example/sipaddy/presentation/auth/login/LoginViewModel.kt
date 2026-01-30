package com.example.sipaddy.presentation.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.model.response.LoginResponse
import com.example.sipaddy.data.repository.AuthRepository
import com.example.sipaddy.utils.ResultState
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _loginResult = MutableLiveData<ResultState<LoginResponse>>()
    val loginResult: LiveData<ResultState<LoginResponse>> = _loginResult

    fun login(username: String, password: String) {
        when {
            username.isEmpty() -> {
                _loginResult.value = ResultState.Error("Username tidak boleh kosong")
                return
            }
            password.isEmpty() -> {
                _loginResult.value = ResultState.Error("Password tidak boleh kosong")
                return
            }
        }
        viewModelScope.launch {
            authRepository.login(username, password).collect {
                _loginResult.value = it
            }
        }
    }
}