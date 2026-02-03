package com.example.sipaddy.presentation.popt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sipaddy.data.model.response.AssignedPengaduanTanamanResponse
import com.example.sipaddy.data.repository.DataRepository
import com.example.sipaddy.utils.ResultState
import kotlinx.coroutines.launch

class AssignedPengaduanTanamanViewModel(
    private val repository: DataRepository
): ViewModel() {
    private var _assignedResult = MutableLiveData<ResultState<List<AssignedPengaduanTanamanResponse>>>()
    val assignedResult: LiveData<ResultState<List<AssignedPengaduanTanamanResponse>>> = _assignedResult

    init {
        loadAssignedPengaduanTanaman()
    }

    fun loadAssignedPengaduanTanaman() {
        viewModelScope.launch {
            repository.getAssignedPengaduanTanaman().collect {
                _assignedResult.value = it
            }
        }
    }

    fun refreshPengaduanTanaman() {
        loadAssignedPengaduanTanaman()
    }

}