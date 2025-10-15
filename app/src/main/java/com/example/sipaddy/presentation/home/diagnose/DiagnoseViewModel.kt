package com.example.sipaddy.presentation.home.diagnose

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.PredictResponse
import com.example.sipaddy.data.repository.PaddyRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class DiagnoseViewModel(private val repository: PaddyRepository) : ViewModel() {
    private val _resultPredict = MutableSharedFlow<ResultState<PredictResponse>>()
    val resultPredict: SharedFlow<ResultState<PredictResponse>> = _resultPredict.asSharedFlow()

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> get() = _imageUri

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    fun predict(image: MultipartBody.Part) {
        viewModelScope.launch {
            repository.predict(image).collect {
                _resultPredict.emit(it)
            }
        }
    }
}