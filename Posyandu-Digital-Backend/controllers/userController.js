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
    const queryOptions = {
      where: {},
      attributes: { exclude: ["password"] },
      order: [["createdAt", "DESC"]],
      include: [],
    };

    if (role) queryOptions.where.role = role;

    if (role === "admin") {
      queryOptions.include.push({
        model: AdminProfile,
        include: [{ model: Desa, include: [Kecamatan] }],
      });
    } else if (role === "kader") {
      queryOptions.include.push({
        model: KaderProfile,
        include: [
          { model: Posyandu, include: [{ model: Desa, include: [Kecamatan] }] },
        ],
      });
    }

    const users = await User.findAll(queryOptions);
    res.status(200).json(users);
  } catch (error) {
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
