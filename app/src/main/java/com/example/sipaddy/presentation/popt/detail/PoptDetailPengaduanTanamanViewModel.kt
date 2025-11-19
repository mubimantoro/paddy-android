package com.example.sipaddy.presentation.popt.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.PengaduanTanamanDetailItem
import com.example.sipaddy.data.network.response.PengaduanTanamanDetailResponse
import com.example.sipaddy.data.repository.PaddyRepository
import kotlinx.coroutines.launch

class PoptDetailPengaduanTanamanViewModel(private val repository: PaddyRepository) : ViewModel() {

    private var _detailResult = MutableLiveData<ResultState<PengaduanTanamanDetailResponse>>()
    val detailResult: LiveData<ResultState<PengaduanTanamanDetailResponse>> = _detailResult

    private var _verifikasiResult = MutableLiveData<ResultState<PengaduanTanamanDetailItem>>()
    val verifikasiResult: LiveData<ResultState<PengaduanTanamanDetailItem>> = _verifikasiResult

    fun getDetailPengaduanTanaman(id: String) {
        viewModelScope.launch {
            repository.getPengaduanTanamanDetail(id).collect {
                _detailResult.value = it
            }
        }
    }

    fun verifikasiPengaduanTanaman(id: String, catatanPopt: String?) {
        viewModelScope.launch {
            repository.verifikasiPengaduanTanaman(id, catatanPopt).collect {
                _verifikasiResult.value = it
            }
        }
    }
}