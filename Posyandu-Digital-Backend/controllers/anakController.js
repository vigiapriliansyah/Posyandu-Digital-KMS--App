const { sequelize } = require("../config/database");
const Anak = require("../models/Anak");
const OrangTuaAnak = require("../models/OrangTuaAnak");
const KaderProfile = require("../models/KaderProfile");
const User = require("../models/User"); // User adalah tabel orang tua sekarang
const OrangTuaProfile = require("../models/OrangTuaProfile"); // Untuk ambil nama ibu

// --- 1. TAMBAH DATA ANAK (UPDATE SESUAI RELASI BARU) ---
const createAnak = async (req, res) => {
  const {
    nama_anak,
    nik_anak,
    tanggal_lahir,
    jenis_kelamin,
    // Di frontend dikirim 'orangtua_id' (User ID si ibu)
    orangtua_id 
  } = req.body;

  if (!nama_anak || !tanggal_lahir || !jenis_kelamin || !orangtua_id) {
    return res.status(400).json({
        message: "Data wajib (Nama, Tgl Lahir, JK, Orang Tua) harus diisi",
    });
  }

  const t = await sequelize.transaction();

  try {
    // 1. Cari Kader ini tugas di posyandu mana? (Anak ikut posyandu kader)
    const kader = await KaderProfile.findOne({ where: { user_id: req.user.id } });
    if (!kader) {
        await t.rollback();
        return res.status(403).json({ message: "Anda bukan kader terdaftar" });
    }

    // 2. Buat Data Anak
    const anakBaru = await Anak.create({
      nama_anak,
      nik_anak,
      tanggal_lahir,
      jenis_kelamin,
      posyandu_id: kader.posyandu_id,
      status_anak: 'aktif'
    }, { transaction: t });

    // 3. Hubungkan Anak dengan Orang Tua (Tabel Pivot)
    await OrangTuaAnak.create({
        user_id_orangtua: orangtua_id, // ID User orang tua
        anak_id: anakBaru.id
    }, { transaction: t });

    await t.commit();
    res.status(201).json({ message: "Data Anak berhasil ditambahkan", anak: anakBaru });

  } catch (error) {
    await t.rollback();
    console.error("Error create anak:", error);
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};

// --- 2. GET DAFTAR ANAK (UPDATE) ---
const getAnakByPosyandu = async (req, res) => {
  try {
    const kader = await KaderProfile.findOne({ where: { user_id: req.user.id } });
    if (!kader) return res.status(404).json({ message: "Profil Kader tidak ditemukan" });

    // Ambil anak di posyandu ini + data Orang tuanya
    const daftarAnak = await Anak.findAll({
      where: { posyandu_id: kader.posyandu_id },
      include: [
        {
            model: User, // Mengambil data orang tua via many-to-many
            through: { attributes: [] }, // Tidak perlu data pivot
            include: [{ model: OrangTuaProfile, attributes: ['nama_ibu', 'alamat'] }] 
        }
      ],
      order: [["nama_anak", "ASC"]],
    });

    // Mapping agar format JSON tetap ramah untuk Frontend Android yang sudah dibuat
    const formattedData = daftarAnak.map(anak => {
        // Ambil orang tua pertama (asumsi biasanya 1)
        const orangTua = anak.Users[0]?.OrangTuaProfile; 
        
        return {
            id: anak.id,
            nama_anak: anak.nama_anak,
            jenis_kelamin: anak.jenis_kelamin,
            tanggal_lahir: anak.tanggal_lahir,
            // Format OrangTuaProfile disamakan dengan DTO Android
            OrangTuaProfile: orangTua ? { nama_ibu: orangTua.nama_ibu } : { nama_ibu: "Belum terhubung" }
        };
    });

    res.status(200).json(formattedData);
  } catch (error) {
    console.error("Error fetching anak:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

// --- 3. GET ORANG TUA (UPDATE) ---
// Mengambil list User dengan role 'orangtua' di posyandu kader
const getOrangTuaVerified = async (req, res) => {
  try {
    const kader = await KaderProfile.findOne({ where: { user_id: req.user.id } });
    
    // Cari profil orang tua yang posyandu_id nya sama dengan kader
    const listOrangTua = await OrangTuaProfile.findAll({
        where: { posyandu_id: kader.posyandu_id },
        include: [{
            model: User,
            where: { is_verified: true }, // Hanya yang sudah verified
            attributes: ['id', 'username']
        }],
        attributes: ['nama_ibu']
    });

    // Format untuk Dropdown Android: { id: user_id, nama_ibu: "Siti" }
    const response = listOrangTua.map(profil => ({
        id: profil.User.id, // PENTING: ID yang dikirim adalah ID USER, bukan ID Profil
        nama_ibu: profil.nama_ibu
    }));

    res.status(200).json(response);
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: "Server Error" });
  }
};

module.exports = {
  createAnak,
  getAnakByPosyandu,
  getOrangTuaVerified,
};