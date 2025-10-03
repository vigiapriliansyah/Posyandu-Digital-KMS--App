const express = require("express");
const router = express.Router();
const {
  createUser,
  getUsers,
  getUserById,
  updateUser,
  deleteUser,
} = require("../controllers/userController");
// ---- PERBAIKAN UTAMA ADA DI BARIS INI ----
const { protect, checkRole } = require("../middleware/authMiddleware"); // Mengganti 'authorize' menjadi 'checkRole'

// Lindungi semua rute di bawah ini dengan otentikasi dan otorisasi Super Admin
router.use(protect);
// ---- DAN JUGA DI BARIS INI ----
router.use(checkRole(["superadmin"])); // Menggunakan 'checkRole' yang sudah kita impor

// /api/users
router.route("/").post(createUser).get(getUsers);

// /api/users/:id
router.route("/:id").get(getUserById).put(updateUser).delete(deleteUser);

module.exports = router;
