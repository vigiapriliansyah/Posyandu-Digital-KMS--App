const { DataTypes } = require("sequelize");
const { sequelize } = require("../config/database");

const User = sequelize.define(
  "User",
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    username: {
      type: DataTypes.STRING,
      allowNull: false,
      unique: true,
    },
    password: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    role: {
      type: DataTypes.ENUM("superadmin", "admin", "kader", "orangtua"),
      allowNull: false,
    },
    status: {
        type: DataTypes.ENUM("aktif", "nonaktif", "pending_verification"),
        defaultValue: "aktif",
        allowNull: false
    },
    // --- SESUAI SKEMA ANDA ---
    kode_verifikasi: {
      type: DataTypes.STRING,
      allowNull: true,
      unique: true,
    },
    // is_verified bisa dihapus jika sudah pakai 'status', 
    // tapi untuk kompatibilitas kode lama, kita simpan dulu atau map 'status' ke boolean
    is_verified: {
        type: DataTypes.BOOLEAN,
        defaultValue: false
    }
  },
  {
    tableName: "users",
  }
);

module.exports = User;