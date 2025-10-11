package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("namaLengkap")
    val namaLengkap: String? = null,

    @field:SerializedName("username")
    val username: String? = null
)