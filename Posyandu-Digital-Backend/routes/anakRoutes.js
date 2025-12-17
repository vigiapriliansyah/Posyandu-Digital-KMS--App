const express = require("express");
const router = express.Router();
const {
  createAnak,
  getAnakByPosyandu,
  getOrangTuaVerified,
} = require("../controllers/anakController");
const { protect, checkRole } = require("../middleware/authMiddleware");

// Lindungi rute: Hanya Kader yang boleh akses manajemen anak (sementara ini)
router.use(protect);
router.use(checkRole(["kader", "admin", "superadmin"])); // Admin juga boleh lihat

// GET /api/anak -> List Anak di Posyandu user
// POST /api/anak -> Tambah Anak
router.route("/").get(getAnakByPosyandu).post(createAnak);

// GET /api/anak/orangtua -> List Orang Tua untuk Dropdown
router.get("/orangtua", getOrangTuaVerified);

module.exports = router;
