const { Sequelize } = require("sequelize");
require("dotenv").config(); // Load password/user dari file .env

// Konfigurasi untuk MySQL (Laragon/XAMPP)
const sequelize = new Sequelize(
  process.env.DB_NAME, // Nama Database (misal: posyandu_db)
  process.env.DB_USER, // User (biasanya: root)
  process.env.DB_PASS, // Password (biasanya kosong di Laragon)
  {
    host: process.env.DB_HOST, // localhost
    dialect: "mysql",          // PENTING: Ganti ke mysql
    logging: false,            // Supaya terminal tidak penuh log SQL
    timezone: "+07:00"         // Waktu Indonesia Barat
  }
);

// Tes koneksi
const connectDB = async () => {
  try {
    await sequelize.authenticate();
    console.log("✅ Berhasil terhubung ke Database MySQL (Laragon).");
  } catch (error) {
    console.error("❌ Gagal terhubung ke database:", error);
  }
};

module.exports = { sequelize, connectDB };