package com.example.sipaddy.data.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class PredictResponse(

    @field:SerializedName("data")
    val data: DataPredict? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

@Parcelize
data class DataPredict(

    @field:SerializedName("confidenceScore")
    val confidenceScore: Double = 0.0,

    @field:SerializedName("solutions")
    val solutions: String? = null,

    @field:SerializedName("causes")
    val causes: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("label")
    val label: String? = null
): Parcelable
