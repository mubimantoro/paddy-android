package com.example.sipaddy.data.model.response

import com.google.gson.annotations.SerializedName

data class PengaduanTanamanResponse(

    @field:SerializedName("image")
    val image: String,

    @field:SerializedName("updated_at")
    val updatedAt: String,

    @field:SerializedName("user_id")
    val userId: Int,

    @field:SerializedName("latitude")
    val latitude: String,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("kelompok_tani_id")
    val kelompokTaniId: Int,

    @field:SerializedName("assigned_popt_id")
    val assignedPoptId: Int?,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("deskripsi")
    val deskripsi: String,

    @field:SerializedName("kecamatan_id")
    val kecamatanId: Int,

    @field:SerializedName("longitude")
    val longitude: String,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("pelapor_nama")
    val pelaporNama: String,


    @field:SerializedName("kelompok_tani_nama")
    val kelompokTaniNama: String,

    @field:SerializedName("kecamatan_nama")
    val kecamatanNama: String,
)
