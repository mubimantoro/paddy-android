package com.example.sipaddy.di

import android.content.Context
import com.example.sipaddy.data.api.ApiConfig
import com.example.sipaddy.data.local.TokenPreferences
import com.example.sipaddy.data.local.dataStore
import com.example.sipaddy.data.repository.AuthRepository
import com.example.sipaddy.data.repository.DataRepository

object Injection {

    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService(context)
        val tokenPreferences = TokenPreferences.getInstance(context.dataStore)
        return AuthRepository.getInstance(apiService, tokenPreferences)
    }

    fun provideDataRepository(context: Context): DataRepository {
        val apiService = ApiConfig.getApiService(context)
        return DataRepository.getInstance(apiService)
    }

    fun provideTokenPreferences(context: Context): TokenPreferences {
        return TokenPreferences.getInstance(context.dataStore)
    }
}