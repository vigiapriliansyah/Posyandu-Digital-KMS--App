package com.skripsi.posyandudigital.data.remote.dto

import com.google.gson.annotations.SerializedName
// Data yang diterima untuk dashboard Super Admin

data class SuperAdminDashboardDto(
    @SerializedName("totalAdminAktif") val totalAdminAktif: Int,
    @SerializedName("totalKaderAktif") val totalKaderAktif: Int,
    @SerializedName("totalDesaTerdaftar") val totalDesaTerdaftar: Int,
    @SerializedName("totalPosyanduAktif") val totalPosyanduAktif: Int,
    @SerializedName("totalBalitaTerpantau") val totalBalitaTerpantau: Int,
    @SerializedName("totalOrangTuaTerverifikasi") val totalOrangTuaTerverifikasi: Int
)

data class AdminDashboardDto(
    @SerializedName("namaDesa") val namaDesa: String?, // Dibuat nullable untuk keamanan
    @SerializedName("totalBalitaTerpantau") val totalBalitaTerpantau: Int,
    @SerializedName("totalGiziBuruk") val totalGiziBuruk: Int,
    @SerializedName("totalGiziKurang") val totalGiziKurang: Int,
    @SerializedName("totalGiziBaik") val totalGiziBaik: Int,
    @SerializedName("totalGiziLebih") val totalGiziLebih: Int,
    @SerializedName("totalKaderAktif") val totalKaderAktif: Int
)

data class KaderDashboardDto(
    @SerializedName("namaPosyandu") val namaPosyandu: String?,
    @SerializedName("namaDesa") val namaDesa: String?,
    @SerializedName("totalBalitaDiPosyandu") val totalBalitaDiPosyandu: Int,
    @SerializedName("totalOrangTuaMenungguVerifikasi") val totalOrangTuaMenungguVerifikasi: Int,
    // Tambahkan field-field baru untuk statistik gizi
    @SerializedName("totalGiziBuruk") val totalGiziBuruk: Int,
    @SerializedName("totalGiziKurang") val totalGiziKurang: Int,
    @SerializedName("totalGiziBaik") val totalGiziBaik: Int,
    @SerializedName("totalGiziLebih") val totalGiziLebih: Int
)

// DTO untuk Orang Tua, sekarang dengan struktur bertingkat
data class OrangTuaDashboardDto(
    @SerializedName("anak") val anak: AnakDto?,
    @SerializedName("kms_terakhir") val kmsTerakhir: KmsSimpleDto?
)

// Kelas data baru untuk objek 'anak' yang bertingkat
data class AnakDto(
    @SerializedName("nama_anak") val namaAnak: String?,
    @SerializedName("umur_bulan") val umurBulan: Int
)

data class KmsSimpleDto(
    @SerializedName("tanggal_pencatatan") val tanggalPencatatan: String?,
    @SerializedName("berat_badan") val beratBadan: Double,
    @SerializedName("tinggi_badan") val tinggiBadan: Double,
    @SerializedName("status_gizi") val statusGizi: String?
)

