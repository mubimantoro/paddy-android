package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName

data class WilayahResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("data")
    val data: WilayahData
)

data class WilayahData(
    @field:SerializedName("wilayah")
    val wilayah: List<WilayahItem>
)

data class WilayahItem(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("kode")
    val kode: String?
)

