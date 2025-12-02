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

// --- REGISTER (DIPERBAIKI: VALIDASI FLEKSIBEL) ---
const registerUser = async (req, res) => {
  // 1. Ambil 'role' dari body. Default ke 'orangtua' jika kosong.
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

  // 2. Validasi Dasar (Username & Password Wajib)
  if (!username || !password) {
    return res
      .status(400)
      .json({ message: "Username dan Password wajib diisi" });
  }

  // 3. Validasi Khusus: Hanya Orang Tua yang wajib isi Nama Ibu
  // Superadmin, Admin, Kader akan LEWAT (SKIP) validasi ini
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

    // 4. Logika Verifikasi: Superadmin langsung verified
    const isVerified = role === "superadmin";

    // 5. Buat User
    const newUser = await User.create(
      {
        username,
        password: hashedPassword,
        role,
        is_verified: isVerified,
      },
      { transaction: t }
    );

    // 6. Buat Profile (Hanya untuk Orang Tua)
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
    }

    await t.commit();

    res.status(201).json({
      message: `Registrasi ${role} berhasil!`,
      user: { id: newUser.id, username: newUser.username, role: newUser.role },
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
      // Cek Verifikasi (Khusus Orang Tua)
      if (user.role === "orangtua" && user.is_verified === false) {
        return res.status(403).json({ message: "Akun belum diverifikasi." });
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

// --- INIT DATABASE (Opsional) ---
const initDatabase = async (req, res) => {
  try {
    await sequelize.sync({ alter: true });
    res.status(200).json({ message: "Struktur Database berhasil diperbarui!" });
  } catch (error) {
    res
      .status(500)
      .json({ message: "Gagal init database", error: error.message });
  }
};

module.exports = {
  registerUser,
  loginUser,
  getMe,
  initDatabase,
};
