const { DataTypes } = require("sequelize");
const { sequelize } = require("../config/database");
const OrangTuaProfile = require("./OrangTuaProfile");

const Anak = sequelize.define(
  "Anak",
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    nama_anak: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    nik_anak: {
      type: DataTypes.STRING,
      allowNull: true,
    },
    tempat_lahir: {
      type: DataTypes.STRING,
      allowNull: true,
    },
    tanggal_lahir: {
      type: DataTypes.DATEONLY, // Format YYYY-MM-DD
      allowNull: false,
    },
    jenis_kelamin: {
      type: DataTypes.ENUM("L", "P"),
      allowNull: false,
    },
    berat_badan_lahir: {
      type: DataTypes.FLOAT, // dalam Kg
      allowNull: true,
    },
    tinggi_badan_lahir: {
      type: DataTypes.FLOAT, // dalam Cm
      allowNull: true,
    },
    orangtua_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: OrangTuaProfile,
        key: "id",
      },
    },
  },
  {
    tableName: "anak",
    timestamps: true,
  }
);

// Relasi: Satu Orang Tua punya banyak Anak
OrangTuaProfile.hasMany(Anak, { foreignKey: "orangtua_id" });
Anak.belongsTo(OrangTuaProfile, { foreignKey: "orangtua_id" });

module.exports = Anak;
