package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName

data class PengaduanTanamanDetailResponse(

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("data")
    val data: PengaduanTanamanDetailData? = null
)

data class PengaduanTanamanDetailData(
    @field:SerializedName("pengaduan")
    val pengaduan: PengaduanTanamanDetailItem
)