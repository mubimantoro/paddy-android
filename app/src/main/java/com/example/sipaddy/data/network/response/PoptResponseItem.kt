package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName

data class PoptResponseItem(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("namaLengkap")
    val namaLengkap: String? = null
)
