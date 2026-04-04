const User = require("../models/User");
const Anak = require("../models/Anak");
const Posyandu = require("../models/Posyandu");
const Desa = require("../models/Desa");
const Pengukuran = require("../models/Pengukuran");
const AdminProfile = require("../models/AdminProfile");
const KaderProfile = require("../models/KaderProfile");
const OrangTuaProfile = require("../models/OrangTuaProfile");

// --- PERBAIKAN: Import rumus WHO ---
const { hitungStatusGizi } = require("../utils/zscore");

const hitungStatistikGizi = async (daftarAnak) => {
  let stats = { buruk: 0, kurang: 0, baik: 0, lebih: 0 };
  
  for (const anak of daftarAnak) {
    const lastUkur = await Pengukuran.findOne({
      where: { anak_id: anak.id },
      order: [['tanggal_pencatatan', 'DESC'], ['id', 'DESC']]
    });

    if (lastUkur) {
      const hasilGizi = hitungStatusGizi(anak.jenis_kelamin, lastUkur.umur_bulan, lastUkur.berat_badan, 0);
      let giziStatus = hasilGizi.bb_u || "";

      if (giziStatus.includes("Buruk")) stats.buruk++;
      else if (giziStatus.includes("Kurang")) stats.kurang++;
      else if (giziStatus.includes("Baik") || giziStatus.includes("Normal")) stats.baik++;
      else if (giziStatus.includes("Lebih") || giziStatus.includes("Risiko")) stats.lebih++;
    }
  }
  return stats;
};

// --- 1. SUPER ADMIN ---
const getSuperAdminDashboard = async (req, res) => {
  try {
    const totalAdminDesa = await User.count({ where: { role: 'admin' } });
    const totalKader = await User.count({ where: { role: 'kader' } });
    const totalDesa = await Desa.count();
    const totalPosyandu = await Posyandu.count();
    const daftarAnak = await Anak.findAll();
    const totalOrangTua = await User.count({ where: { role: 'orangtua' } });

    res.status(200).json({
      statistik_nasional: {
        total_admin_desa: totalAdminDesa,
        total_kader: totalKader,
        total_desa_terdaftar: totalDesa,
        total_posyandu_aktif: totalPosyandu,
        total_anak_terdata: daftarAnak.length,
        total_orang_tua_terverifikasi: totalOrangTua
      }
    });
  } catch (error) {
    console.error("Error SuperAdmin Dashboard:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

// --- 2. ADMIN DESA ---
const getAdminDashboard = async (req, res) => {
  try {
    const adminProfile = await AdminProfile.findOne({ 
        where: { user_id: req.user.id },
        include: [{ model: Desa }]
    });

    if (!adminProfile || !adminProfile.desa_id) {
        return res.status(404).json({ message: "Admin Profile tidak ditemukan" });
    }

    const desaId = adminProfile.desa_id;
    const posyandus = await Posyandu.findAll({ where: { desa_id: desaId } });
    const posyanduIds = posyandus.map(p => p.id);

    const totalKader = await KaderProfile.count({ where: { posyandu_id: posyanduIds } });
    const daftarAnak = await Anak.findAll({ where: { posyandu_id: posyanduIds } });

    const giziStats = await hitungStatistikGizi(daftarAnak);

    res.status(200).json({
      namaDesa: adminProfile.Desa ? adminProfile.Desa.nama_desa : "",
      totalBalitaTerpantau: daftarAnak.length,
      totalPosyandu: posyandus.length,
      totalKaderAktif: totalKader,
      totalGiziBuruk: giziStats.buruk,
      totalGiziKurang: giziStats.kurang,
      totalGiziBaik: giziStats.baik,
      totalGiziLebih: giziStats.lebih
    });
  } catch (error) {
    console.error("Error Admin Dashboard:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

// --- 3. KADER POSYANDU ---
const getKaderDashboard = async (req, res) => {
  try {
    const kaderProfile = await KaderProfile.findOne({
      where: { user_id: req.user.id },
      include: [{ model: Posyandu, include: [{ model: Desa }] }]
    });

    if (!kaderProfile || !kaderProfile.posyandu_id) {
      return res.status(404).json({ message: "Data posyandu kader tidak ditemukan" });
    }

    const posyanduId = kaderProfile.posyandu_id;
    const daftarAnak = await Anak.findAll({ where: { posyandu_id: posyanduId } });

    const totalMenungguVerifikasi = await OrangTuaProfile.count({
        where: { posyandu_id: posyanduId },
        include: [{ model: User, where: { is_verified: false, status: 'pending_verification' } }]
    });

    const giziStats = await hitungStatistikGizi(daftarAnak);

    res.status(200).json({
      namaPosyandu: kaderProfile.Posyandu ? kaderProfile.Posyandu.nama_posyandu : "",
      namaDesa: (kaderProfile.Posyandu && kaderProfile.Posyandu.Desa) ? kaderProfile.Posyandu.Desa.nama_desa : "",
      totalBalitaDiPosyandu: daftarAnak.length,
      totalOrangTuaMenungguVerifikasi: totalMenungguVerifikasi,
      totalGiziBuruk: giziStats.buruk,
      totalGiziKurang: giziStats.kurang,
      totalGiziBaik: giziStats.baik,
      totalGiziLebih: giziStats.lebih
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
  getSuperAdminDashboard,
  getAdminDashboard,
  getKaderDashboard,
  getOrangTuaDashboard
};