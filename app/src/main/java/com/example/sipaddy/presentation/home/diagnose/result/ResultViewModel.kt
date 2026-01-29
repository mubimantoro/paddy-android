package com.example.sipaddy.presentation.home.diagnose.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.model.response.PredictResponse
import com.example.sipaddy.data.repository.DataRepository
import com.example.sipaddy.utils.ResultState
import kotlinx.coroutines.launch

class ResultViewModel(
    private val repository: DataRepository
) : ViewModel() {

    private val _predictionResult = MutableLiveData<ResultState<PredictResponse>>()
    val predictionResult: LiveData<ResultState<PredictResponse>> = _predictionResult

    fun loadPredictionResult(predictionId: String) {
        viewModelScope.launch {
            repository.getPredictionById(predictionId).collect {
                _predictionResult.value = it
            }
        }
    }
}