package com.example.sipaddy.data.repository

import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.model.PengaduanForm
import com.example.sipaddy.data.network.response.CommonResponse
import com.example.sipaddy.data.network.response.HistoryResponse
import com.example.sipaddy.data.network.response.LoginResponse
import com.example.sipaddy.data.network.response.PredictResponse
import com.example.sipaddy.data.network.retrofit.ApiConfig
import com.example.sipaddy.data.network.retrofit.PaddyApiService
import com.example.sipaddy.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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

    fun createPengaduan(
        form: PengaduanForm,
        photoFile: File
    ): Flow<ResultState<CommonResponse>> = flow {
        emit(ResultState.Loading)
        val rbText = "text/plain".toMediaType()
        val rPhotoFile = photoFile.asRequestBody("image/jpeg".toMediaType())
        val kelompokTani = form.kelompokTani.toRequestBody(rbText)
        val alamat = form.alamat.toRequestBody(rbText)
        val kecamatan = form.kecamatan.toRequestBody(rbText)
        val kabupaten = form.kabupaten.toRequestBody(rbText)
        val deskripsi = form.deskripsi.toRequestBody(rbText)
        val latitude = form.latitude?.toString()?.toRequestBody(rbText)
        val longitude = form.longitude?.toString()?.toRequestBody(rbText)
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            photoFile.name,
            rPhotoFile
        )
        try {
            val token = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(token)
            val response = apiService.createPengaduan(
                token,
                kelompokTani = kelompokTani,
                alamat,
                kecamatan,
                kabupaten,
                deskripsi,
                latitude,
                longitude,
                multipartBody
            )

            if (response.status == "success") {
                emit(ResultState.Success(response))
            }

        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }

    }

    fun predict(photo: MultipartBody.Part): Flow<ResultState<PredictResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val token = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(token)
            val response = apiService.predict(photo)
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