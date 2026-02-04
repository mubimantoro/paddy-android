package com.example.sipaddy.presentation.popt.verifikasi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.model.response.PengaduanTanamanResponse
import com.example.sipaddy.data.repository.DataRepository
import com.example.sipaddy.utils.ResultState
import kotlinx.coroutines.launch
import java.io.File

class VerifikasiPengaduanTanamanViewModel(
    private val repository: DataRepository
) : ViewModel() {

    private val _verifikasiResult = MutableLiveData<ResultState<PengaduanTanamanResponse>>()
    val verifikasiresult: LiveData<ResultState<PengaduanTanamanResponse>> = _verifikasiResult

    private val _detailResult = MutableLiveData<ResultState<PengaduanTanamanResponse>>()
    val detailResult: LiveData<ResultState<PengaduanTanamanResponse>> = _detailResult

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