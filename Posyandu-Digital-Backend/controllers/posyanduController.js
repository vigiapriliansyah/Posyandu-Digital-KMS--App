const Posyandu = require("../models/Posyandu");

/**
 * @desc    Mendapatkan semua posyandu, bisa difilter berdasarkan desa_id
 * @route   GET /api/posyandu?desa_id=<id>
 * @access  Private
 */
const getAllPosyandu = async (req, res) => {
  try {
    const { desa_id } = req.query;
    const whereClause = {};

    if (desa_id) {
      whereClause.desa_id = desa_id;
    }

    const posyanduList = await Posyandu.findAll({
      where: whereClause,
      order: [["nama_posyandu", "ASC"]],
    });
    res.status(200).json(posyanduList);
  } catch (error) {
    console.error("Error fetching posyandu:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

/**
 * @desc    Membuat posyandu baru
 * @route   POST /api/posyandu
 * @access  Private/SuperAdmin
 */
const createPosyandu = async (req, res) => {
  const { nama_posyandu, desa_id } = req.body;

  if (!nama_posyandu || !desa_id) {
    return res
      .status(400)
      .json({ message: "Nama posyandu dan ID desa diperlukan." });
  }

  try {
    const [posyandu, created] = await Posyandu.findOrCreate({
      where: { nama_posyandu: nama_posyandu, desa_id: desa_id },
      defaults: { nama_posyandu, desa_id },
    });

    if (created) {
      res.status(201).json(posyandu);
    } else {
      res.status(200).json(posyandu);
    }
  } catch (error) {
    console.error("Error creating posyandu:", error);
    res.status(500).json({ message: "Server Error" });
  }
};

module.exports = {
  getAllPosyandu,
  createPosyandu,
};
