package com.example.sipaddy.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.LoginResponse
import com.example.sipaddy.data.repository.PaddyRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: PaddyRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<ResultState<LoginResponse>>()
    val loginResult: LiveData<ResultState<LoginResponse>> = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            repository.login(username, password).collect {
                _loginResult.value = it
            }
        }
    }

    fun saveSession(
        namaLengkap: String,
        username: String,
        token: String,
        role: String,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            repository.saveSession(namaLengkap, username, token, role)
            callback()
        }
    }

    fun getSession(): LiveData<String> {
        return repository.getSession().asLiveData()
    }

    fun getRole(): LiveData<String> {
        return repository.getRole().asLiveData()
    }
}