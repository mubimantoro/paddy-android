package com.example.sipaddy.data.repository

import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.CommonResponse
import com.example.sipaddy.data.network.response.LoginResponse
import com.example.sipaddy.data.network.retrofit.PaddyApiService
import com.example.sipaddy.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PaddyRepository(
    private val apiService: PaddyApiService,
    private val userPreference: UserPreference
) {
    fun register(
        namaLengkap: String,
        nomorHp: String,
        username: String,
        password: String
    ): Flow<ResultState<CommonResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val response = apiService.register(namaLengkap, nomorHp, username, password)

            if (response.status == "success") {
                emit(ResultState.Success(response))
            }

        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun login(
        username: String,
        password: String
    ): Flow<ResultState<LoginResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val response = apiService.login(username, password)

            if (response.status == "success") {
                emit(ResultState.Success(response))
            }

        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    suspend fun saveSession(username: String, token: String) {
        userPreference.saveSession(username, token)
    }

    fun getSession(): Flow<String> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: PaddyRepository? = null

        fun getInstance(
            apiService: PaddyApiService,
            userPreference: UserPreference
        ): PaddyRepository = instance ?: synchronized(this) {
            instance ?: PaddyRepository(apiService, userPreference)
        }.also { instance = it }
    }
}