package com.example.sipaddy.presentation.pengaduantanaman.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sipaddy.data.network.response.PengaduanTanamanItem
import com.example.sipaddy.data.repository.PaddyRepository

class DetailPengaduanTanamanViewModel(private val repository: PaddyRepository) : ViewModel() {
    private val _detail = MutableLiveData<PengaduanTanamanItem>()
    val detail: LiveData<PengaduanTanamanItem> = _detail

    fun setPengaduanTanamanDetail(item: PengaduanTanamanItem) {
        _detail.value = item
    }

}