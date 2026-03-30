const express = require("express");
const router = express.Router();
const { cetakLaporanBulanan } = require("../controllers/laporanController");
const { protect, checkRole } = require("../middleware/authMiddleware");

// Hanya Admin dan Kader yang boleh cetak laporan
router.use(protect);
router.get("/bulanan", checkRole(["admin", "kader"]), cetakLaporanBulanan);

module.exports = router;