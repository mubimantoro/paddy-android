package com.example.sipaddy.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sipaddy.data.pref.UserPreference
import com.example.sipaddy.data.pref.dataStore
import com.example.sipaddy.data.repository.PaddyRepository
import com.example.sipaddy.presentation.history.HistoryViewModel
import com.example.sipaddy.presentation.home.HomeViewModel
import com.example.sipaddy.presentation.home.diagnose.DiagnoseViewModel
import com.example.sipaddy.presentation.login.LoginViewModel
import com.example.sipaddy.presentation.pengaduantanaman.PengaduanTanamanViewModel
import com.example.sipaddy.presentation.pengaduantanaman.detail.DetailPengaduanTanamanViewModel
import com.example.sipaddy.presentation.pengaduantanaman.history.HistoryPengaduanTanamanViewModel
import com.example.sipaddy.presentation.popt.PoptPengaduanTanamanViewModel
import com.example.sipaddy.presentation.popt.detail.PoptDetailPengaduanTanamanViewModel
import com.example.sipaddy.presentation.profile.ProfilViewModel
import com.example.sipaddy.presentation.register.RegisterViewModel

class ViewModelFactory(
    private val context: Context
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val pref = UserPreference.getInstance(context.dataStore)
        val repository = PaddyRepository.getInstance(pref)

        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }

            modelClass.isAssignableFrom(DiagnoseViewModel::class.java) -> {
                DiagnoseViewModel(repository) as T
            }

            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ProfilViewModel::class.java) -> {
                ProfilViewModel(repository) as T
            }

            modelClass.isAssignableFrom(PengaduanTanamanViewModel::class.java) -> {
                PengaduanTanamanViewModel(repository) as T
            }

            modelClass.isAssignableFrom(HistoryPengaduanTanamanViewModel::class.java) -> {
                HistoryPengaduanTanamanViewModel(repository) as T
            }

            modelClass.isAssignableFrom(PoptPengaduanTanamanViewModel::class.java) -> {
                PoptPengaduanTanamanViewModel(repository) as T
            }

            modelClass.isAssignableFrom(PoptDetailPengaduanTanamanViewModel::class.java) -> {
                PoptDetailPengaduanTanamanViewModel(repository) as T
            }

            modelClass.isAssignableFrom(DetailPengaduanTanamanViewModel::class.java) -> {
                DetailPengaduanTanamanViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}