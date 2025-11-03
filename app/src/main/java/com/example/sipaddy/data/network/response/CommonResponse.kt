package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName

data class CommonResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String,

)
