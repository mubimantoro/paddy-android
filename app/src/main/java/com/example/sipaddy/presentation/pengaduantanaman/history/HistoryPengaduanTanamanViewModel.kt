package com.example.sipaddy.presentation.pengaduantanaman.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.model.response.PengaduanTanamanResponse
import com.example.sipaddy.data.repository.DataRepository
import com.example.sipaddy.utils.ResultState
import kotlinx.coroutines.launch

class HistoryPengaduanTanamanViewModel(private val repository: DataRepository) : ViewModel() {
    private val _result = MutableLiveData<ResultState<List<PengaduanTanamanResponse>>>()
    val result: LiveData<ResultState<List<PengaduanTanamanResponse>>> = _result

    fun loadPengaduanTanamanHistory() {
        viewModelScope.launch {
            repository.getMyPengaduanTanaman().collect {
                _result.value = it
            }
        }
    }

    fun refreshHistory() {
        loadPengaduanTanamanHistory()
    }
}