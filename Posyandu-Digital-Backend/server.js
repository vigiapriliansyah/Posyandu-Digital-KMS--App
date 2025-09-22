require("dotenv").config();
const express = require("express");
const { connectDB } = require("./config/database");
const authRoutes = require("./routes/authRoutes");
const { protect, checkRole } = require("./middleware/authMiddleware");

// Koneksikan ke database
connectDB();

const app = express();

// --- TAMBAHKAN BARIS INI ---
// Middleware untuk mengurai (parse) body JSON dari request.
// Ini akan membuat req.body tersedia untuk Anda.
app.use(express.json());
// --------------------------

// Gunakan rute setelah middleware JSON
app.use("/api/auth", authRoutes);

// ... (Rute untuk testing tetap sama)
app.get("/api/test/kader", protect, checkRole(["kader"]), (req, res) => {
  res.status(200).json({ message: "Welcome Kader! You can access this." });
});

app.get(
  "/api/test/admin",
  protect,
  checkRole(["admin", "superAdmin"]),
  (req, res) => {
    res
      .status(200)
      .json({ message: "Welcome Admin/SuperAdmin! You can access this." });
  }
);

app.get(
  "/api/test/superadmin",
  protect,
  checkRole(["superAdmin"]),
  (req, res) => {
    res
      .status(200)
      .json({ message: "Welcome SuperAdmin! Only you can see this." });
  }
);

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
