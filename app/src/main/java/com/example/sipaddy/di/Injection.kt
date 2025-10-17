package com.example.sipaddy.di

import android.content.Context
import com.example.sipaddy.data.network.retrofit.ApiConfig
import com.example.sipaddy.data.pref.UserPreference
import com.example.sipaddy.data.pref.dataStore
import com.example.sipaddy.data.repository.PaddyRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): PaddyRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return PaddyRepository.getInstance(pref)
    }
}