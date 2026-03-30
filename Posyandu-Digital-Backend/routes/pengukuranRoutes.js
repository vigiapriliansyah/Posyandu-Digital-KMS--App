const express = require("express");
const router = express.Router();
const { createPengukuran, getRiwayatAnak } = require("../controllers/pengukuranController");
const { protect, checkRole } = require("../middleware/authMiddleware");

router.use(protect);

router.post("/", checkRole(["kader", "admin"]), createPengukuran);

router.get("/anak/:anakId", getRiwayatAnak);

module.exports = router;