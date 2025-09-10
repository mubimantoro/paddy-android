package com.example.sipaddy.presentation.home.diagnose

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.PredictResponse
import com.example.sipaddy.data.repository.PaddyRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class DiagnoseViewModel(private val repository: PaddyRepository) : ViewModel() {
    private val _resultPredict = MutableLiveData<ResultState<PredictResponse>>()
    val resultPredict: LiveData<ResultState<PredictResponse>> = _resultPredict

    private val _photoUri = MutableLiveData<Uri?>()
    val photoUri: LiveData<Uri?> get() = _photoUri

    fun setPhotoUri(uri: Uri?) {
        _photoUri.value = uri
    }

    fun predict(photo: MultipartBody.Part) {
        viewModelScope.launch {
            repository.predict(photo).collect {
                _resultPredict.value = it
            }
        }
    }
}