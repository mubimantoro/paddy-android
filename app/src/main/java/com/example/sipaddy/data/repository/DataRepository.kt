package com.example.sipaddy.data.repository

import com.example.sipaddy.data.api.ApiService
import com.example.sipaddy.data.model.response.DetailPengaduanTanamanResponse
import com.example.sipaddy.data.model.response.KecamatanResponse
import com.example.sipaddy.data.model.response.KelompokTaniResponse
import com.example.sipaddy.data.model.response.PengaduanTanamanResponse
import com.example.sipaddy.data.model.response.PredictResponse
import com.example.sipaddy.utils.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class DataRepository private constructor(
    private val apiService: ApiService
) {

    fun getKelompokTani(): Flow<ResultState<List<KelompokTaniResponse>>> = flow {
        try {
            emit(ResultState.Loading)
            val response = apiService.getkelompokTani()

            if (response.code in 200..299 && response.data != null) {
                emit(ResultState.Success(response.data))
            } else {
                emit(ResultState.Error(response.message, response.code))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Terjadi kesalahan"))
        }
    }


    fun getKecamatan(): Flow<ResultState<List<KecamatanResponse>>> = flow {
        try {
            emit(ResultState.Loading)
            val response = apiService.getKecamatan()

            if (response.code in 200..299 && response.data != null) {
                emit(ResultState.Success(response.data))
            } else {
                emit(ResultState.Error(response.message, response.code))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun createPengaduanTanaman(
        kelompokTaniId: Int,
        kecamatanId: Int,
        deskripsi: String,
        latitude: String,
        longitude: String,
        imageFile: File?
    ): Flow<ResultState<PengaduanTanamanResponse>> = flow {
        try {
            emit(ResultState.Loading)

            val kelompokTaniIdBody =
                kelompokTaniId.toString().toRequestBody("text/plain".toMediaType())
            val kecamatanIdBody = kecamatanId.toString().toRequestBody("text/plain".toMediaType())
            val deskripsiBody = deskripsi.toRequestBody("text/plain".toMediaType())
            val latitudeBody = latitude.toRequestBody("text/plain".toMediaType())
            val longitudeBody = longitude.toRequestBody("text/plain".toMediaType())

            val imagePart = imageFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaType())
                MultipartBody.Part.createFormData("image", it.name, requestFile)
            }

            val response = apiService.createPengaduanTanaman(
                kelompokTaniIdBody,
                kecamatanIdBody,
                deskripsiBody,
                latitudeBody,
                longitudeBody,
                imagePart
            )

            if (response.code in 200..299 && response.data != null) {
                emit(ResultState.Success(response.data))
            } else {
                emit(ResultState.Error(response.message))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Terjadi kesalahan"))
        }
    }.flowOn(Dispatchers.IO)

    fun getDetailPengaduanTanaman(id: Int): Flow<ResultState<DetailPengaduanTanamanResponse>> =
        flow {
            try {
                emit(ResultState.Loading)
                val response = apiService.getDetailPengaduanTanaman(id = id)

                if (response.code in 200..299 && response.data != null) {
                    emit(ResultState.Success(response.data))
                } else {
                    emit(ResultState.Error(response.message))
                }
            } catch (e: Exception) {
                emit(ResultState.Error(e.message ?: "Terjadi kesalahan"))
            }
        }

    fun predictDisease(imageFile: File): Flow<ResultState<PredictResponse>> = flow {
        try {
            emit(ResultState.Loading)
            val requestFile = imageFile.asRequestBody("image/*".toMediaType())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            val response = apiService.predictDisease(imagePart)

            if (response.code in 200..299 && response.data != null) {
                emit(ResultState.Success(response.data))
            } else {
                emit(ResultState.Error(response.message, response.code))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Terjadi kesalahan"))
        }
    }.flowOn(Dispatchers.IO)

    fun getMyPrediction(): Flow<ResultState<List<PredictResponse>>> = flow {
        try {
            emit(ResultState.Loading)
            val response = apiService.getMyPrediction()

            if (response.code in 200..299 && response.data != null) {
                emit(ResultState.Success(response.data))
            } else {
                emit(ResultState.Error(response.message, response.code))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun getPredictionById(predictionId: String): Flow<ResultState<PredictResponse>> = flow {
        try {
            emit(ResultState.Loading)
            val response = apiService.getPredictionById(predictionId)

            if (response.code in 200..299 && response.data != null) {
                emit(ResultState.Success(response.data))
            } else {
                emit(ResultState.Error(response.message, response.code))
            }

        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Terjadi kesalahan"))
        }
    }


    companion object {
        @Volatile
        private var instance: DataRepository? = null

        fun getInstance(
            apiService: ApiService
        ): DataRepository = instance ?: synchronized(this) {
            instance ?: DataRepository(apiService)
        }.also { instance = it }
    }
}