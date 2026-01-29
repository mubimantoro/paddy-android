package com.example.sipaddy.data.model.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("username")
    val username: String,

    @field:SerializedName("nama_lengkap")
    val namaLengkap: String,

    @field:SerializedName("nomor_hp")
    val nomorHp: String,

    @field:SerializedName("kelompok_tani_id")
    val kelompokTaniId: Int?
)