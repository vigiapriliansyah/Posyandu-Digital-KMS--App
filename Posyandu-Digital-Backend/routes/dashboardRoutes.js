const express = require("express");
const router = express.Router();
const {
  getAdminDashboard,
  getKaderDashboard,
  getOrangTuaDashboard,
  getSuperAdminDashboard,
} = require("../controllers/dashboardController");

const {
  protect,
  authorize,
  checkRole,
} = require("../middleware/authMiddleware");

router.get("/kader", protect, checkRole(["kader"]), getKaderDashboard);
router.get("/orangtua", protect, checkRole(["orangtua"]), getOrangTuaDashboard);
router.get("/admin", protect, checkRole(["admin"]), getAdminDashboard);
router.get(
  "/superadmin",
  protect,
  checkRole(["superadmin"]),
  getSuperAdminDashboard
);

module.exports = router;
