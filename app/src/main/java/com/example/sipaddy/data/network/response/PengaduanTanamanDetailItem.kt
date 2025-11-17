package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName

data class PengaduanTanamanDetailItem(
    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("kabupaten")
    val kabupaten: String? = null,

    @field:SerializedName("tanggalVerifikasi")
    val tanggalVerifikasi: String? = null,

    @field:SerializedName("catatanPopt")
    val catatanPopt: String? = null,

    @field:SerializedName("alamat")
    val alamat: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("file")
    val file: String? = null,

    @field:SerializedName("kelompokTani")
    val kelompokTani: String? = null,

    @field:SerializedName("popt")
    val popt: PoptResponseItem? = null,

    @field:SerializedName("kecamatan")
    val kecamatan: String? = null,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("deskripsi")
    val deskripsi: String? = null,

    @field:SerializedName("user")
    val user: UserResponseItem? = null,

    @field:SerializedName("longitude")
    val longitude: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String
)
