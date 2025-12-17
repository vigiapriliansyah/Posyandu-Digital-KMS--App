const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const { sequelize } = require("../config/database");

// Import Model
const User = require("../models/User");
const OrangTuaProfile = require("../models/OrangTuaProfile");
const AdminProfile = require("../models/AdminProfile");
const KaderProfile = require("../models/KaderProfile");
const Desa = require("../models/Desa");
const Kecamatan = require("../models/Kecamatan");
const Posyandu = require("../models/Posyandu");

const generateToken = (id, role) => {
  return jwt.sign({ id, role }, process.env.JWT_SECRET, { expiresIn: "30d" });
};

// Fungsi helper bikin kode angka 6 digit
const generateVerificationCode = () => {
  return Math.floor(100000 + Math.random() * 900000).toString();
};

// --- REGISTER ---
const registerUser = async (req, res) => {
  const {
    username,
    password,
    role: inputRole,
    nama_ibu,
    no_hp,
    alamat,
    posyandu_id,
  } = req.body;
  
  const role = inputRole || "orangtua";

  if (!username || !password) {
    return res.status(400).json({ message: "Username dan Password wajib diisi" });
  }

  // Validasi khusus Orang Tua
  if (role === "orangtua" && !nama_ibu) {
    return res.status(400).json({ message: "Data wajib diisi (Nama Ibu)" });
  }

  const t = await sequelize.transaction();

  try {
    const userExists = await User.findOne({ where: { username } });
    if (userExists) {
      await t.rollback();
      return res.status(400).json({ message: "Username sudah digunakan" });
    }

    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);

    // --- LOGIKA VERIFIKASI BARU ---
    // Superadmin langsung verified. Orang tua butuh kode.
    const isVerified = role === "superadmin";
    let verificationCode = null;

    if (role === "orangtua") {
        verificationCode = generateVerificationCode();
        // Cek clash kode (sangat jarang terjadi, tapi good practice)
        // Disini kita skip cek clash kompleks demi kesederhanaan skripsi
    }

    // 1. Buat User
    const newUser = await User.create(
      {
        username,
        password: hashedPassword,
        role,
        is_verified: isVerified,
        kode_verifikasi: verificationCode, // Simpan kode
        status: isVerified ? 'aktif' : 'pending_verification'
      },
      { transaction: t }
    );

    // 2. Buat Profile
    if (role === "orangtua") {
      await OrangTuaProfile.create(
        {
          user_id: newUser.id,
          nama_ibu,
          no_hp,
          alamat,
          posyandu_id: posyandu_id || null,
        },
        { transaction: t }
      );
    } else if (role === "admin") {
       // ... (Logika admin profile jika ada register admin lewat sini)
    }

    await t.commit();

    res.status(201).json({
      message: `Registrasi berhasil! Kode Verifikasi Anda: ${verificationCode}`,
      user: { 
          id: newUser.id, 
          username: newUser.username, 
          role: newUser.role,
          // Kirim balik kode agar bisa ditampilkan di Android setelah register sukses
          kode_verifikasi: verificationCode 
      },
    });
  } catch (error) {
    await t.rollback();
    res.status(500).json({ message: "Server error", error: error.message });
  }
};

// --- LOGIN ---
const loginUser = async (req, res) => {
  const { username, password } = req.body;
  try {
    const user = await User.findOne({ where: { username } });

    if (user && (await bcrypt.compare(password, user.password))) {
      
      // Cek Verifikasi
      if (user.role === "orangtua" && !user.is_verified) {
        // PERUBAHAN: Jika belum verified, tetap return token TAPI beri flag khusus
        // atau return error code spesifik agar Android bisa mengarahkan ke layar "Tunggu Verifikasi"
        // Untuk skripsi ini, kita return user dengan kode_verifikasi agar bisa ditampilkan lagi
        return res.status(200).json({
            message: "Akun belum diverifikasi",
            require_verification: true,
            user: { 
                id: user.id, 
                username: user.username, 
                role: user.role,
                kode_verifikasi: user.kode_verifikasi 
            }
        });
      }

      res.status(200).json({
        message: "Login successful",
        user: { id: user.id, username: user.username, role: user.role },
        token: generateToken(user.id, user.role),
      });
    } else {
      res.status(401).json({ message: "Username atau password salah" });
    }
  } catch (error) {
    res.status(500).json({ message: "Server error", error: error.message });
  }
};

// --- GET ME ---
const getMe = async (req, res) => {
  try {
    const user = await User.findByPk(req.user.id, {
      attributes: { exclude: ["password"] },
      include: [
        {
          model: AdminProfile,
          include: [{ model: Desa, include: [Kecamatan] }],
        },
        {
          model: KaderProfile,
          include: [
            {
              model: Posyandu,
              include: [{ model: Desa, include: [Kecamatan] }],
            },
          ],
        },
        { model: OrangTuaProfile, include: [{ model: Posyandu }] },
      ],
    });

    if (!user) return res.status(404).json({ message: "User not found" });
    res.status(200).json(user);
  } catch (error) {
    console.error("Error fetching me:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

const initDatabase = async (req, res) => {
  try {
    await sequelize.sync({ alter: true });
    res.status(200).json({ message: "Database synced!" });
  } catch (error) {
    res.status(500).json({ message: "Sync failed", error: error.message });
  }
};

module.exports = {
  registerUser,
  loginUser,
  getMe,
  initDatabase,
};