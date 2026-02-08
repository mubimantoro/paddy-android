package com.example.sipaddy.presentation.popt.verifikasi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.model.response.DetailPengaduanTanamanResponse
import com.example.sipaddy.data.model.response.PengaduanTanamanResponse
import com.example.sipaddy.data.model.response.VerifikasiPengaduanTanaman
import com.example.sipaddy.data.repository.DataRepository
import com.example.sipaddy.utils.ResultState
import kotlinx.coroutines.launch
import java.io.File

class VerifikasiPengaduanTanamanViewModel(
    private val repository: DataRepository
) : ViewModel() {

    private val _verifikasiResult = MutableLiveData<ResultState<VerifikasiPengaduanTanaman>>()
    val verifikasiresult: LiveData<ResultState<VerifikasiPengaduanTanaman>> = _verifikasiResult

    private val _detailResult = MutableLiveData<ResultState<DetailPengaduanTanamanResponse>>()
    val detailResult: LiveData<ResultState<DetailPengaduanTanamanResponse>> = _detailResult

    fun fetchPengaduanDetail(id: Int) {
        viewModelScope.launch {
            repository.getPengaduanTanamanById(id).collect {
                _detailResult.value = it
            }
        }
    }

    fun submitVerikasi(
        pengaduanId: Int,
        latitude: String,
        longitude: String,
        catatan: String,
        fotoFile: File?
    ) {
        viewModelScope.launch {
            repository.submitVerifikasi(
                pengaduanId,
                latitude,
                longitude,
                catatan,
                fotoFile
            ).collect {
                _verifikasiResult.value = it
            }
        }
    }

}