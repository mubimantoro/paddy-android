package com.example.sipaddy.data.network.response

import com.google.gson.annotations.SerializedName

data class TestingResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class User(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("namaLengkap")
	val namaLengkap: String? = null
)

data class Pengaduan(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("kabupaten")
	val kabupaten: String? = null,

	@field:SerializedName("tanggalVerifikasi")
	val tanggalVerifikasi: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("file")
	val file: String? = null,

	@field:SerializedName("kelompokTani")
	val kelompokTani: String? = null,

	@field:SerializedName("popt")
	val popt: Popt? = null,

	@field:SerializedName("kecamatan")
	val kecamatan: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class Popt(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("namaLengkap")
	val namaLengkap: String? = null
)

data class Data(

	@field:SerializedName("pengaduan")
	val pengaduan: Pengaduan? = null
)
