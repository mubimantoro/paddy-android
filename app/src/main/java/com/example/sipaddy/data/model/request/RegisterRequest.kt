package com.example.sipaddy.data.model.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @field:SerializedName("username")
    val username: String,

    @field:SerializedName("password")
    val password: String,

    @field:SerializedName("nama_lengkap")
    val namaLengkap: String,

    @field:SerializedName("nomor_hp")
    val nomorHp: String,

    @field:SerializedName("kelompok_tani_id")
    val kelompokTaniId: Int? = null
)
