package com.example.sipaddy.data.model.response

import com.google.gson.annotations.SerializedName


data class PredictResponse(

    @field:SerializedName("image")
    val image: String,

    @field:SerializedName("disease")
    val disease: String,

    @field:SerializedName("updated_at")
    val updatedAt: String,

    @field:SerializedName("user_id")
    val userId: Int,

    @field:SerializedName("solutions")
    val solutions: String,

    @field:SerializedName("confidence_score")
    val confidenceScore: Double,

    @field:SerializedName("causes")
    val causes: String,

    @field:SerializedName("url_youtube")
    val urlYoutube: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("id")
    val id: String
)
