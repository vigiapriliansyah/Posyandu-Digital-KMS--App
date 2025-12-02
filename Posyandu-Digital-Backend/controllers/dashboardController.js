const User = require("../models/User");
const Desa = require("../models/Desa");
const Posyandu = require("../models/Posyandu");
const AdminProfile = require("../models/AdminProfile");
const KaderProfile = require("../models/KaderProfile");
const OrangTuaProfile = require("../models/OrangTuaProfile");
const Anak = require("../models/Anak");
// ... (getSuperAdminDashboard tetap sama) ...
const getSuperAdminDashboard = async (req, res) => {
  try {
    const totalAdmin = await User.count({ where: { role: "admin" } });
    const totalKader = await User.count({ where: { role: "kader" } });
    const totalOrangTua = await User.count({ where: { role: "orangtua" } });
    const totalPengguna = await User.count();
    const totalDesa = await Desa.count();
    const totalPosyandu = await Posyandu.count();
    const totalBalita = await Anak.count(); // Sudah bisa hitung anak jika tabel ada

    res.status(200).json({
      statistik_nasional: {
        total_pengguna: totalPengguna,
        total_admin_desa: totalAdmin,
        total_kader: totalKader,
        total_desa_terdaftar: totalDesa,
        total_posyandu_aktif: totalPosyandu,
        total_anak_terdata: totalBalita,
        total_orang_tua_terverifikasi: totalOrangTua,
      },
    });
  } catch (error) {
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};

const getAdminDashboard = async (req, res) => {
  try {
    const adminProfile = await AdminProfile.findOne({
      where: { user_id: req.user.id },
      include: [{ model: Desa }],
    });

    if (!adminProfile)
      return res.status(404).json({ message: "Profil tidak ditemukan" });

    const desaId = adminProfile.desa_id;
    const namaDesa = adminProfile.Desa ? adminProfile.Desa.nama_desa : "-";
    const totalPosyandu = await Posyandu.count({ where: { desa_id: desaId } });

    // Hitung kader di desa ini
    const posyandus = await Posyandu.findAll({
      where: { desa_id: desaId },
      attributes: ["id"],
    });
    const posyanduIds = posyandus.map((p) => p.id);
    const totalKader = await KaderProfile.count({
      where: { posyandu_id: posyanduIds },
    });

    // Hitung balita di desa ini (lewat relasi Posyandu -> OrangTua -> Anak)
    const totalBalita = await Anak.count({
      include: [
        {
          model: OrangTuaProfile,
          where: { posyandu_id: posyanduIds },
        },
      ],
    });

    res.status(200).json({
      namaDesa: namaDesa,
      totalBalitaTerpantau: totalBalita,
      totalGiziBuruk: 0, // Nanti diisi dari tabel pengukuran
      totalGiziKurang: 0,
      totalGiziBaik: 0,
      totalGiziLebih: 0,
      totalKaderAktif: totalKader,
      totalPosyandu: totalPosyandu,
    });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: "Server Error" });
  }
};

// --- PERBAIKAN UTAMA: DASHBOARD KADER REAL-TIME ---
const getKaderDashboard = async (req, res) => {
  try {
    // 1. Cari Profil Kader (Tugas di Posyandu mana?)
    const kader = await KaderProfile.findOne({
      where: { user_id: req.user.id },
      include: [
        {
          model: Posyandu,
          include: [{ model: Desa }],
        },
      ],
    });

    if (!kader) {
      return res.status(404).json({ message: "Profil Kader tidak ditemukan" });
    }

    const posyanduId = kader.posyandu_id;
    const namaPosyandu = kader.Posyandu?.nama_posyandu || "-";
    const namaDesa = kader.Posyandu?.Desa?.nama_desa || "-";

    // 2. Hitung Orang Tua MENUNGGU VERIFIKASI di Posyandu ini
    const pendingOrtu = await OrangTuaProfile.count({
      where: { posyandu_id: posyanduId },
      include: [
        {
          model: User,
          where: { is_verified: false }, // Cari yang belum verified
        },
      ],
    });

    // 3. Hitung Total Balita di Posyandu ini
    const totalBalita = await Anak.count({
      include: [
        {
          model: OrangTuaProfile,
          where: { posyandu_id: posyanduId },
        },
      ],
    });

    res.status(200).json({
      namaPosyandu,
      namaDesa,
      totalBalitaDiPosyandu: totalBalita,
      totalOrangTuaMenungguVerifikasi: pendingOrtu,
      // Statistik Gizi masih 0 (Menunggu fitur Pencatatan)
      totalGiziBuruk: 0,
      totalGiziKurang: 0,
      totalGiziBaik: 0,
      totalGiziLebih: 0,
    });
  } catch (error) {
    console.error("Error kader dashboard:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

// ... (getOrangTuaDashboard biarkan sama) ...
const getOrangTuaDashboard = async (req, res) => {
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
