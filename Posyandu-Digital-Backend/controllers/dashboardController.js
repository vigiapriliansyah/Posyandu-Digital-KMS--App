const User = require("../models/User");
const Desa = require("../models/Desa");
const Posyandu = require("../models/Posyandu");
const AdminProfile = require("../models/AdminProfile");
const KaderProfile = require("../models/KaderProfile");
const OrangTuaProfile = require("../models/OrangTuaProfile");
const Anak = require("../models/Anak");
const Pengukuran = require("../models/Pengukuran"); // TAMBAHAN: Import tabel Pengukuran KMS

// --- FUNGSI BANTUAN UNTUK MENGHITUNG GIZI TERAKHIR ---
const hitungStatistikGizi = async (daftarAnak) => {
  let stats = { buruk: 0, kurang: 0, baik: 0, lebih: 0 };
  
  for (const anak of daftarAnak) {
    // Ambil pengukuran KMS anak yang paling baru
    const lastUkur = await Pengukuran.findOne({
      where: { anak_id: anak.id },
      order: [['tanggal_pencatatan', 'DESC'], ['createdAt', 'DESC']]
    });

    if (lastUkur && lastUkur.status_gizi) {
      let giziStatus = "";
      try {
          // Parse JSON karena status gizi disave dalam bentuk string JSON
          const parsed = JSON.parse(lastUkur.status_gizi);
          giziStatus = parsed.bb_u || "";
      } catch (e) {
          giziStatus = lastUkur.status_gizi;
      }

      // Deteksi kata kunci
      if (giziStatus.includes("Buruk")) stats.buruk++;
      else if (giziStatus.includes("Kurang")) stats.kurang++;
      else if (giziStatus.includes("Baik") || giziStatus.includes("Normal")) stats.baik++;
      else if (giziStatus.includes("Lebih") || giziStatus.includes("Risiko")) stats.lebih++;
    }
  }
  return stats;
};

// --- 1. SUPER ADMIN (Tidak butuh Profile, hitung global) ---
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
    console.error("Error Superadmin Dashboard:", error);
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};

// --- 2. ADMIN DESA (Wajib Punya AdminProfile) ---
const getAdminDashboard = async (req, res) => {
  try {
    const adminProfile = await AdminProfile.findOne({
      where: { user_id: req.user.id },
      include: [{ model: Desa }],
    });

    if (!adminProfile) {
        return res.status(200).json({
            namaDesa: "Desa Belum Diset",
            totalBalitaTerpantau: 0,
            totalGiziBuruk: 0, totalGiziKurang: 0, totalGiziBaik: 0, totalGiziLebih: 0,
            totalKaderAktif: 0, totalPosyandu: 0,
            is_profile_incomplete: true 
        });
    }

    const desaId = adminProfile.desa_id;
    const namaDesa = adminProfile.Desa ? adminProfile.Desa.nama_desa : "-";
    const totalPosyandu = await Posyandu.count({ where: { desa_id: desaId } });

    const posyandus = await Posyandu.findAll({ where: { desa_id: desaId }, attributes: ["id"] });
    const posyanduIds = posyandus.map((p) => p.id);

    if (posyanduIds.length === 0) {
        return res.status(200).json({
            namaDesa: namaDesa,
            totalBalitaTerpantau: 0,
            totalGiziBuruk: 0, totalGiziKurang: 0, totalGiziBaik: 0, totalGiziLebih: 0,
            totalKaderAktif: 0, totalPosyandu: 0,
        });
    }

    const totalKader = await KaderProfile.count({ where: { posyandu_id: posyanduIds } });
    
    // Ambil daftar anak lalu kalkulasi gizinya
    const daftarAnak = await Anak.findAll({ where: { posyandu_id: posyanduIds } });
    const giziStats = await hitungStatistikGizi(daftarAnak);

    // PERBAIKAN UTAMA: Masukkan hasil kalkulasi ke dalam Response JSON
    res.status(200).json({
      namaDesa: namaDesa,
      totalBalitaTerpantau: daftarAnak.length,
      totalGiziBuruk: giziStats.buruk, 
      totalGiziKurang: giziStats.kurang,
      totalGiziBaik: giziStats.baik,
      totalGiziLebih: giziStats.lebih,
      totalKaderAktif: totalKader,
      totalPosyandu: totalPosyandu,
    });
  } catch (error) {
    console.error("Error Admin Dashboard:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

// --- 3. KADER (Wajib Punya KaderProfile) ---
const getKaderDashboard = async (req, res) => {
  try {
    const kader = await KaderProfile.findOne({
      where: { user_id: req.user.id },
      include: [{ model: Posyandu, include: [{ model: Desa }] }],
    });

    if (!kader) {
        return res.status(200).json({
            namaPosyandu: "Belum Diset", namaDesa: "-",
            totalBalitaDiPosyandu: 0, totalOrangTuaMenungguVerifikasi: 0,
            totalGiziBuruk: 0, totalGiziKurang: 0, totalGiziBaik: 0, totalGiziLebih: 0,
            is_profile_incomplete: true
        });
    }

    const posyanduId = kader.posyandu_id;
    const namaPosyandu = kader.Posyandu?.nama_posyandu || "-";
    const namaDesa = kader.Posyandu?.Desa?.nama_desa || "-";

    const pendingOrtu = await OrangTuaProfile.count({
      where: { posyandu_id: posyanduId },
      include: [{ model: User, where: { is_verified: false } }],
    });

    // Ambil daftar anak lalu kalkulasi gizinya
    const daftarAnak = await Anak.findAll({ where: { posyandu_id: posyanduId } });
    const giziStats = await hitungStatistikGizi(daftarAnak);

    // PERBAIKAN UTAMA: Masukkan hasil kalkulasi ke dalam Response JSON
    res.status(200).json({
      namaPosyandu,
      namaDesa,
      totalBalitaDiPosyandu: daftarAnak.length,
      totalOrangTuaMenungguVerifikasi: pendingOrtu,
      totalGiziBuruk: giziStats.buruk,
      totalGiziKurang: giziStats.kurang,
      totalGiziBaik: giziStats.baik,
      totalGiziLebih: giziStats.lebih,
    });
  } catch (error) {
    console.error("Error Kader Dashboard:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

// --- 4. ORANG TUA ---
const getOrangTuaDashboard = async (req, res) => {
  try {
    const userId = req.user.id;

    const daftarAnak = await Anak.findAll({
      include: [{
        model: User,
        where: { id: userId },
        through: { attributes: [] }
      }],
      order: [['createdAt', 'DESC']]
    });

    if (!daftarAnak || daftarAnak.length === 0) {
      return res.status(200).json({ daftar_anak: [], kms_terakhir: null });
    }

    const formattedAnakList = daftarAnak.map(anak => {
        const tglLahir = new Date(anak.tanggal_lahir);
        const skrg = new Date();
        let umurBulan = (skrg.getFullYear() - tglLahir.getFullYear()) * 12 + skrg.getMonth() - tglLahir.getMonth();
        if (umurBulan < 0) umurBulan = 0;
        
        return {
            id: anak.id,
            nama_anak: anak.nama_anak,
            umur_bulan: umurBulan,
            jenis_kelamin: anak.jenis_kelamin
        };
    });

    res.status(200).json({
      daftar_anak: formattedAnakList,
      kms_terakhir: null
    });

  } catch (error) {
    console.error("Error getOrangTuaDashboard:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

module.exports = {
  getKaderDashboard,
  getAdminDashboard,
  getSuperAdminDashboard,
  getOrangTuaDashboard,
};