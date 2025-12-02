const express = require("express");
const router = express.Router();
// PERBAIKAN: Import 'initDatabase' dari controller
const {
  registerUser,
  loginUser,
  getMe,
  initDatabase,
} = require("../controllers/authController");
const { protect } = require("../middleware/authMiddleware");

router.post("/register", registerUser);
router.post("/login", loginUser);
router.get("/me", protect, getMe);

// PERBAIKAN: Pasang rute init agar bisa dipanggil via browser/curl
router.get("/init", initDatabase);

module.exports = router;
