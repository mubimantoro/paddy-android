package com.example.sipaddy.presentation.profile.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.CommonResponse
import com.example.sipaddy.data.network.response.UserProfile
import com.example.sipaddy.data.repository.PaddyRepository
import kotlinx.coroutines.launch

class SettingAkunViewModel(private val repository: PaddyRepository) : ViewModel() {
    private val _profileState = MutableLiveData<ResultState<UserProfile>>()
    val profileState: LiveData<ResultState<UserProfile>> = _profileState

    private val _updateState = MutableLiveData<ResultState<CommonResponse>>()
    val updateState: LiveData<ResultState<CommonResponse>> = _updateState

    fun getUserProfile() {
        viewModelScope.launch {
            repository.getUserProfile().collect { result ->
                _profileState.value = result
            }
        }
    }

    fun updateUserProfile(
        username: String,
        namaLengkap: String,
        nomorHp: String?,
        password: String?
    ) {
        if (username.isBlank()) {
            _updateState.value = ResultState.Error("Username tidak boleh kosong")
            return
        }

        if (namaLengkap.isBlank()) {
            _updateState.value = ResultState.Error("Nama lengkap tidak boleh kosong")
            return
        }

        if (!password.isNullOrBlank() && password.length < 6) {
            _updateState.value = ResultState.Error("Password minimal 6 karakter")
            return
        }

        viewModelScope.launch {
            repository.updateUserProfile(
                username = username,
                namaLengkap = namaLengkap,
                nomorHp = nomorHp?.takeIf { it.isNotBlank() },
                password = password?.takeIf { it.isNotBlank() }
            ).collect { result ->
                _updateState.value = result
            }
        }
    }
}