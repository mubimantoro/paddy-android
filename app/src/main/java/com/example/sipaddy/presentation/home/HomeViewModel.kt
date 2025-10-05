package com.example.sipaddy.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.sipaddy.data.repository.PaddyRepository

class HomeViewModel(private val repository: PaddyRepository) : ViewModel() {
    fun getSession(): LiveData<String> {
        return  repository.getSession().asLiveData()
    }
}