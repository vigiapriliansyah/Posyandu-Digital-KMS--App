const { DataTypes } = require("sequelize");
const { sequelize } = require("../config/database");
const Posyandu = require("./Posyandu");

const Anak = sequelize.define(
  "Anak",
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    nik_anak: {
      type: DataTypes.STRING,
      allowNull: true,
      unique: true,
    },
    nama_anak: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    tanggal_lahir: {
      type: DataTypes.DATEONLY,
      allowNull: false,
    },
    jenis_kelamin: {
      type: DataTypes.ENUM("L", "P"),
      allowNull: false,
    },
    posyandu_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: Posyandu,
        key: "id",
      },
    },
    status_anak: {
      type: DataTypes.ENUM("aktif", "pindah", "lulus", "meninggal"),
      defaultValue: "aktif",
      allowNull: false,
    },
  },
  {
    tableName: "anak",
    timestamps: true,
  }
);

// Relasi: Anak milik satu Posyandu
Posyandu.hasMany(Anak, { foreignKey: "posyandu_id" });
Anak.belongsTo(Posyandu, { foreignKey: "posyandu_id" });

module.exports = Anak;