const express = require("express");
const router = express.Router();
const { getAllDesa, createDesa } = require("../controllers/desaController");
const { protect, checkRole } = require("../middleware/authMiddleware");

// /api/desa
router
  .route("/")
  .get(protect, getAllDesa) // GET untuk mengambil daftar desa
  .post(protect, checkRole(["superadmin"]), createDesa); // POST untuk membuat desa baru, hanya oleh superadmin

module.exports = router;
