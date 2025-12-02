const express = require("express");
const router = express.Router();

// Import controller
const userController = require("../controllers/userController");

// Debugging: Cek apakah fungsi termuat (Akan muncul di terminal saat server start)
if (!userController.createUser) {
  console.error("CRITICAL ERROR: createUser is UNDEFINED in userRoutes.js!");
  console.error("Check userController.js exports!");
}

const { createUser, getUsers, getUserById, updateUser, deleteUser } =
  userController;

const { protect, checkRole } = require("../middleware/authMiddleware");

// Middleware perlindungan
router.use(protect);
router.use(checkRole(["superadmin", "admin"]));

// Definisi Rute
router
  .route("/")
  .post(createUser) // Pastikan ini fungsi, bukan undefined
  .get(getUsers);

router.route("/:id").get(getUserById).put(updateUser).delete(deleteUser);

module.exports = router;
