const Pengukuran = require("../models/Pengukuran");
const Anak = require("../models/Anak");
const User = require("../models/User");
const { hitungStatusGizi, getKBM } = require("../utils/zscore");
const { Op } = require("sequelize");

const createPengukuran = async (req, res) => {
  try {
    const { 
        anak_id, tanggal_pencatatan, umur_bulan, berat_badan, 
        kenaikan_bb_gram, status_naik_turun, asi_eksklusif, catatan 
    } = req.body;

    const anak = await Anak.findByPk(anak_id);
    if (!anak) return res.status(404).json({ message: "Anak tidak ditemukan" });

    let finalUmurBulan = umur_bulan;
    if (finalUmurBulan === undefined || finalUmurBulan === null) {
        const tglLahir = new Date(anak.tanggal_lahir);
        const tglUkur = new Date(tanggal_pencatatan || Date.now());
        finalUmurBulan = (tglUkur.getFullYear() - tglLahir.getFullYear()) * 12 + tglUkur.getMonth() - tglLahir.getMonth();
        if (finalUmurBulan < 0) finalUmurBulan = 0;
    }

    let finalKBM = kenaikan_bb_gram || getKBM(finalUmurBulan);
    let finalNT = status_naik_turun;

    if (!finalNT && finalUmurBulan > 0) {
        const prevBulan = await Pengukuran.findOne({
            where: { anak_id: anak_id, umur_bulan: { [Op.lt]: finalUmurBulan } },
            order: [['umur_bulan', 'DESC']]
        });

        if (prevBulan) {
            const kenaikan = (parseFloat(berat_badan) - prevBulan.berat_badan) * 1000;
            finalNT = (kenaikan >= finalKBM) ? "N" : "T";
        } else {
            finalNT = "B"; 
        }
    } else if (!finalNT) {
        finalNT = "-"; 
    }

    const hasilGizi = hitungStatusGizi(
        anak.jenis_kelamin,
        finalUmurBulan,
        parseFloat(berat_badan),
        0
    );
    
    const dataBaru = await Pengukuran.create({
        anak_id,
        petugas_id: req.user.id,
        tanggal_pencatatan: tanggal_pencatatan || new Date(),
        umur_bulan: finalUmurBulan,
        berat_badan,
        tinggi_badan: null,
        kenaikan_bb_gram: finalKBM,
        status_naik_turun: finalNT,
        asi_eksklusif,
        status_gizi: JSON.stringify(hasilGizi),
        catatan_petugas: catatan,
        is_synced: false
    });

    res.status(201).json({ 
        message: "Berhasil disimpan", 
        data: dataBaru, 
        analisis: hasilGizi 
    });

  } catch (error) { 
    res.status(500).json({ 
        message: "Server Error", 
        error: error.message 
    }); 
  }
};

const getRiwayatAnak = async (req, res) => {
    try {
        const riwayat = await Pengukuran.findAll({
            where: { anak_id: req.params.anakId }, order: [['tanggal_pencatatan', 'DESC']],
            include: [{ model: User, as: 'Petugas', attributes: ['username'] }]
        });
        res.status(200).json(riwayat);
    } catch (error) { res.status(500).json({ message: "Server Error" }); }
};

module.exports = { createPengukuran, getRiwayatAnak };