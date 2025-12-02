const { DataTypes } = require("sequelize");
const { sequelize } = require("../config/database");
const Kecamatan = require("./Kecamatan");

const Desa = sequelize.define(
  "Desa",
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    nama_desa: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    kecamatan_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: Kecamatan,
        key: "id",
      },
    },
  },
  {
    tableName: "desa",
    timestamps: true,
    // PERBAIKAN: Index komposit (nama_desa + kecamatan_id harus unik)
    // Artinya: Di satu kecamatan, tidak boleh ada 2 desa dengan nama sama.
    // TAPI: Satu kecamatan boleh punya banyak desa dengan nama berbeda.
    indexes: [
      {
        unique: true,
        fields: ["nama_desa", "kecamatan_id"], // Kombinasi keduanya yang dicek
      },
    ],
  }
);

// Relasi
Kecamatan.hasMany(Desa, { foreignKey: "kecamatan_id" });
Desa.belongsTo(Kecamatan, { foreignKey: "kecamatan_id" });

module.exports = Desa;
