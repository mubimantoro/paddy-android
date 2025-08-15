package com.example.sipaddy.presentation.pengaduangangguanpadi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.model.PengaduanForm
import com.example.sipaddy.data.network.response.CommonResponse
import com.example.sipaddy.data.repository.PaddyRepository
import kotlinx.coroutines.launch
import java.io.File

class PengaduanGangguanPadiViewModel(private val repository: PaddyRepository) : ViewModel() {
    private val _result = MutableLiveData<ResultState<CommonResponse>>()
    val result: LiveData<ResultState<CommonResponse>> = _result

    fun createPengaduan(
        form: PengaduanForm,
        photo: File,
    ) {
        viewModelScope.launch {
            repository.createPengaduan(form, photo).collect {
                _result.value = it
            }
        }
    }
}