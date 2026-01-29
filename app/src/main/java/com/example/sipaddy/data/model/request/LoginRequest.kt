package com.example.sipaddy.data.model.request

import com.example.sipaddy.data.model.KelompokTaniModel
import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @field:SerializedName("username")
    val username: String,

    @field:SerializedName("password")
    val password: String
)