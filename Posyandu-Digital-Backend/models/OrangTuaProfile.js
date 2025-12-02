const { DataTypes } = require("sequelize");
const { sequelize } = require("../config/database");
const User = require("./User");
const Posyandu = require("./Posyandu");

const OrangTuaProfile = sequelize.define(
  "OrangTuaProfile",
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    nama_ibu: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    nik_ibu: {
      type: DataTypes.STRING,
      allowNull: true,
    },
    no_hp: {
      type: DataTypes.STRING,
      allowNull: true,
    },
    alamat: {
      type: DataTypes.TEXT,
      allowNull: true,
    },
    user_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      unique: true,
      references: {
        model: User,
        key: "id",
      },
    },
    // Orang tua mendaftar di posyandu mana
    posyandu_id: {
      type: DataTypes.INTEGER,
      allowNull: true,
      references: {
        model: Posyandu,
        key: "id",
      },
    },
  },
  {
    tableName: "orangtua_profiles",
    timestamps: true,
  }
);

// Relasi
User.hasOne(OrangTuaProfile, { foreignKey: "user_id" });
OrangTuaProfile.belongsTo(User, { foreignKey: "user_id" });

Posyandu.hasMany(OrangTuaProfile, { foreignKey: "posyandu_id" });
OrangTuaProfile.belongsTo(Posyandu, { foreignKey: "posyandu_id" });

module.exports = OrangTuaProfile;
