package com.example.sipaddy.presentation.pengaduantanaman

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.CommonResponse
import com.example.sipaddy.data.repository.PaddyRepository
import kotlinx.coroutines.launch
import java.io.File

class PengaduanTanamanViewModel(private val repository: PaddyRepository) : ViewModel() {
    private val _result = MutableLiveData<ResultState<CommonResponse>>()
    val result: LiveData<ResultState<CommonResponse>> = _result

    fun createPengaduanTanaman(
        kelompokTani: String,
        alamat: String,
        kecamatan: String,
        kabupaten: String,
        deskripsi: String,
        latitude: String,
        longitude: String,
        photo: File
    ) {


        viewModelScope.launch {
            repository.createPengaduanTanaman(
                kelompokTani,
                alamat,
                kecamatan,
                kabupaten,
                deskripsi,
                latitude.toDoubleOrNull() ?: 0.0,
                longitude.toDoubleOrNull() ?: 0.0,
                photo
            ).collect {
                _result.value = it
            }
        }
    }
}