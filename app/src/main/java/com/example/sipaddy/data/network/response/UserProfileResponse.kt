package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("data")
    val data: UserProfileData

)

data class UserProfileData(
    @field:SerializedName("user")
    val user: UserProfile
)

data class UserProfile(
    @field:SerializedName("username")
    val username: String,

    @field:SerializedName("namaLengkap")
    val namaLengkap: String,

    @field:SerializedName("nomorHp")
    val nomorHp: String?
)
