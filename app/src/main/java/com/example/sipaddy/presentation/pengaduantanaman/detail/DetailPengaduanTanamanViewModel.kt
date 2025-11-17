package com.example.sipaddy.presentation.pengaduantanaman.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.PengaduanTanamanDetailItem
import com.example.sipaddy.data.network.response.PengaduanTanamanDetailResponse
import com.example.sipaddy.data.repository.PaddyRepository
import kotlinx.coroutines.launch

class DetailPengaduanTanamanViewModel(private val repository: PaddyRepository) : ViewModel() {
    private val _detailResult = MutableLiveData<ResultState<PengaduanTanamanDetailResponse>>()
    val detailResult: LiveData<ResultState<PengaduanTanamanDetailResponse>> = _detailResult

    fun getDetailPengaduanTanaman(id: String) {
        viewModelScope.launch {
            repository.getPengaduanTanamanDetail(id).collect {
                _detailResult.value = it
            }
        }
    }

}