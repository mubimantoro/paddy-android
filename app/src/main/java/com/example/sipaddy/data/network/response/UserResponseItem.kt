package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName

data class UserResponseItem(
    @field:SerializedName("nama_lengkap")
    val namaLengkap: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("username")
    val username: String? = null
)
