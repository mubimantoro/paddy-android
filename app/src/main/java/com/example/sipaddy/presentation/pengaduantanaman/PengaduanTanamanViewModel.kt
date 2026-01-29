package com.example.sipaddy.presentation.pengaduantanaman

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.data.model.response.KecamatanResponse
import com.example.sipaddy.data.model.response.KelompokTaniResponse
import com.example.sipaddy.data.model.response.PengaduanTanamanResponse
import com.example.sipaddy.data.repository.DataRepository
import kotlinx.coroutines.launch
import java.io.File

class PengaduanTanamanViewModel(private val repository: DataRepository) : ViewModel() {
    private val _result = MutableLiveData<ResultState<PengaduanTanamanResponse>>()
    val result: LiveData<ResultState<PengaduanTanamanResponse>> = _result

    private val _kelompokTaniResult = MutableLiveData<ResultState<List<KelompokTaniResponse>>>()
    val kelompokTaniResult: LiveData<ResultState<List<KelompokTaniResponse>>> = _kelompokTaniResult

    private val _kecamatanResult = MutableLiveData<ResultState<List<KecamatanResponse>>>()
    val kecamatanResult: LiveData<ResultState<List<KecamatanResponse>>> = _kecamatanResult

    fun loadKelompokTani() {
        viewModelScope.launch {
            repository.getKelompokTani().collect {
                _kelompokTaniResult.value = it
            }
        }
    }

    fun getKecamatan() {
        viewModelScope.launch {
            repository.getKecamatan().collect {
                _kecamatanResult.value = it
            }
        }
    }

    fun submitPengaduan(
        kelompokTaniId: Int,
        kecamatanId: Int,
        deskripsi: String,
        latitude: String,
        longitude: String,
        imageFile: File?
    ) {

        viewModelScope.launch {
            repository.createPengaduanTanaman(
                kelompokTaniId,
                kecamatanId,
                deskripsi,
                latitude,
                longitude,
                imageFile
            ).collect {
                _result.value = it
            }
        }
    }
}