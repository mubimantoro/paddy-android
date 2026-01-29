package com.example.sipaddy.data.repository

import com.example.sipaddy.data.api.ApiService
import com.example.sipaddy.data.local.TokenPreferences
import com.example.sipaddy.data.model.request.LoginRequest
import com.example.sipaddy.data.model.request.RegisterRequest
import com.example.sipaddy.data.model.response.LoginResponse
import com.example.sipaddy.data.model.response.RegisterResponse
import com.example.sipaddy.data.model.response.UserResponse
import com.example.sipaddy.utils.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val tokenPreferences: TokenPreferences
) {

    fun register(
        username: String,
        password: String,
        namaLengkap: String,
        nomorHp: String,
        kelompokTaniId: Int? = null
    ): Flow<ResultState<RegisterResponse>> = flow {
        try {
            emit(ResultState.Loading)

            val request = RegisterRequest(
                username = username,
                password = password,
                namaLengkap = namaLengkap,
                nomorHp = nomorHp,
                kelompokTaniId = kelompokTaniId
            )

            val response = apiService.register(request)

            if (response.code in 200..299 && response.data != null) {
                emit(ResultState.Success(response.data))
            } else {
                emit(ResultState.Error(response.message, response.code))
            }


        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Terjadi kesalahan"))
        }

    }.flowOn(Dispatchers.IO)

    suspend fun login(username: String, password: String): Flow<ResultState<LoginResponse>> = flow {
        try {
            emit(ResultState.Loading)

            val request = LoginRequest(
                username = username,
                password = password
            )

            val response = apiService.login(request)

            if (response.code in 200..299 && response.data != null) {
                saveLoginSession(response.data)
                emit(ResultState.Success(response.data))
            } else {
                emit(ResultState.Error(response.message, response.code))
            }

        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Terjadi Kesalahan"))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun saveLoginSession(data: LoginResponse) {
        val role = data.user.roles.firstOrNull() ?: "user"

        tokenPreferences.saveLoginSession(
            accessToken = data.accessToken,
            refreshToken = data.refreshToken,
            userId = data.user.id,
            username = data.user.username,
            namaLengkap = data.user.namaLengkap,
            role = role
        )
    }


    suspend fun logout() {
        tokenPreferences.clearSession()
    }

    fun isLoggedIn(): Flow<Boolean> {
        return tokenPreferences.isLoggedIn()
    }

    fun getUserData(): Flow<ResultState<UserResponse>> = flow {
        try {
            emit(ResultState.Loading)

            tokenPreferences.getUserData().first()?.let { userData ->
                emit(ResultState.Success(userData))
            } ?: run {
                emit(ResultState.Error("Data User tidak ditemukan"))
            }

        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Terjadi kesalahan"))
        }
    }.flowOn(Dispatchers.IO)

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(
            apiService: ApiService,
            tokenPreferences: TokenPreferences
        ): AuthRepository = instance ?: synchronized(this) {
            instance ?: AuthRepository(apiService, tokenPreferences)
        }.also { instance = it }
    }
}