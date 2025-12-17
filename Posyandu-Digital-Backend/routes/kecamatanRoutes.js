const express = require("express");
const router = express.Router();
const { getAllKecamatan } = require("../controllers/kecamatanController");
const { protect } = require("../middleware/authMiddleware");

// Mendefinisikan rute /api/kecamatan
// Dilindungi oleh 'protect' agar hanya user yang sudah login yang bisa mengakses
router.route("/").get(getAllKecamatan);

module.exports = router;
