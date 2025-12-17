package com.skripsi.posyandudigital.data.remote.dto

import com.google.gson.annotations.SerializedName

// 1. DTO Untuk menampilkan list anak di halaman "Daftar Balita"
data class AnakDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_anak") val namaAnak: String,
    @SerializedName("jenis_kelamin") val jenisKelamin: String,
    @SerializedName("tanggal_lahir") val tanggalLahir: String,
    @SerializedName("umur_bulan") val umurBulan: Int?,
    @SerializedName("OrangTuaProfile") val orangTua: OrangTuaSimpleDto?
)

// 2. DTO Untuk Dropdown pemilihan Orang Tua saat "Tambah Anak"
data class OrangTuaSimpleDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_ibu") val namaIbu: String
)

// 3. DTO Untuk Mengirim Data Anak Baru ke Backend
data class CreateAnakRequest(
    @SerializedName("nama_anak") val namaAnak: String,
    @SerializedName("nik_anak") val nikAnak: String,
    @SerializedName("tempat_lahir") val tempatLahir: String,
    @SerializedName("tanggal_lahir") val tanggalLahir: String, // Format YYYY-MM-DD
    @SerializedName("jenis_kelamin") val jenisKelamin: String, // "L" atau "P"
    @SerializedName("berat_badan_lahir") val beratLahir: Double,
    @SerializedName("tinggi_badan_lahir") val tinggiLahir: Double,
    @SerializedName("orangtua_id") val orangTuaId: Int
)