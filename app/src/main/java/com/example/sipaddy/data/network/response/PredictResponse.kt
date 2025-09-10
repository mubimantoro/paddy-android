package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName

data class PredictResponse(

    @field:SerializedName("data")
    val data: DataPredict? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class DataPredict(

    @field:SerializedName("confidenceScore")
    val confidenceScore: Int = 0,

    @field:SerializedName("solutions")
    val solutions: String? = null,

    @field:SerializedName("causes")
    val causes: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("label")
    val label: String? = null
)
