package com.example.sipaddy.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.model.response.KelompokTaniResponse
import com.example.sipaddy.data.model.response.LoginResponse
import com.example.sipaddy.data.model.response.RegisterResponse
import com.example.sipaddy.data.repository.AuthRepository
import com.example.sipaddy.data.repository.DataRepository
import com.example.sipaddy.utils.ResultState
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val dataRepository: DataRepository? = null
) : ViewModel() {
    private val _loginResult = MutableLiveData<ResultState<LoginResponse>>()
    val loginResult: LiveData<ResultState<LoginResponse>> = _loginResult

    private val _registerResult = MutableLiveData<ResultState<RegisterResponse>>()
    val registerResult: LiveData<ResultState<RegisterResponse>> = _registerResult

    val isLoggedIn: LiveData<Boolean> = authRepository.isLoggedIn().asLiveData()

    private val _kelompokTaniResult = MutableLiveData<ResultState<List<KelompokTaniResponse>>>()
    val kelompokTaniResult: LiveData<ResultState<List<KelompokTaniResponse>>> = _kelompokTaniResult

    fun register(
        username: String,
        password: String,
        namaLengkap: String,
        nomorHp: String,
        kelompokTaniId: Int? = null
    ) {
        viewModelScope.launch {
            authRepository.register(username, password, namaLengkap, nomorHp, kelompokTaniId)
                .collect {
                    _registerResult.value = it
                }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            authRepository.login(username, password).collect {
                _loginResult.value = it
            }
        }
    }


    fun loadKelompokTani() {
        viewModelScope.launch {
            dataRepository?.getKelompokTani()?.collect {
                _kelompokTaniResult.value = it
            }
        }
    }

}