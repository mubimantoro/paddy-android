package com.example.sipaddy.presentation.home.predict

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.model.response.PredictResponse
import com.example.sipaddy.data.repository.DataRepository
import com.example.sipaddy.utils.ResultState
import kotlinx.coroutines.launch
import java.io.File

class PredictDiseaseViewModel(
    private val repository: DataRepository
) : ViewModel() {

    private val _predictResult = MutableLiveData<ResultState<PredictResponse>>()
    val predictResult: LiveData<ResultState<PredictResponse>> = _predictResult


    private val _selectedImage = MutableLiveData<File?>()
    val selectedImage: LiveData<File?> = _selectedImage

    fun setSelectedImage(imageFile: File?) {
        _selectedImage.value = imageFile
    }

    fun predictDisease(imageFile: File) {
        viewModelScope.launch {
            repository.predictDisease(imageFile).collect {
                _predictResult.value = it
            }
        }
    }

    fun resetState() {
        _selectedImage.value = null
    }

}