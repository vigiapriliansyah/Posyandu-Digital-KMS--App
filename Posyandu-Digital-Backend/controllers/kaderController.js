const { sequelize } = require("../config/database");
const User = require("../models/User");
const OrangTuaProfile = require("../models/OrangTuaProfile");
const KaderProfile = require("../models/KaderProfile");

// --- 1. GET DAFTAR PENDING (Opsional, untuk info saja) ---
const getPendingOrangTua = async (req, res) => {
  try {
    const kader = await KaderProfile.findOne({ where: { user_id: req.user.id } });
    if (!kader) return res.status(404).json({ message: "Profil Kader tidak ditemukan" });

    const pendingList = await OrangTuaProfile.findAll({
      where: { posyandu_id: kader.posyandu_id },
      include: [
        {
          model: User,
          where: { is_verified: false },
          attributes: ["id", "username", "kode_verifikasi", "createdAt"],
        },
      ],
      order: [["createdAt", "DESC"]],
    });

    res.status(200).json(pendingList);
  } catch (error) {
    res.status(500).json({ message: "Server Error" });
  }
};

// --- 2. VERIFIKASI BY KODE (FITUR UTAMA) ---
const verifyByCode = async (req, res) => {
  const { code } = req.body; // Kader input kode 6 digit

  if (!code) {
      return res.status(400).json({ message: "Kode verifikasi wajib diisi" });
  }

  try {
    // 1. Cari User berdasarkan kode ini
    // Pastikan user tersebut role-nya orangtua dan belum verified
    const user = await User.findOne({ 
        where: { 
            kode_verifikasi: code,
            role: 'orangtua',
            is_verified: false 
        },
        include: [{ model: OrangTuaProfile }] // Include profil untuk cek posyandu
    });

    if (!user) {
      return res.status(404).json({ message: "Kode salah atau User sudah diverifikasi." });
    }

    // 2. Validasi Tambahan: Apakah orang tua ini benar terdaftar di Posyandu Kader ini?
    const kader = await KaderProfile.findOne({ where: { user_id: req.user.id } });
    
    // Jika user punya posyandu_id, pastikan sama dengan posyandu kader
    // Jika null, mungkin kader bisa mengklaimnya (opsional)
    if (user.OrangTuaProfile && user.OrangTuaProfile.posyandu_id !== kader.posyandu_id) {
        return res.status(403).json({ message: "User ini terdaftar di Posyandu lain." });
    }

    // 3. Lakukan Verifikasi
    user.is_verified = true;
    user.status = 'aktif';
    user.kode_verifikasi = null; // Hapus kode setelah dipakai agar aman
    await user.save();

    res.status(200).json({ 
        message: "Verifikasi Berhasil!", 
        detail: `Akun Ibu ${user.OrangTuaProfile.nama_ibu} telah diaktifkan.` 
    });

  } catch (error) {
    console.error("Error verifying:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

module.exports = {
  getPendingOrangTua,
  verifyByCode, // Export fungsi baru ini
};