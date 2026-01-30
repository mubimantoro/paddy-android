package com.example.sipaddy.data.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("accessToken")
    val accessToken: String,

    @field:SerializedName("refreshToken")
    val refreshToken: String,

    @field:SerializedName("user")
    val user: UserData
)

data class UserData(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("username")
    val username: String,

    @field:SerializedName("nama_lengkap")
    val namaLengkap: String?,


    @field:SerializedName("nomor_hp")
    val nomorHp: String?,

    @field:SerializedName("kelompok_tani")
    val kelompokTani: String?,


    @field:SerializedName("roles")
    val roles: List<String>?
) {
    val role: String?
        get() = roles?.firstOrNull()
}