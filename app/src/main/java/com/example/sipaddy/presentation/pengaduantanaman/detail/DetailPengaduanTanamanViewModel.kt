package com.example.sipaddy.presentation.pengaduantanaman.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.model.response.DetailPengaduanTanamanResponse
import com.example.sipaddy.data.repository.DataRepository
import com.example.sipaddy.utils.ResultState
import kotlinx.coroutines.launch

class DetailPengaduanTanamanViewModel(private val repository: DataRepository) : ViewModel() {
    private val _detailResult = MutableLiveData<ResultState<DetailPengaduanTanamanResponse>>()
    val detailResult: LiveData<ResultState<DetailPengaduanTanamanResponse>> = _detailResult

    fun getDetailPengaduanTanaman(id: Int) {
        viewModelScope.launch {
            repository.getDetailPengaduanTanaman(id).collect {
                _detailResult.value = it
            }
        }
    }

}