package com.example.sipaddy.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.repository.PaddyRepository
import kotlinx.coroutines.launch

class ProfilViewModel(private val repository: PaddyRepository) : ViewModel() {
    fun logout(callback: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            callback()
        }
    }
}