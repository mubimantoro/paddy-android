package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName


data class VerifikasiPengaduanTanamanResponse(

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("data")
    val data: VerifikasiPengaduanTanamanItem,
)

data class VerifikasiPengaduanTanamanItem(
    @field:SerializedName("pengaduanId")
    val pengaduanId: String? = null,

    @field:SerializedName("tanggalVerifikasi")
    val tanggalVerifikasi: String? = null
)
