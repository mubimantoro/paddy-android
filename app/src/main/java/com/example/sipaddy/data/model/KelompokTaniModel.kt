package com.example.sipaddy.data.model

import com.google.gson.annotations.SerializedName

data class KelompokTaniModel(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("nama")
    val nama: String
)
