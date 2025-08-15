package com.example.sipaddy.data.network.retrofit

import com.example.sipaddy.data.network.response.CommonResponse
import com.example.sipaddy.data.network.response.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

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
}