package com.example.sipaddy.presentation.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.utils.ResultState
import com.example.sipaddy.data.model.response.PredictResponse
import com.example.sipaddy.data.repository.DataRepository
import kotlinx.coroutines.launch

class HistoryPredictionViewModel(
    private val repository: DataRepository
) : ViewModel() {
    private val _result = MutableLiveData<ResultState<List<PredictResponse>>>()
    val result: LiveData<ResultState<List<PredictResponse>>> =
        _result

    fun loadPrediction() {
        viewModelScope.launch {
            repository.getMyPrediction().collect {
                _result.value = it
            }
        }
    }

    fun refreshHistory() {
        loadPrediction()
    }
}