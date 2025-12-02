package com.skripsi.posyandudigital.data.remote.dto

import com.google.gson.annotations.SerializedName

// --- SUPER ADMIN ---
// PERBAIKAN: Struktur disesuaikan dengan respon backend yang bersarang (Nested)
data class SuperAdminDashboardDto(
    @SerializedName("statistik_nasional") val statistik: StatistikNasionalDto
)

data class StatistikNasionalDto(
    @SerializedName("total_admin_desa") val totalAdminDesa: Int,
    @SerializedName("total_kader") val totalKader: Int,
    @SerializedName("total_desa_terdaftar") val totalDesa: Int,
    @SerializedName("total_posyandu_aktif") val totalPosyandu: Int,
    @SerializedName("total_anak_terdata") val totalBalita: Int,
    @SerializedName("total_orang_tua_terverifikasi") val totalOrangTua: Int
)

// --- ADMIN DESA ---
data class AdminDashboardDto(
    @SerializedName("namaDesa") val namaDesa: String?,
    @SerializedName("totalBalitaTerpantau") val totalBalitaTerpantau: Int,
    @SerializedName("totalGiziBuruk") val totalGiziBuruk: Int,
    @SerializedName("totalGiziKurang") val totalGiziKurang: Int,
    @SerializedName("totalGiziBaik") val totalGiziBaik: Int,
    @SerializedName("totalGiziLebih") val totalGiziLebih: Int,
    @SerializedName("totalKaderAktif") val totalKaderAktif: Int,
    @SerializedName("totalPosyandu") val totalPosyandu: Int
)

// --- KADER ---
data class KaderDashboardDto(
    @SerializedName("namaPosyandu") val namaPosyandu: String?,
    @SerializedName("namaDesa") val namaDesa: String?,
    @SerializedName("totalBalitaDiPosyandu") val totalBalitaDiPosyandu: Int,
    @SerializedName("totalOrangTuaMenungguVerifikasi") val totalOrangTuaMenungguVerifikasi: Int,
    @SerializedName("totalGiziBuruk") val totalGiziBuruk: Int,
    @SerializedName("totalGiziKurang") val totalGiziKurang: Int,
    @SerializedName("totalGiziBaik") val totalGiziBaik: Int,
    @SerializedName("totalGiziLebih") val totalGiziLebih: Int
)

// --- ORANG TUA ---
data class OrangTuaDashboardDto(
    @SerializedName("anak") val anak: AnakDto?,
    @SerializedName("kms_terakhir") val kmsTerakhir: KmsSimpleDto?
)

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