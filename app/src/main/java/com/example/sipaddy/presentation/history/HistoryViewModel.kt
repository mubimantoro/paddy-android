package com.example.sipaddy.presentation.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.DiseaseResponse
import com.example.sipaddy.data.repository.PaddyRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: PaddyRepository) : ViewModel() {
    private val _resultHistory = MutableLiveData<ResultState<DiseaseResponse>>()
    val resultHistory: LiveData<ResultState<DiseaseResponse>> = _resultHistory

    fun getHistory() {
        viewModelScope.launch {
            repository.getHistory().collect {
                _resultHistory.value = it
            }

        }
    }

    suspend fun logout() {
        repository.logout()
    }
}