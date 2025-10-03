const { DataTypes } = require("sequelize");
const { sequelize } = require("../config/database");
const Desa = require("./Desa");

const Posyandu = sequelize.define(
  "Posyandu",
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    nama_posyandu: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    desa_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: Desa,
        key: "id",
      },
    },
  },
  {
    tableName: "posyandu",
    timestamps: true,
  }
);

// Mendefinisikan relasi: Satu Desa bisa punya banyak Posyandu
Desa.hasMany(Posyandu, { foreignKey: "desa_id" });
Posyandu.belongsTo(Desa, { foreignKey: "desa_id" });

module.exports = Posyandu;
