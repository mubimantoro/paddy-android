package com.example.sipaddy.data.model.response

import com.google.gson.annotations.SerializedName

data class PengaduanTanamanResponse(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("user_id")
    val userId: Int,

    @field:SerializedName("kelompok_tani_id")
    val kelompokTaniId: Int,

    @field:SerializedName("kecamatan_id")
    val kecamatanId: Int,

    @field:SerializedName("deskripsi")
    val deskripsi: String,

    @field:SerializedName("latitude")
    val latitude: String,

    @field:SerializedName("longitude")
    val longitude: String,

    @field:SerializedName("image")
    val image: String,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("assigned_popt_id")
    val assignedPoptId: Int? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String,

    @field:SerializedName("pelapor_nama")
    val pelaporNama: String? = null,

    @field:SerializedName("kelompok_tani_nama")
    val kelompokTaniNama: String? = null,

    @field:SerializedName("kecamatan_nama")
    val kecamatanNama: String? = null,
)
