package com.example.sipaddy.di

import android.content.Context
import com.example.sipaddy.data.api.ApiConfig
import com.example.sipaddy.data.local.TokenPreferences
import com.example.sipaddy.data.pref.UserPreference
import com.example.sipaddy.data.pref.dataStore
import com.example.sipaddy.data.repository.AuthRepository
import com.example.sipaddy.data.repository.DataRepository

object Injection {

    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = TokenPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return AuthRepository.getInstance(apiService, pref)
    }

    fun provideDataRepository(context: Context): DataRepository {
        val pref = TokenPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return DataRepository.getInstance(apiService)
    }
}