package com.example.sipaddy.data.repository

import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.CommonResponse
import com.example.sipaddy.data.network.response.HistoryResponse
import com.example.sipaddy.data.network.response.LoginResponse
import com.example.sipaddy.data.network.response.PredictResponse
import com.example.sipaddy.data.network.retrofit.ApiConfig
import com.example.sipaddy.data.network.retrofit.PaddyApiService
import com.example.sipaddy.data.pref.UserPreference
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class PaddyRepository(
    private val apiService: PaddyApiService,
    private val userPreference: UserPreference
) {
    fun register(
        username: String,
        password: String,
        namaLengkap: String,
        nomorHp: String,
    ): Flow<ResultState<CommonResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val response = apiService.register(username, password, namaLengkap, nomorHp)

            if (response.status == "success") {
                emit(ResultState.Success(response))
            }

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, CommonResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
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

    fun createPengaduanTanaman(
        kelompokTani: String,
        alamat: String,
        kecamatan: String,
        kabupaten: String,
        deskripsi: String,
        latitude: Double,
        longitude: Double,
        image: File
    ): Flow<ResultState<CommonResponse>> = flow {
        emit(ResultState.Loading)
        val rbText = "text/plain".toMediaType()
        val rbImage = image.asRequestBody("image/jpeg".toMediaType())
        val kelompokTaniField = kelompokTani.toRequestBody(rbText)
        val alamatField = alamat.toRequestBody(rbText)
        val kecamatanField = kecamatan.toRequestBody(rbText)
        val kabupatenField = kabupaten.toRequestBody(rbText)
        val deskripsiField = deskripsi.toRequestBody(rbText)
        val latitudeField = latitude.toString().toRequestBody(rbText)
        val longitudeField = longitude.toString().toRequestBody(rbText)
        val multipartBody = MultipartBody.Part.createFormData(
            "image",
            image.name,
            rbImage
        )
        try {
            val token = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(token)
            val response = apiService.createPengaduanTanaman(
                kelompokTaniField,
                alamatField,
                kecamatanField,
                kabupatenField,
                deskripsiField,
                latitudeField,
                longitudeField,
                multipartBody
            )

            if (response.status == "success") {
                emit(ResultState.Success(response))
            }

        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }

    }

    fun predict(image: MultipartBody.Part): Flow<ResultState<PredictResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val token = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(token)
            val response = apiService.predict(image)
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun getHistory(): Flow<ResultState<HistoryResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val token = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(token)
            val response = apiService.getHistory()
            emit(ResultState.Success(response))
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