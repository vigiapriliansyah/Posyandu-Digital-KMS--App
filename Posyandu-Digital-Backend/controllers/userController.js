const bcrypt = require("bcryptjs");
const { sequelize } = require("../config/database");

// Pastikan semua model ini ADA di folder models Anda
const User = require("../models/User");
const AdminProfile = require("../models/AdminProfile");
const KaderProfile = require("../models/KaderProfile");
const Desa = require("../models/Desa");
const Kecamatan = require("../models/Kecamatan");
const Posyandu = require("../models/Posyandu");

// --- 1. CREATE USER ---
const createUser = async (req, res) => {
  const { username, password, role, nama_lengkap, desa_id, posyandu_id } =
    req.body;

  if (!username || !password || !role) {
    return res
      .status(400)
      .json({ message: "Username, password, dan role wajib diisi" });
  }

  const t = await sequelize.transaction();

  try {
    const userExists = await User.findOne({ where: { username } });
    if (userExists) {
      await t.rollback();
      return res.status(400).json({ message: "Username sudah terdaftar" });
    }

    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);

    // Buat User
    const newUser = await User.create(
      {
        username,
        password: hashedPassword,
        role,
        // Superadmin langsung verified
        is_verified: role === "superadmin" ? true : false,
      },
      { transaction: t }
    );

    // Buat Profil Sesuai Role
    if (role === "admin") {
      await AdminProfile.create(
        {
          nama_admin: nama_lengkap,
          user_id: newUser.id,
          desa_id: desa_id,
        },
        { transaction: t }
      );
    } else if (role === "kader") {
      await KaderProfile.create(
        {
          nama_kader: nama_lengkap,
          user_id: newUser.id,
          posyandu_id: posyandu_id,
        },
        { transaction: t }
      );
    }

    await t.commit();
    res.status(201).json({
      message: "User berhasil dibuat",
      user: { id: newUser.id, username: newUser.username, role: newUser.role },
    });
  } catch (error) {
    await t.rollback();
    console.error("Error creating user:", error);
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};

// --- 2. GET USERS ---
const getUsers = async (req, res) => {
  try {
    const { role } = req.query;
    const currentUser = req.user; // User yang sedang melakukan request (Admin/Superadmin)

    let whereClause = {};
    if (role) {
      whereClause.role = role;
    }

    // --- PERBAIKAN: Jika Admin Desa me-request daftar Kader ---
    if (currentUser.role === 'admin' && role === 'kader') {
      // 1. Cari tahu Admin ini bertugas di desa mana
      const adminProfile = await AdminProfile.findOne({ where: { user_id: currentUser.id } });
      if (!adminProfile) return res.status(404).json({ message: "Profil Admin tidak ditemukan" });

      // 2. Kumpulkan semua Posyandu yang ada di Desa milik Admin tersebut
      // (Pastikan model Posyandu sudah ter-import di bagian atas file ini)
      const Posyandu = require("../models/Posyandu"); 
      const posyandus = await Posyandu.findAll({ where: { desa_id: adminProfile.desa_id } });
      const posyanduIds = posyandus.map(p => p.id);

      // 3. Filter User (Kader) yang posyandu_id nya HANYA ada di desa tersebut
      const users = await User.findAll({
        where: whereClause,
        attributes: { exclude: ["password"] },
        include: [
          {
            model: KaderProfile,
            where: { posyandu_id: posyanduIds }, // KUNCI FILTER: Cocokkan ID posyandu
            include: [{ model: Posyandu }]
          }
        ]
      });
      return res.status(200).json(users);
    }

    // --- TAMPILAN DEFAULT (Untuk Super Admin / get all users) ---
    const users = await User.findAll({
      where: whereClause,
      attributes: { exclude: ["password"] },
      include: [
        {
          model: AdminProfile,
          include: [{ model: require("../models/Desa"), include: [{ model: require("../models/Kecamatan") }] }]
        },
        {
          model: KaderProfile,
          include: [{ model: require("../models/Posyandu") }]
        }
      ]
    });

    res.status(200).json(users);
  } catch (error) {
    console.error("Error getUsers:", error);
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};


// --- 3. DUMMY FUNCTIONS (Agar tidak error saat di-import) ---
const getUserById = async (req, res) =>
  res.json({ message: "Not implemented yet" });
const updateUser = async (req, res) =>
  res.json({ message: "Not implemented yet" });
const deleteUser = async (req, res) => {
  const { id } = req.params;
  try {
    await User.destroy({ where: { id } });
    res.json({ message: "User deleted" });
  } catch (e) {
    res.status(500).json({ message: "Error deleting user" });
  }
};

// --- PENTING: Export object harus lengkap ---
module.exports = {
  createUser,
  getUsers,
  getUserById,
  updateUser,
  deleteUser,
};
