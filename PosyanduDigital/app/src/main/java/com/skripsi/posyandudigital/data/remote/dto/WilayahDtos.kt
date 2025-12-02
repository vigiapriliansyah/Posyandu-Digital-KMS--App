package com.skripsi.posyandudigital.data.remote.dto

import com.google.gson.annotations.SerializedName

data class KecamatanDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_kecamatan") val namaKecamatan: String
)

data class DesaDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_desa") val namaDesa: String
)

data class PosyanduDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_posyandu") val namaPosyandu: String
)