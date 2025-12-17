const express = require("express");
const router = express.Router();
const {
  getPendingOrangTua,
  verifyByCode, // Ganti verifyOrangTua dengan verifyByCode
} = require("../controllers/kaderController");
const { protect, checkRole } = require("../middleware/authMiddleware");

router.use(protect);
router.use(checkRole(["kader"]));

// Lihat daftar (opsional, untuk monitoring kader)
router.get("/verifikasi", getPendingOrangTua);

// Input Kode Verifikasi
// POST /api/kader/verifikasi
router.post("/verifikasi", verifyByCode); 

module.exports = router;