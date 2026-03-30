const { DataTypes } = require("sequelize");
const { sequelize } = require("../config/database");
const Anak = require("./Anak");
const User = require("./User");

const Pengukuran = sequelize.define(
  "Pengukuran",
  {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    anak_id: { type: DataTypes.INTEGER, allowNull: false, references: { model: Anak, key: "id" } },
    petugas_id: { type: DataTypes.INTEGER, allowNull: false, references: { model: User, key: "id" } },
    tanggal_pencatatan: { type: DataTypes.DATEONLY, allowNull: false, defaultValue: DataTypes.NOW },
    umur_bulan: { type: DataTypes.INTEGER, allowNull: false },
    berat_badan: { type: DataTypes.FLOAT, allowNull: false },
    tinggi_badan: { type: DataTypes.FLOAT, allowNull: true },
    kenaikan_bb_gram: { type: DataTypes.INTEGER, allowNull: true },
    status_naik_turun: { type: DataTypes.STRING, allowNull: true },
    asi_eksklusif: { type: DataTypes.BOOLEAN, allowNull: true },
    status_gizi: { type: DataTypes.STRING, allowNull: true },
    catatan_petugas: { type: DataTypes.TEXT, allowNull: true },
    is_synced: { type: DataTypes.BOOLEAN, defaultValue: false, allowNull: false }
  },
  { tableName: "pencatatan_kms", timestamps: true }
);

Anak.hasMany(Pengukuran, { foreignKey: "anak_id" });
Pengukuran.belongsTo(Anak, { foreignKey: "anak_id" });
User.hasMany(Pengukuran, { foreignKey: "petugas_id" });
Pengukuran.belongsTo(User, { foreignKey: "petugas_id", as: "Petugas" });

module.exports = Pengukuran;