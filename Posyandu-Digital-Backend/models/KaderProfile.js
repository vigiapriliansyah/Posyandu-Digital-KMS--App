const { DataTypes } = require("sequelize");
const { sequelize } = require("../config/database");
const User = require("./User");
const Posyandu = require("./Posyandu");

const KaderProfile = sequelize.define(
  "KaderProfile",
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    nama_kader: {
      type: DataTypes.STRING,
      allowNull: false,
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
    posyandu_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: Posyandu,
        key: "id",
      },
    },
  },
  {
    tableName: "kader_profiles",
    timestamps: true,
  }
);

// Relasi
User.hasOne(KaderProfile, { foreignKey: "user_id" });
KaderProfile.belongsTo(User, { foreignKey: "user_id" });

Posyandu.hasMany(KaderProfile, { foreignKey: "posyandu_id" });
KaderProfile.belongsTo(Posyandu, { foreignKey: "posyandu_id" });

module.exports = KaderProfile;
