package com.example.sipaddy.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sipaddy.data.repository.AuthRepository
import com.example.sipaddy.data.repository.DataRepository
import com.example.sipaddy.di.Injection
import com.example.sipaddy.presentation.auth.AuthViewModel
import com.example.sipaddy.presentation.auth.login.LoginViewModel
import com.example.sipaddy.presentation.auth.register.RegisterViewModel
import com.example.sipaddy.presentation.history.HistoryPredictionViewModel
import com.example.sipaddy.presentation.home.diagnose.result.ResultViewModel
import com.example.sipaddy.presentation.home.predict.PredictDiseaseViewModel
import com.example.sipaddy.presentation.pengaduantanaman.PengaduanTanamanViewModel
import com.example.sipaddy.presentation.pengaduantanaman.history.HistoryPengaduanTanamanViewModel

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val dataRepository: DataRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository, dataRepository) as T
            }

            modelClass.isAssignableFrom(PengaduanTanamanViewModel::class.java) -> {
                PengaduanTanamanViewModel(dataRepository) as T
            }

            modelClass.isAssignableFrom(HistoryPengaduanTanamanViewModel::class.java) -> {
                HistoryPengaduanTanamanViewModel(dataRepository) as T
            }

            modelClass.isAssignableFrom(HistoryPredictionViewModel::class.java) -> {
                HistoryPredictionViewModel(dataRepository) as T
            }

            modelClass.isAssignableFrom(PredictDiseaseViewModel::class.java) -> {
                PredictDiseaseViewModel(dataRepository) as T
            }

            modelClass.isAssignableFrom(ResultViewModel::class.java) -> {
                ResultViewModel(dataRepository) as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(authRepository, dataRepository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val authRepository = Injection.provideAuthRepository(context)
                val dataRepository = Injection.provideDataRepository(context)
                ViewModelFactory(authRepository, dataRepository).also {
                    INSTANCE = it
                }
            }
        }
    }
}