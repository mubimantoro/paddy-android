package com.example.sipaddy.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.model.response.UserData
import com.example.sipaddy.data.repository.AuthRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _userRole = MutableLiveData<UserData?>()
    val userRole: LiveData<UserData?> = _userRole

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            authRepository.getUserData().collect {
                _userRole.value = it
            }
        }
    }
}