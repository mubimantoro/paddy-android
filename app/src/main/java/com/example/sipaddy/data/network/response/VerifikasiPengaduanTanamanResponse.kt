package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName


data class VerifikasiPengaduanTanamanResponse(

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("data")
    val data: PengaduanTanamanDetailItem,
)