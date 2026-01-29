package com.example.sipaddy.data.model.response

import com.google.gson.annotations.SerializedName


data class KecamatanResponse(

    @field:SerializedName("wilayah_id")
    val wilayahId: Int,

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("updated_at")
    val updatedAt: String,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("id")
    val id: Int
)
