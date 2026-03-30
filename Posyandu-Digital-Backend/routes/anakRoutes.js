const express = require("express");
const router = express.Router();
const {
  createAnak,
  getAnakList, 
  getOrangTuaVerified,
} = require("../controllers/anakController");
const { protect, checkRole } = require("../middleware/authMiddleware");

// Lindungi rute: Semua yang mengakses harus punya token
router.use(protect);

// POST /api/anak -> Tambah Anak (Orang Tua & Kader)
router.post("/", checkRole(["admin", "kader", "orangtua"]), createAnak);

// GET /api/anak -> List Anak di Posyandu user (Orang Tua & Kader)
router.get("/", checkRole(["admin", "kader", "orangtua"]), getAnakList);

// GET /api/anak/orangtua -> List Orang Tua untuk Dropdown Kader
router.get("/orangtua", checkRole(["admin", "kader"]), getOrangTuaVerified);

module.exports = router;