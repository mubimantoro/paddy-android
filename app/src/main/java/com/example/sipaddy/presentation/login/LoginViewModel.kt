package com.example.sipaddy.presentation.login

import android.util.Log
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

    fun saveSession(username: String, token: String, callback: () -> Unit) {
        viewModelScope.launch {
            repository.saveSession(username, token)
            callback()
        }
    }

    fun getSession(): LiveData<String> {
        return repository.getSession().asLiveData()
    }
}