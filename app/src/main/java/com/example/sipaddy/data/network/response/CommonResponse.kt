package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName

data class CommonResponse<T>(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String,

	@field:SerializedName("data")
	val data: T?

)
