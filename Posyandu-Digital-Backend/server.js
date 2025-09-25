require("dotenv").config();
const express = require("express");
const { connectDB } = require("./config/database");
const authRoutes = require("./routes/authRoutes");
const { protect, checkRole } = require("./middleware/authMiddleware");
const dashboardRoutes = require("./routes/dashboardRoutes");

connectDB();

const app = express();

app.use(express.json());
app.use("/api/auth", authRoutes);
app.use("/api/dashboard", dashboardRoutes);

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
  console.log(`Server is running on port http://localhost:${PORT}`);
});
