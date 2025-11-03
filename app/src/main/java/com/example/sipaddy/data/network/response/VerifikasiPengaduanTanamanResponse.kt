package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName


data class VerifikasiPengaduanTanamanItem(

    @field:SerializedName("pengaduanId")
    val pengaduanId: String? = null,

    @field:SerializedName("tanggalVerifikasi")
    val tanggalVerifikasi: String? = null
)
