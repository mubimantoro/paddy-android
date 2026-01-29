package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @field:SerializedName("code")
    val code: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("data")
    val data: T?
)
