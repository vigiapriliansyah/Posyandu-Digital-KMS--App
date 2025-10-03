const { DataTypes } = require("sequelize");
const { sequelize } = require("../config/database");
const Kecamatan = require("./Kecamatan"); // Pastikan mengimpor Kecamatan

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
        model: Kecamatan, // Menunjuk ke model Kecamatan
        key: "id",
      },
    },
  },
  {
    tableName: "desa",
    timestamps: true,
    // Menambahkan unique constraint untuk kombinasi nama_desa dan kecamatan_id
    // Ini mencegah adanya "Desa A" yang sama dua kali di "Kecamatan B"
    indexes: [
      {
        unique: true,
        fields: ["nama_desa", "kecamatan_id"],
      },
    ],
  }
);

// Mendefinisikan relasi: Satu Kecamatan punya banyak Desa
Kecamatan.hasMany(Desa, { foreignKey: "kecamatan_id" });
Desa.belongsTo(Kecamatan, { foreignKey: "kecamatan_id" });

module.exports = Desa;
