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
  .get(protect, getAllPosyandu)
  .post(protect, checkRole(["superadmin"]), createPosyandu);

module.exports = router;
