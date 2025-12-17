const User = require("../models/User");
const Desa = require("../models/Desa");
const Posyandu = require("../models/Posyandu");
const AdminProfile = require("../models/AdminProfile");
const KaderProfile = require("../models/KaderProfile");
const OrangTuaProfile = require("../models/OrangTuaProfile");
const Anak = require("../models/Anak");

// --- 1. SUPER ADMIN (Global) ---
const getSuperAdminDashboard = async (req, res) => {
  try {
    const totalAdmin = await User.count({ where: { role: "admin" } });
    const totalKader = await User.count({ where: { role: "kader" } });
    const totalOrangTua = await User.count({ where: { role: "orangtua" } });
    const totalPengguna = await User.count();
    const totalDesa = await Desa.count();
    const totalPosyandu = await Posyandu.count();
    const totalBalita = await Anak.count(); 

    res.status(200).json({
      statistik_nasional: {
        total_admin_desa: totalAdmin,
        total_kader: totalKader,
        total_desa_terdaftar: totalDesa,
        total_posyandu_aktif: totalPosyandu,
        total_anak_terdata: totalBalita,
        total_orang_tua_terverifikasi: totalOrangTua,
        total_pengguna: totalPengguna 
      },
    });
  } catch (error) {
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};

// --- 2. ADMIN DESA (Perbaikan: Handle Profile Null) ---
const getAdminDashboard = async (req, res) => {
  try {
    const adminProfile = await AdminProfile.findOne({
      where: { user_id: req.user.id },
      include: [{ model: Desa }],
    });

    // PENGAMANAN: Jika akun Admin ada tapi belum diset profil desanya
    if (!adminProfile) {
        return res.status(200).json({
            namaDesa: "Desa Belum Diset",
            totalBalitaTerpantau: 0,
            totalGiziBuruk: 0, 
            totalGiziKurang: 0,
            totalGiziBaik: 0,
            totalGiziLebih: 0,
            totalKaderAktif: 0,
            totalPosyandu: 0,
            warning: "Profil Desa belum diatur."
        });
    }

    const desaId = adminProfile.desa_id;
    const namaDesa = adminProfile.Desa ? adminProfile.Desa.nama_desa : "-";
    const totalPosyandu = await Posyandu.count({ where: { desa_id: desaId } });

    // Ambil ID semua Posyandu di desa ini
    const posyandus = await Posyandu.findAll({
      where: { desa_id: desaId },
      attributes: ["id"],
    });
    const posyanduIds = posyandus.map((p) => p.id);

    if (posyanduIds.length === 0) {
        return res.status(200).json({
            namaDesa: namaDesa,
            totalBalitaTerpantau: 0,
            totalGiziBuruk: 0, totalGiziKurang: 0, totalGiziBaik: 0, totalGiziLebih: 0,
            totalKaderAktif: 0,
            totalPosyandu: 0,
        });
    }

    // Hitung Kader
    const totalKader = await KaderProfile.count({
      where: { posyandu_id: posyanduIds },
    });

    // Hitung Balita (via kolom posyandu_id di tabel Anak)
    const totalBalita = await Anak.count({
      where: { posyandu_id: posyanduIds }
    });

    res.status(200).json({
      namaDesa: namaDesa,
      totalBalitaTerpantau: totalBalita,
      totalGiziBuruk: 0, 
      totalGiziKurang: 0,
      totalGiziBaik: 0,
      totalGiziLebih: 0,
      totalKaderAktif: totalKader,
      totalPosyandu: totalPosyandu,
    });
  } catch (error) {
    console.error("Error Admin Dashboard:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

// --- 3. KADER (Perbaikan: Handle Profile Null & Relasi Baru) ---
const getKaderDashboard = async (req, res) => {
  try {
    const kader = await KaderProfile.findOne({
      where: { user_id: req.user.id },
      include: [{ model: Posyandu, include: [{ model: Desa }] }],
    });

    // PENGAMANAN: Jika akun Kader belum punya profil posyandu
    if (!kader) {
        return res.status(200).json({
            namaPosyandu: "Belum Diset",
            namaDesa: "-",
            totalBalitaDiPosyandu: 0,
            totalOrangTuaMenungguVerifikasi: 0,
            totalGiziBuruk: 0, totalGiziKurang: 0, totalGiziBaik: 0, totalGiziLebih: 0,
        });
    }

    const posyanduId = kader.posyandu_id;
    const namaPosyandu = kader.Posyandu?.nama_posyandu || "-";
    const namaDesa = kader.Posyandu?.Desa?.nama_desa || "-";

    // Hitung Orang Tua Pending (via tabel OrangTuaProfile)
    const pendingOrtu = await OrangTuaProfile.count({
      where: { posyandu_id: posyanduId },
      include: [{ model: User, where: { is_verified: false } }],
    });

    // Hitung Anak (via tabel Anak)
    const totalBalita = await Anak.count({
      where: { posyandu_id: posyanduId }
    });

    res.status(200).json({
      namaPosyandu,
      namaDesa,
      totalBalitaDiPosyandu: totalBalita,
      totalOrangTuaMenungguVerifikasi: pendingOrtu,
      totalGiziBuruk: 0,
      totalGiziKurang: 0,
      totalGiziBaik: 0,
      totalGiziLebih: 0,
    });
  } catch (error) {
    console.error("Error Kader Dashboard:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

// --- 4. ORANG TUA ---
const getOrangTuaDashboard = async (req, res) => {
  // TODO: Hubungkan dengan data real (Many-to-Many logic)
  res.status(200).json({
    anak: { nama_anak: "Budi Santoso", umur_bulan: 12 },
    kms_terakhir: {
      tanggal_pencatatan: "2025-09-25",
      berat_badan: 9.5,
      tinggi_badan: 75.5,
      status_gizi: "Normal",
    },
  });
};

module.exports = {
  getKaderDashboard,
  getAdminDashboard,
  getSuperAdminDashboard,
  getOrangTuaDashboard,
};