package com.example.sipaddy.data.api

import com.example.sipaddy.data.model.request.LoginRequest
import com.example.sipaddy.data.model.request.RegisterRequest
import com.example.sipaddy.data.model.response.AssignedPengaduanTanamanResponse
import com.example.sipaddy.data.model.response.BaseResponse
import com.example.sipaddy.data.model.response.DetailPengaduanTanamanResponse
import com.example.sipaddy.data.model.response.HandlePengaduanTanamanResponse
import com.example.sipaddy.data.model.response.KecamatanResponse
import com.example.sipaddy.data.model.response.KelompokTaniResponse
import com.example.sipaddy.data.model.response.LoginResponse
import com.example.sipaddy.data.model.response.PengaduanTanamanResponse
import com.example.sipaddy.data.model.response.PredictResponse
import com.example.sipaddy.data.model.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): BaseResponse<RegisterResponse>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): BaseResponse<LoginResponse>

    @GET("kelompok-tani")
    suspend fun getkelompokTani(): BaseResponse<List<KelompokTaniResponse>>

    @GET("kecamatan/all")
    suspend fun getKecamatan(
    ): BaseResponse<List<KecamatanResponse>>

    @Multipart
    @POST("pengaduan-tanaman")
    suspend fun createPengaduanTanaman(
        @Part("kelompok_tani_id") kelompokTaniId: RequestBody,
        @Part("kecamatan_id") kecamatanId: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part image: MultipartBody.Part?
    ): BaseResponse<PengaduanTanamanResponse>

    @GET("pengaduan-tanaman/my")
    suspend fun getMyPengaduanTanaman(): BaseResponse<List<PengaduanTanamanResponse>>

    @GET("pengaduan-tanaman/id")
    suspend fun getDetailPengaduanTanaman(
        @Path("id") id: Int
    ): BaseResponse<DetailPengaduanTanamanResponse>

    @Multipart
    @POST("predict")
    suspend fun predictDisease(
        @Part image: MultipartBody.Part
    ): BaseResponse<PredictResponse>

    @GET("predictions/my")
    suspend fun getMyPrediction(): BaseResponse<List<PredictResponse>>


    @GET("predictions/{id}")
    suspend fun getPredictionById(
        @Path("id") predictionId: String
    ): BaseResponse<PredictResponse>

    @GET("pengaduan-tanaman/assigned")
    suspend fun getAssignedPengaduanTanaman(): BaseResponse<List<AssignedPengaduanTanamanResponse>>

    @GET("pengaduan-tanaman/{id}")
    suspend fun getPengaduanTanamanById(
        @Path("id") id: Int
    ): BaseResponse<PengaduanTanamanResponse>

    @PUT("pengaduan-tanaman/{id}")
    suspend fun handlePengaduanTanaman(
        @Path("id") id: Int
    ): BaseResponse<HandlePengaduanTanamanResponse>

    @Multipart
    @PUT("pengaduan-tanaman/{id}/verifikasi")
    suspend fun submitVerifikasi(
        @Path("id") id: Int,
        @Part("foto_verifikasi") fotoVerifikasi: MultipartBody.Part?,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("catatan") catatan: RequestBody
    ): BaseResponse<PengaduanTanamanResponse>


}