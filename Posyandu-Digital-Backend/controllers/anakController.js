const { sequelize } = require("../config/database");
const Anak = require("../models/Anak");
const OrangTuaAnak = require("../models/OrangTuaAnak");
const KaderProfile = require("../models/KaderProfile");
const AdminProfile = require("../models/AdminProfile");
const Posyandu = require("../models/Posyandu");
const User = require("../models/User"); 
const OrangTuaProfile = require("../models/OrangTuaProfile"); 

// --- 1. TAMBAH DATA ANAK (ORANG TUA, KADER, & ADMIN) ---
const createAnak = async (req, res) => {
  const {
    nama_anak,
    nik_anak,
    tanggal_lahir,
    jenis_kelamin,
    berat_badan_lahir,
    tinggi_badan_lahir,
    orangtua_id // Dikirim oleh kader/admin. Jika orang tua, ini akan diabaikan.
  } = req.body;

  if (!nama_anak || !tanggal_lahir || !jenis_kelamin) {
    return res.status(400).json({
        message: "Data wajib (Nama, Tgl Lahir, Jenis Kelamin) harus diisi",
    });
  }

  const t = await sequelize.transaction();

  try {
    let finalOrangTuaUserId;
    let finalPosyanduId;

    // CEK SIAPA YANG SEDANG LOGIN
    if (req.user.role === 'orangtua') {
      // JIKA ORANG TUA: Otomatis gunakan ID profil dia sendiri
      const profilIbu = await OrangTuaProfile.findOne({ where: { user_id: req.user.id } });
      if (!profilIbu) {
          await t.rollback();
          return res.status(404).json({ message: "Profil Ibu tidak ditemukan" });
      }
      
      finalOrangTuaUserId = req.user.id;
      finalPosyanduId = profilIbu.posyandu_id;

    } else if (req.user.role === 'kader' || req.user.role === 'admin') {
      // JIKA KADER/ADMIN: Harus ada orangtua_id dari request body (dropdown)
      if (!orangtua_id) {
          await t.rollback();
          return res.status(400).json({ message: "ID Orang Tua wajib dipilih" });
      }
      
      // Ambil Posyandu dari profil ibu yang dipilih (karena Admin mengawasi banyak Posyandu)
      const profilIbu = await OrangTuaProfile.findOne({ where: { user_id: orangtua_id } });
      if (!profilIbu || !profilIbu.posyandu_id) {
          await t.rollback();
          return res.status(400).json({ message: "Orang tua yang dipilih belum terhubung ke Posyandu" });
      }
      
      finalOrangTuaUserId = orangtua_id;
      finalPosyanduId = profilIbu.posyandu_id;
    } else {
      await t.rollback();
      return res.status(403).json({ message: "Akses ditolak" });
    }

    // Buat Data Anak
    const anakBaru = await Anak.create({
      nama_anak,
      nik_anak: nik_anak || null,
      tanggal_lahir,
      jenis_kelamin,
      berat_badan_lahir: berat_badan_lahir || null,
      tinggi_badan_lahir: tinggi_badan_lahir || null,
      posyandu_id: finalPosyanduId,
      status_anak: 'aktif'
    }, { transaction: t });

    // Hubungkan Anak dengan Orang Tua (Tabel Pivot)
    await OrangTuaAnak.create({
        user_id_orangtua: finalOrangTuaUserId, 
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

// --- 2. GET DAFTAR ANAK (DIPERBARUI DENGAN STATUS GIZI) ---
const getAnakList = async (req, res) => {
  try {
    let daftarAnak = [];

    if (req.user.role === 'kader') {
      const kader = await KaderProfile.findOne({ where: { user_id: req.user.id } });
      if (!kader) return res.status(404).json({ message: "Profil Kader tidak ditemukan" });

      daftarAnak = await Anak.findAll({
        where: { posyandu_id: kader.posyandu_id },
        include: [
          {
              model: User, 
              through: { attributes: [] }, 
              include: [{ model: OrangTuaProfile, attributes: ['nama_ibu', 'alamat'] }] 
          }
        ],
        order: [["nama_anak", "ASC"]],
      });
    } else if (req.user.role === 'admin') {
      const admin = await AdminProfile.findOne({ where: { user_id: req.user.id } });
      if (!admin || !admin.desa_id) return res.status(404).json({ message: "Profil Admin Desa tidak ditemukan" });

      const posyandus = await Posyandu.findAll({ where: { desa_id: admin.desa_id } });
      const posyanduIds = posyandus.map(p => p.id);

      daftarAnak = await Anak.findAll({
        where: { posyandu_id: posyanduIds },
        include: [
          {
              model: User, 
              through: { attributes: [] }, 
              include: [{ model: OrangTuaProfile, attributes: ['nama_ibu', 'alamat'] }] 
          }
        ],
        order: [["nama_anak", "ASC"]],
      });
    } else if (req.user.role === 'orangtua') {
      daftarAnak = await Anak.findAll({
        include: [
          {
              model: User,
              where: { id: req.user.id },
              through: { attributes: [] },
              include: [{ model: OrangTuaProfile, attributes: ['nama_ibu', 'alamat'] }] 
          }
        ],
        order: [["nama_anak", "ASC"]],
      });
    }

    // Mapping format JSON agar ramah untuk Frontend Android
    const formattedData = await Promise.all(daftarAnak.map(async (anak) => {
        const orangTua = (anak.Users && anak.Users.length > 0) ? anak.Users[0].OrangTuaProfile : null; 
        
        // Hitung Umur Bulan
        const tglLahir = new Date(anak.tanggal_lahir);
        const skrg = new Date();
        let umurBulan = (skrg.getFullYear() - tglLahir.getFullYear()) * 12 + skrg.getMonth() - tglLahir.getMonth();
        if (umurBulan < 0) umurBulan = 0;

        // --- MENCARI STATUS GIZI TERAKHIR DARI TABEL PENGUKURAN ---
        let statusGiziTeks = null;
        try {
            const modelPengukuran = sequelize.models.Pengukuran; 
            
            if (modelPengukuran) {
                const lastKms = await modelPengukuran.findOne({
                    where: { anak_id: anak.id },
                    // PERBAIKAN FATAL: Tambahkan createdAt agar backend mengambil data 
                    // yang BENAR-BENAR terakhir diinput jika tanggalnya sama.
                    order: [
                        ['tanggal_pencatatan', 'DESC'],
                        ['createdAt', 'DESC'] 
                    ]
                });

                if (lastKms && lastKms.status_gizi) {
                    const giziObj = JSON.parse(lastKms.status_gizi);
                    statusGiziTeks = giziObj.bb_u;
                }
            }
        } catch (err) {
            console.error(`Gagal parsing riwayat KMS anak ID ${anak.id}:`, err.message);
        }

        return {
            id: anak.id,
            nama_anak: anak.nama_anak,
            jenis_kelamin: anak.jenis_kelamin,
            tanggal_lahir: anak.tanggal_lahir,
            umur_bulan: umurBulan,
            OrangTuaProfile: orangTua ? { nama_ibu: orangTua.nama_ibu } : { nama_ibu: "Belum terhubung" },
            status_gizi_terakhir: statusGiziTeks
        };
    }));

    res.status(200).json(formattedData);
  } catch (error) {
    console.error("Error fetching anak:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

// --- 3. GET ORANG TUA VERIFIED (Untuk Dropdown Kader & Admin) ---
const getOrangTuaVerified = async (req, res) => {
  try {
    let posyanduIds = [];

    if (req.user.role === 'kader') {
        const kader = await KaderProfile.findOne({ where: { user_id: req.user.id } });
        if (!kader) return res.status(404).json({ message: "Kader tidak ditemukan" });
        posyanduIds = [kader.posyandu_id];
    } else if (req.user.role === 'admin') {
        const admin = await AdminProfile.findOne({ where: { user_id: req.user.id } });
        if (!admin) return res.status(404).json({ message: "Admin tidak ditemukan" });
        
        const posyandus = await Posyandu.findAll({ where: { desa_id: admin.desa_id } });
        posyanduIds = posyandus.map(p => p.id);
    }
    
    const listOrangTua = await OrangTuaProfile.findAll({
        where: { posyandu_id: posyanduIds },
        include: [{
            model: User,
            where: { is_verified: true }, 
            attributes: ['id', 'username']
        }],
        attributes: ['nama_ibu']
    });

    const response = listOrangTua.map(profil => ({
        id: profil.User.id, 
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
  getAnakList,
  getOrangTuaVerified,
};