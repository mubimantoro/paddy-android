package com.example.sipaddy.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.model.response.UserData
import com.example.sipaddy.data.repository.AuthRepository
import com.example.sipaddy.data.repository.DataRepository
import kotlinx.coroutines.launch

class ProfilViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> = _userData


    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            repository.getUserData().collect {
                _userData.value = it
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }


}