package com.example.sipaddy.data.model.response

import com.google.gson.annotations.SerializedName

data class DetailPengaduanTanamanResponse(

    @field:SerializedName("pengaduanTanaman")
    val pengaduanTanaman: PengaduanTanamanDetail,

    @field:SerializedName("verifikasiPengaduanTanaman")
    val verifikasiPengaduanTanaman: List<VerifikasiPengaduanTanaman>,

    @field:SerializedName("pemeriksaanPengaduanTanaman")
    val pemeriksaanPengaduanTanaman: List<PemeriksaanPengaduanTanaman>
)

data class PengaduanTanamanDetail(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("user_id")
    val userId: Int,

    @field:SerializedName("kelompok_tani_id")
    val kelompokTaniId: Int,

    @field:SerializedName("kecamatan_id")
    val kecamatanId: Int,

    @field:SerializedName("deskripsi")
    val deskripsi: String,

    @field:SerializedName("latitude")
    val latitude: String,

    @field:SerializedName("longitude")
    val longitude: String,

    @field:SerializedName("image")
    val image: String,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("assigned_popt_id")
    val assignedPoptId: Int? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String,

    @field:SerializedName("pelapor_nama")
    val pelaporNama: String,

    @field:SerializedName("kelompok_tani_nama")
    val kelompokTaniNama: String,

    @field:SerializedName("kecamatan_nama")
    val kecamatanNama: String,

    @field:SerializedName("popt_nama")
    val poptNama: String?,
)

data class VerifikasiPengaduanTanaman(
    @SerializedName("id")
    val id: Int,

    @SerializedName("pengaduan_tanaman_id")
    val pengaduanTanamanId: Int,

    @SerializedName("popt_id")
    val poptId: Int,

    @SerializedName("tanggal_visit")
    val tanggalVisit: String,

    @SerializedName("foto_visit")
    val fotoVisit: String,

    @SerializedName("latitude")
    val latitude: String,

    @SerializedName("longitude")
    val longitude: String,

    @SerializedName("catatan")
    val catatan: String?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("popt_nama")
    val poptNama: String,
)

data class PemeriksaanPengaduanTanaman(
    @SerializedName("id")
    val id: Int,

    @SerializedName("pengaduan_tanaman_id")
    val pengaduanTanamanId: Int,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("hasil_pemeriksaan")
    val hasilPemeriksaan: String,

    @SerializedName("file")
    val file: String?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("pemeriksa_nama")
    val pemeriksaNama: String?
)
