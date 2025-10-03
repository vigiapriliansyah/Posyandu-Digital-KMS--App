const Desa = require("../models/Desa");

/**
 * @desc    Mendapatkan semua desa, bisa difilter berdasarkan kecamatan_id
 * @route   GET /api/desa?kecamatan_id=<id>
 * @access  Private
 */
const getAllDesa = async (req, res) => {
  try {
    const { kecamatan_id } = req.query;
    const whereClause = {};

    if (kecamatan_id) {
      whereClause.kecamatan_id = kecamatan_id;
    }

    const desaList = await Desa.findAll({
      where: whereClause,
      order: [["nama_desa", "ASC"]],
    });
    res.status(200).json(desaList);
  } catch (error) {
    console.error("Error fetching desa:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

/**
 * @desc    Membuat desa baru
 * @route   POST /api/desa
 * @access  Private/SuperAdmin
 */
const createDesa = async (req, res) => {
  const { nama_desa, kecamatan_id } = req.body;

  if (!nama_desa || !kecamatan_id) {
    return res
      .status(400)
      .json({ message: "Nama desa dan ID kecamatan diperlukan." });
  }

  try {
    // findOrCreate akan membuat desa jika belum ada, atau mengembalikan data desa yang sudah ada.
    const [desa, created] = await Desa.findOrCreate({
      where: { nama_desa: nama_desa, kecamatan_id: kecamatan_id },
      defaults: { nama_desa, kecamatan_id },
    });

    if (created) {
      res.status(201).json(desa); // Berhasil dibuat
    } else {
      res.status(200).json(desa); // Ditemukan yang sudah ada
    }
  } catch (error) {
    console.error("Error creating desa:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

module.exports = {
  getAllDesa,
  createDesa,
};
