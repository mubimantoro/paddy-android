package com.example.sipaddy.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.repository.DataRepository
import kotlinx.coroutines.launch

class ProfilViewModel(private val repository: DataRepository) : ViewModel() {
    fun logout(callback: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            callback()
        }
    }

    fun getUsername(): LiveData<String> {
        return repository.getUsername().asLiveData()
    }

    fun getNamaLengkap(): LiveData<String> {
        return repository.getNamaLengkap().asLiveData()
    }
}