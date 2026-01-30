package com.example.sipaddy.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.model.response.PredictResponse
import com.example.sipaddy.data.model.response.UserResponse
import com.example.sipaddy.data.repository.AuthRepository
import com.example.sipaddy.utils.ResultState
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _userResult = MutableLiveData<ResultState<UserResponse>>()
    val userResult: LiveData<ResultState<UserResponse>> = _userResult

    private val _recentPredictions = MutableLiveData<ResultState<List<PredictResponse>>>()
    val recentPredictions: LiveData<ResultState<List<PredictResponse>>> = _recentPredictions

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            authRepository.getUserData().collect {
                _userResult.value = it
            }
        }
    }

    fun loadRecentPredictions() {
    }
}