const { DataTypes } = require("sequelize");
const { sequelize } = require("../config/database");
const User = require("./User");
const Desa = require("./Desa");

const AdminProfile = sequelize.define(
  "AdminProfile",
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    nama_admin: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    // Foreign key untuk menghubungkan ke tabel users
    user_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      unique: true, // Satu user hanya boleh punya satu profil admin
      references: {
        model: User,
        key: "id",
      },
    },
    // Foreign key untuk menghubungkan ke tabel desa
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
    tableName: "admin_profiles",
    timestamps: true,
  }
);

// Mendefinisikan relasi:
// Satu User memiliki satu AdminProfile
User.hasOne(AdminProfile, { foreignKey: "user_id" });
AdminProfile.belongsTo(User, { foreignKey: "user_id" });

// Satu Desa bisa memiliki banyak AdminProfile (meskipun biasanya satu)
Desa.hasMany(AdminProfile, { foreignKey: "desa_id" });
AdminProfile.belongsTo(Desa, { foreignKey: "desa_id" });

module.exports = AdminProfile;
