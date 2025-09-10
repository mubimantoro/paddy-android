package com.example.sipaddy.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sipaddy.di.Injection
import com.example.sipaddy.presentation.home.diagnose.DiagnoseViewModel
import com.example.sipaddy.presentation.login.LoginViewModel
import com.example.sipaddy.presentation.pengaduangangguanpadi.PengaduanGangguanPadiViewModel
import com.example.sipaddy.presentation.register.RegisterViewModel

class ViewModelFactory(
    private val context: Context
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(Injection.provideRepository(context)) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(Injection.provideRepository(context)) as T
            }

            modelClass.isAssignableFrom(PengaduanGangguanPadiViewModel::class.java) -> {
                PengaduanGangguanPadiViewModel(Injection.provideRepository(context)) as T
            }

            modelClass.isAssignableFrom(DiagnoseViewModel::class.java) -> {
                DiagnoseViewModel(Injection.provideRepository(context)) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}