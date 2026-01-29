package com.example.sipaddy.data.model.request

import com.google.gson.annotations.SerializedName

data class PengaduanTanamanRequest(
    @SerializedName("kelompok_tani_id")
    val kelompokTaniId: Int,

    @SerializedName("kecamatan_id")
    val kecamatanId: Int,

    @SerializedName("deskripsi")
    val deskripsi: String,

    @SerializedName("latitude")
    val latitude: String,

    @SerializedName("longitude")
    val longitude: String
)
