const bcrypt = require("bcryptjs");
const { sequelize } = require("../config/database"); // Impor sequelize untuk transaksi
const User = require("../models/User");
const AdminProfile = require("../models/AdminProfile");
const Desa = require("../models/Desa");
const Kecamatan = require("../models/Kecamatan");

/**
 * @desc    Membuat user baru (Admin atau Kader)
 * @route   POST /api/users
 * @access  Private/SuperAdmin
 */
const createUser = async (req, res) => {
  // Ambil semua data yang mungkin dari body
  const { username, password, role, nama_lengkap, desa_id } = req.body;

  // Validasi dasar
  if (!username || !password || !role) {
    return res
      .status(400)
      .json({ message: "Username, password, dan role wajib diisi" });
  }

  // --- LOGIKA BARU UNTUK ADMIN ---
  if (role === "admin" && (!nama_lengkap || !desa_id)) {
    return res
      .status(400)
      .json({ message: "Untuk admin, nama lengkap dan ID desa wajib diisi" });
  }
  // -----------------------------

  const t = await sequelize.transaction(); // Mulai transaksi

  try {
    const userExists = await User.findOne({ where: { username } });
    if (userExists) {
      await t.rollback(); // Batalkan transaksi jika user sudah ada
      return res.status(400).json({ message: "Username sudah terdaftar" });
    }

    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);

    // 1. Buat entri di tabel user
    const newUser = await User.create(
      {
        username,
        password: hashedPassword,
        role,
      },
      { transaction: t }
    );

    // --- LOGIKA BARU: Jika rolenya admin, buat juga profilnya ---
    if (newUser.role === "admin") {
      // 2. Buat entri di tabel admin_profiles
      await AdminProfile.create(
        {
          nama_admin: nama_lengkap,
          user_id: newUser.id,
          desa_id: desa_id,
        },
        { transaction: t }
      );
    }
    // (Nanti kita bisa tambahkan logika `else if (newUser.role === 'kader')` di sini)
    // -----------------------------------------------------------

    await t.commit(); // Selesaikan transaksi jika semua berhasil

    // Kirim kembali data user tanpa password
    res.status(201).json({
      id: newUser.id,
      username: newUser.username,
      role: newUser.role,
    });
  } catch (error) {
    await t.rollback(); // Batalkan semua jika ada error
    console.error("Error creating user:", error);
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};

/**
 * @desc    Mendapatkan semua user, bisa filter berdasarkan role
 * @route   GET /api/users?role=admin
 * @access  Private/SuperAdmin
 */
const getUsers = async (req, res) => {
  try {
    const { role } = req.query;
    let users;

    if (role === "admin") {
      // --- LOGIKA BARU: Ambil user admin beserta data profil dan desanya ---
      users = await User.findAll({
        where: { role: "admin" },
        attributes: { exclude: ["password"] }, // Jangan pernah kirim password
        include: [
          {
            model: AdminProfile,
            attributes: ["nama_admin"], // Ambil nama admin dari profil
            include: [
              {
                model: Desa,
                attributes: ["nama_desa"], // Ambil nama desa dari relasi
                include: [
                  {
                    model: Kecamatan,
                    attributes: ["nama_kecamatan"], // Ambil juga nama kecamatan
                  },
                ],
              },
            ],
          },
        ],
        order: [["createdAt", "DESC"]],
      });
    } else {
      // Logika lama untuk role lain (misal: kader)
      const whereClause = {};
      if (role) {
        whereClause.role = role;
      }
      users = await User.findAll({
        where: whereClause,
        attributes: { exclude: ["password"] },
        order: [["createdAt", "DESC"]],
      });
    }

    res.status(200).json(users);
  } catch (error) {
    console.error("Error fetching users:", error);
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};

// ... (Fungsi getUserById, updateUser, deleteUser tetap sama untuk saat ini) ...
const getUserById = async (req, res) => {
  /* ... */
};
const updateUser = async (req, res) => {
  /* ... */
};
const deleteUser = async (req, res) => {
  /* ... */
};

module.exports = {
  createUser,
  getUsers,
  getUserById,
  updateUser,
  deleteUser,
};
