const express = require("express");
const router = express.Router();
const {
  getAllPosyandu,
  createPosyandu,
} = require("../controllers/posyanduController");
const { protect, checkRole } = require("../middleware/authMiddleware");

// /api/posyandu
router
  .route("/")
  .get(protect, getAllPosyandu) // Semua user login bisa melihat daftar posyandu
  // PERBAIKAN: Tambahkan "admin" ke dalam array checkRole
  .post(protect, checkRole(["superadmin", "admin"]), createPosyandu);

module.exports = router;
