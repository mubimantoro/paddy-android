package com.example.sipaddy.data.model

class PengaduanForm(
    val kelompokTani: String,
    val alamat: String,
    val kecamatan: String,
    val kabupaten: String,
    val deskripsi: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)