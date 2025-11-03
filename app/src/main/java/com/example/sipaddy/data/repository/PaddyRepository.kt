package com.example.sipaddy.data.repository

import com.example.sipaddy.data.ResultState
import com.example.sipaddy.data.network.response.CommonResponse
import com.example.sipaddy.data.network.response.DiseaseResponse
import com.example.sipaddy.data.network.response.LoginResponse
import com.example.sipaddy.data.network.response.PengaduanTanamanResponse
import com.example.sipaddy.data.network.response.PredictResponse
import com.example.sipaddy.data.network.retrofit.ApiConfig
import com.example.sipaddy.data.network.retrofit.PaddyApiService
import com.example.sipaddy.data.pref.UserPreference
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class PaddyRepository(
    private val userPreference: UserPreference
) {

    private suspend fun getApiService(): PaddyApiService {
        val token = userPreference.getSession().first()
        return ApiConfig.getApiService { token }
    }

    private fun isTokenExpired(e: HttpException): Boolean {
        if (e.code() != 401) return false

        return try {
            val errorBody = e.response()?.errorBody()?.string() ?: return false
            val jsonObject = JSONObject(errorBody)
            val message = jsonObject.optString("message", "")
            message.contains("Token maximum age exceeded", ignoreCase = true) ||
                    message.contains("Token expired", ignoreCase = true)
        } catch (ex: Exception) {
            false
        }
    }


    private fun getErrorMessage(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            val jsonObject = JSONObject(errorBody ?: "")
            jsonObject.optString("message", "Server error: ${e.code()}")
        } catch (ex: Exception) {
            "Server error: ${e.code()}"
        }
    }

    fun register(
        username: String,
        password: String,
        namaLengkap: String,
        nomorHp: String,
    ): Flow<ResultState<CommonResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val apiService = ApiConfig.getApiService()
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
            val apiService = ApiConfig.getApiService()
            val response = apiService.login(username, password)

            if (response.status == "success") {
                emit(ResultState.Success(response))
            }

        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun getHistory(): Flow<ResultState<DiseaseResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val apiService = getApiService()
            val response = apiService.getHistory()
            emit(ResultState.Success(response))

        } catch (e: HttpException) {
            if (isTokenExpired(e)) {
                emit(
                    ResultState.Error(
                        "Sesi Anda telah berakhir. Silakan login kembali.",
                        isTokenExpired = true
                    )
                )
            } else {
                val errorMessage = when (e.code()) {
                    500 -> "Server sedang bermasalah"
                    else -> getErrorMessage(e)
                }
                emit(ResultState.Error(errorMessage))
            }
        } catch (e: IOException) {
            emit(ResultState.Error("Tidak ada koneksi internet"))

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
        val multipartBody = MultipartBody.Part.createFormData(
            "image",
            image.name,
            rbImage
        )

        try {
            val apiService = getApiService()
            val response = apiService.createPengaduanTanaman(
                kelompokTani.toRequestBody(rbText),
                alamat.toRequestBody(rbText),
                kecamatan.toRequestBody(rbText),
                kabupaten.toRequestBody(rbText),
                deskripsi.toRequestBody(rbText),
                latitude.toString().toRequestBody(rbText),
                longitude.toString().toRequestBody(rbText),
                multipartBody
            )

            if (response.status == "success") {
                emit(ResultState.Success(response))
            } else {
                emit(ResultState.Error(response.message))
            }

        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }

    }

    fun getPengaduanTanamanHistory(): Flow<ResultState<PengaduanTanamanResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val apiService = getApiService()
            val response = apiService.getPengaduanTanamanHistory()
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun predict(image: MultipartBody.Part): Flow<ResultState<PredictResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val apiService = getApiService()
            val response = apiService.predict(image)
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }


    suspend fun saveSession(username: String, token: String, role: String) {
        userPreference.saveSession(username, token, role)
    }

    fun getSession(): Flow<String> {
        return userPreference.getSession()
    }

    fun getRole(): Flow<String> {
        return userPreference.getRole()
    }

    fun getUsername(): Flow<String> {
        return userPreference.getUsername()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: PaddyRepository? = null

        fun getInstance(
            userPreference: UserPreference
        ): PaddyRepository = instance ?: synchronized(this) {
            instance ?: PaddyRepository(userPreference)
        }.also { instance = it }
    }
}