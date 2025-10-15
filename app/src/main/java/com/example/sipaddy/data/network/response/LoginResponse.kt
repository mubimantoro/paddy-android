package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("data")
    val loginResult: LoginResult? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class LoginResult(


    @field:SerializedName("user")
    val user: UserResponse? = null,

    @field:SerializedName("token")
    val token: String? = null,
)
