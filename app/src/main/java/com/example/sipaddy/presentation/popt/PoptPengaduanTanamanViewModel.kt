package com.example.sipaddy.presentation.popt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.data.network.response.PengaduanTanamanResponse
import com.example.sipaddy.data.repository.DataRepository
import kotlinx.coroutines.launch

class PoptPengaduanTanamanViewModel(private val repository: DataRepository) : ViewModel() {
    private var _result = MutableLiveData<ResultState<PengaduanTanamanResponse>>()
    val result: LiveData<ResultState<PengaduanTanamanResponse>> = _result

    fun getPengaduanTanaman() {
        viewModelScope.launch {
            repository.getPengaduanTanamanHistory().collect {
                _result.value = it
            }
        }
    }
}