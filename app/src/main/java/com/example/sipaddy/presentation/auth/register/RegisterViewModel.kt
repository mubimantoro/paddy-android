package com.example.sipaddy.presentation.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.model.response.KelompokTaniResponse
import com.example.sipaddy.data.model.response.RegisterResponse
import com.example.sipaddy.data.repository.AuthRepository
import com.example.sipaddy.data.repository.DataRepository
import com.example.sipaddy.utils.ResultState
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val dataRepository: DataRepository
) : ViewModel() {
    private val _registerResult = MutableLiveData<ResultState<RegisterResponse>>()
    val registerResult: LiveData<ResultState<RegisterResponse>> = _registerResult

    private val _kelompokTaniResult = MutableLiveData<ResultState<List<KelompokTaniResponse>>>()
    val kelompokTaniResult: LiveData<ResultState<List<KelompokTaniResponse>>> = _kelompokTaniResult


    fun loadKelompokTani() {
        viewModelScope.launch {
            dataRepository.getKelompokTani().collect { result ->
                _kelompokTaniResult.value = result
            }
        }
    }


    fun register(
        username: String,
        password: String,
        namaLengkap: String,
        nomorHp: String,
        kelompokTaniId: Int? = null
    ) {
        when {
            username.isEmpty() -> {
                _registerResult.value = ResultState.Error("Username tidak boleh kosong")
                return
            }

            username.length < 3 -> {
                _registerResult.value = ResultState.Error("Username minimal 3 karakter")
                return
            }

            password.isEmpty() -> {
                _registerResult.value = ResultState.Error("Password tidak boleh kosong")
                return
            }

            password.length < 6 -> {
                _registerResult.value = ResultState.Error("Password minimal 6 karakter")
                return
            }

            namaLengkap.isEmpty() -> {
                _registerResult.value = ResultState.Error("Nama lengkap tidak boleh kosong")
                return
            }

            nomorHp.isEmpty() -> {
                _registerResult.value = ResultState.Error("Nomor HP tidak boleh kosong")
                return
            }

            !nomorHp.matches(Regex("^[0-9]{10,13}$")) -> {
                _registerResult.value = ResultState.Error("Nomor HP tidak valid (10-13 digit)")
                return
            }

            kelompokTaniId == 0 -> {
                _registerResult.value = ResultState.Error("Pilih kelompok tani terlebih dahulu")
                return
            }
        }

        viewModelScope.launch {
            authRepository.register(
                username = username,
                password = password,
                namaLengkap = namaLengkap,
                nomorHp = nomorHp,
                kelompokTaniId = kelompokTaniId
            ).collect { result ->
                _registerResult.value = result
            }
        }
    }
}