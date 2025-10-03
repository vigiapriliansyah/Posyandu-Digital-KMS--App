const { DataTypes } = require("sequelize");
const { sequelize } = require("../config/database");

const Kecamatan = sequelize.define(
  "Kecamatan",
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    nama_kecamatan: {
      type: DataTypes.STRING,
      allowNull: false,
      unique: true,
    },
  },
  {
    tableName: "kecamatan",
    timestamps: true,
  }
);

module.exports = Kecamatan;
