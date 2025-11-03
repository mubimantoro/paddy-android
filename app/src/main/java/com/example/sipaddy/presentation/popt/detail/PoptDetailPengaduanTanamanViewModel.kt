package com.example.sipaddy.presentation.popt.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.PengaduanTanamanDetailResponse
import com.example.sipaddy.data.network.response.VerifikasiPengaduanTanamanItem
import com.example.sipaddy.data.repository.PaddyRepository
import kotlinx.coroutines.launch

class PoptDetailPengaduanTanamanViewModel(private val repository: PaddyRepository) : ViewModel() {

    private var _resultDetail = MutableLiveData<ResultState<PengaduanTanamanDetailResponse>>()
    val resultDetail: LiveData<ResultState<PengaduanTanamanDetailResponse>> = _resultDetail

    private var _resultVerifikasi = MutableLiveData<ResultState<VerifikasiPengaduanTanamanItem>>()
    val resultVerifikasi: LiveData<ResultState<VerifikasiPengaduanTanamanItem>> = _resultVerifikasi

    fun getDetailPengaduanTanaman(id: String) {
        viewModelScope.launch {
            repository.getPengaduanTanamanDetail(id).collect {
                _resultDetail.value = it
            }
        }
    }

    fun verifikasiPengaduanTanaman(pengaduanTanamanId: String) {
        viewModelScope.launch {
            repository.verifikasiPengaduanTanaman(pengaduanTanamanId).collect {
                _resultVerifikasi.value = it
            }
        }
    }
}