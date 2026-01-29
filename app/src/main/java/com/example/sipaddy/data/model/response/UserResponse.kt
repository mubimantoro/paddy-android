package com.example.sipaddy.data.model.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("nama_lengkap")
    val namaLengkap: String? = null,

    @SerializedName("role")
    val role: String
)