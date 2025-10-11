package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName

data class TestResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Data(

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("token")
	val token: String? = null
)

data class User(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("namaLengkap")
	val namaLengkap: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
