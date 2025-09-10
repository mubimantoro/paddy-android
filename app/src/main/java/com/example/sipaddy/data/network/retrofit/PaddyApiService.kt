package com.example.sipaddy.data.network.retrofit

import com.example.sipaddy.data.network.response.CommonResponse
import com.example.sipaddy.data.network.response.LoginResponse
import com.example.sipaddy.data.network.response.PredictResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PaddyApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("nama_lengkap") namaLengkap: String,
        @Field("nomor_hp") nomorHp: String
    ): CommonResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("pengaduan")
    suspend fun addNewPengaduan(
        @Header("Authorization") token: String
    ): CommonResponse

    @Multipart
    suspend fun createPengaduan(
        @Header("Authorization") token: String,
        @Part("kelompokTani") kelompokTani: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("kecamatan") kecamatan: RequestBody,
        @Part("kabupaten") kabupaten: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("latitude") latitude: RequestBody? = null,
        @Part("longitude") longitude: RequestBody? = null,
        @Part photo: MultipartBody.Part
    ): CommonResponse

    @Multipart
    @POST("predict")
    suspend fun predict(
        @Part photo: MultipartBody.Part
    ): PredictResponse
}