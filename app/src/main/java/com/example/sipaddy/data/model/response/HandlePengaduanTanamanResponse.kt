package com.example.sipaddy.data.model.response

import com.google.gson.annotations.SerializedName

data class HandlePengaduanTanamanResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("status") val status: String
)
