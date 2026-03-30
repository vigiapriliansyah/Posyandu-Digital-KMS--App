package com.skripsi.posyandudigital.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreatePengukuranRequest(
    @SerializedName("anak_id") val anakId: Int,
    @SerializedName("umur_bulan") val umurBulan: Int?,
    @SerializedName("berat_badan") val beratBadan: Double,
    @SerializedName("tinggi_badan") val tinggiBadan: Double?,
    @SerializedName("kenaikan_bb_gram") val kbm: Int?, // Wajib sesuai backend
    @SerializedName("status_naik_turun") val statusNaikTurun: String?,
    @SerializedName("asi_eksklusif") val asiEksklusif: Boolean?,
    @SerializedName("tanggal_pencatatan") val tanggalPencatatan: String,
    @SerializedName("catatan_petugas") val catatan: String? // Wajib sesuai backend
)

data class PengukuranResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: PengukuranDetailDto?,
    @SerializedName("analisis") val analisis: AnalisisGiziDto?
)

data class AnalisisGiziDto(
    @SerializedName("bb_u") val bbU: String?,
    @SerializedName("warna_kms") val warnaKms: String?
)

data class PengukuranDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("tanggal_pencatatan") val tanggalPencatatan: String?,
    @SerializedName("umur_bulan") val umurBulan: Int,
    @SerializedName("berat_badan") val beratBadan: Double,
    @SerializedName("tinggi_badan") val tinggiBadan: Double?,
    @SerializedName("kenaikan_bb_gram") val kbm: Int?, // Wajib sesuai backend
    @SerializedName("status_naik_turun") val statusNaikTurun: String?,
    @SerializedName("asi_eksklusif") val asiEksklusif: Boolean?,
    @SerializedName("status_gizi") val statusGiziRaw: String?,
    @SerializedName("catatan_petugas") val catatan: String? // Wajib sesuai backend
)