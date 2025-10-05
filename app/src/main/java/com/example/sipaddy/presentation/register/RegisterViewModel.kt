package com.example.sipaddy.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.CommonResponse
import com.example.sipaddy.data.repository.PaddyRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: PaddyRepository) : ViewModel() {
    private val _registerResult = MutableLiveData<ResultState<CommonResponse>>()
    val registerResult: LiveData<ResultState<CommonResponse>> = _registerResult

    fun register(username: String, password: String, namaLengkap: String, nomorHp: String, ) {
        viewModelScope.launch {
            repository.register(username, password, namaLengkap, nomorHp, ).collect {
                _registerResult.value = it
            }
        }
    }
}