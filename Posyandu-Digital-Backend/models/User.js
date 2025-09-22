const { DataTypes } = require("sequelize");
const { sequelize } = require("../config/database"); // Import koneksi db

const User = sequelize.define(
  "User",
  {
    // Model attributes are defined here
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
      type: DataTypes.ENUM("superAdmin", "admin", "kader"),
      allowNull: false,
    },
  },
  {
    // Other model options go here
    tableName: "users",
  }
);

// Ini akan membuat tabel jika belum ada
(async () => {
  await sequelize.sync();
  console.log("User table synced!");
})();

module.exports = User;
