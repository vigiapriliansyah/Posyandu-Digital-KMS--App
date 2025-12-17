const { DataTypes } = require("sequelize");
const { sequelize } = require("../config/database");
const User = require("./User");
const Anak = require("./Anak");

// Ini adalah tabel 'Jembatan' (Pivot Table)
const OrangTuaAnak = sequelize.define(
  "OrangTuaAnak",
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    // ID User si Ibu/Ayah
    user_id_orangtua: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: User,
        key: "id",
      },
    },
    // ID si Anak
    anak_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: Anak,
        key: "id",
      },
    },
  },
  {
    tableName: "orangtua_anak",
    timestamps: false, // Tabel jembatan biasanya tidak butuh created_at
  }
);

// Definisi Relasi (Wajib ada agar controller tidak error)
User.belongsToMany(Anak, { through: OrangTuaAnak, foreignKey: "user_id_orangtua" });
Anak.belongsToMany(User, { through: OrangTuaAnak, foreignKey: "anak_id" });

module.exports = OrangTuaAnak;