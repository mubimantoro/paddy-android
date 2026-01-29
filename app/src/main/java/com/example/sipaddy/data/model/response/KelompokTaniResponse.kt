package com.example.sipaddy.data.model.response

import com.google.gson.annotations.SerializedName


data class KelompokTaniResponse(

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("updated_at")
    val updatedAt: String,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("id")
    val id: Int
)
