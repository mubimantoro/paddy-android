package com.example.sipaddy.data.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class DiseaseResponse(

    @field:SerializedName("data")
    val data: DiseaseData? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class DiseaseData(
    @field:SerializedName("diseases")
    val diseases: List<DiseaseItem>? = null
)

@Parcelize
data class DiseaseItem(

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("solutions")
    val solutions: String? = null,

    @field:SerializedName("confidence_score")
    val confidenceScore: String? = null,

    @field:SerializedName("causes")
    val causes: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("image")
    val imageUrl: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("label")
    val label: String? = null
) : Parcelable
