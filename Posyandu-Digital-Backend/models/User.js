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
      // --- PERBAIKAN DI SINI: Gunakan huruf kecil untuk semua peran ---
      type: DataTypes.ENUM("superadmin", "admin", "kader", "orangtua"),
      allowNull: false,
    },
  },
  {
    tableName: "users",
  }
);

(async () => {
  await sequelize.sync();
  console.log("User table synced with correct ENUM roles!");
})();

module.exports = User;
