package com.example.sipaddy.presentation.pengaduantanaman.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.data.model.response.PengaduanTanamanResponse
import com.example.sipaddy.data.repository.DataRepository
import kotlinx.coroutines.launch

class HistoryPengaduanTanamanViewModel(private val repository: DataRepository) : ViewModel() {
    private val _historyResult = MutableLiveData<ResultState<List<PengaduanTanamanResponse>>>()
    val result: LiveData<ResultState<List<PengaduanTanamanResponse>>> = _historyResult

    fun getPengaduanTanamanHistory() {
        viewModelScope.launch {
            repository.getPengaduanHistory().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _historyResult.value = ResultState.Loading
                    }

                    is ResultState.Error -> {
                        _historyResult.value = ResultState.Error(result.message)
                    }

                    is ResultState.Success -> {
                        _historyResult.value = ResultState.Success(result.data)
                    }

                }

            }
        }
    }
}