package com.example.sipaddy.presentation.popt.handle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.model.response.HandlePengaduanTanamanResponse
import com.example.sipaddy.data.model.response.PengaduanTanamanResponse
import com.example.sipaddy.data.repository.DataRepository
import com.example.sipaddy.utils.ResultState
import kotlinx.coroutines.launch

class HandlePengaduanTanamanViewModel(
    private val repository: DataRepository
) : ViewModel() {

    private val _handleResult = MutableLiveData<ResultState<HandlePengaduanTanamanResponse>>()
    val handleResult: LiveData<ResultState<HandlePengaduanTanamanResponse>> = _handleResult

    private val _detailResult = MutableLiveData<ResultState<PengaduanTanamanResponse>>()
    val detailResult: LiveData<ResultState<PengaduanTanamanResponse>> = _detailResult


    fun handlePengaduan(id: Int) {
        viewModelScope.launch {
            repository.handlePengaduanTanaman(id).collect {
                _handleResult.value = it
            }
        }
    }

    fun fetchPengaduanDetail(id: Int) {
        viewModelScope.launch {
            repository.getPengaduanTanamanById(id).collect {
                _detailResult.value = it
            }
        }
    }

}