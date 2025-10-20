package com.example.sipaddy.data.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class PengaduanTanamanResponse(

    @field:SerializedName("data")
    val data: PengaduanTanamanData? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class PengaduanTanamanData(

    @field:SerializedName("pengaduan")
    val pengaduanTanaman: List<PengaduanTanamanItem>? = null
)

@Parcelize
data class PengaduanTanamanItem(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("kelompok_tani")
    val kelompokTani: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("kabupaten")
    val kabupaten: String? = null,

    @field:SerializedName("alamat")
    val alamat: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("kecamatan")
    val kecamatan: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("deskripsi")
    val deskripsi: String? = null,

    @field:SerializedName("latitude")
    val latitude: Double? = 0.0,

    @field:SerializedName("longitude")
    val longitude: Double? = 0.0,

    @field:SerializedName("status")
    val status: String? = null
) : Parcelable
