const Kecamatan = require("../models/Kecamatan");

/**
 * @desc    Mendapatkan semua data kecamatan
 * @route   GET /api/kecamatan
 * @access  Private (Membutuhkan login)
 */
const getAllKecamatan = async (req, res) => {
  try {
    const kecamatanList = await Kecamatan.findAll({
      order: [["nama_kecamatan", "ASC"]], // Diurutkan berdasarkan abjad
    });
    res.status(200).json(kecamatanList);
  } catch (error) {
    console.error("Error fetching kecamatan:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

module.exports = {
  getAllKecamatan,
};
