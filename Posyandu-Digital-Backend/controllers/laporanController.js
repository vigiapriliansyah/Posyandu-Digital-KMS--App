const fs = require("fs-extra");
const path = require("path");
const puppeteer = require("puppeteer");
const hbs = require("handlebars");
const moment = require("moment");
const { Op } = require("sequelize");

const Pengukuran = require("../models/Pengukuran");
const Anak = require("../models/Anak");
const Posyandu = require("../models/Posyandu");
const Desa = require("../models/Desa");

// Helper: Compile file HTML dengan data
const compile = async (templateName, data) => {
  const filePath = path.join(process.cwd(), "templates", `${templateName}.hbs`);
  const html = await fs.readFile(filePath, "utf-8");
  return hbs.compile(html)(data);
};

const cetakLaporanBulanan = async (req, res) => {
  try {
    const { bulan, tahun, posyandu_id } = req.query;

    if (!bulan || !tahun || !posyandu_id) {
      return res.status(400).json({ message: "Parameter bulan, tahun, dan posyandu_id wajib diisi." });
    }

    // 1. Ambil Data Posyandu & Desa untuk Kop Surat
    const posyandu = await Posyandu.findByPk(posyandu_id, {
        include: [{ model: Desa }]
    });

    if (!posyandu) return res.status(404).json({ message: "Posyandu tidak ditemukan" });

    // 2. Filter Tanggal (Awal s/d Akhir Bulan)
    const startDate = moment(`${tahun}-${bulan}-01`).startOf('month').format('YYYY-MM-DD');
    const endDate = moment(`${tahun}-${bulan}-01`).endOf('month').format('YYYY-MM-DD');

    // 3. Ambil Data Pengukuran
    const dataPengukuran = await Pengukuran.findAll({
      where: {
        tanggal_pencatatan: {
          [Op.between]: [startDate, endDate]
        }
      },
      include: [
        { 
            model: Anak, 
            where: { posyandu_id: posyandu_id }, // Pastikan anak dari posyandu yg diminta
            attributes: ['nama_anak', 'jenis_kelamin', 'tanggal_lahir']
        }
      ],
      order: [['tanggal_pencatatan', 'ASC']]
    });

    // 4. Format Data untuk Template
    const reportData = dataPengukuran.map((item, index) => {
        // Parse status gizi dari JSON string
        let giziDesc = "-";
        try {
            const giziObj = JSON.parse(item.status_gizi);
            giziDesc = giziObj.bb_u || "-";
        } catch (e) { giziDesc = item.status_gizi || "-"; }

        return {
            no: index + 1,
            nama_anak: item.Anak.nama_anak,
            jk: item.Anak.jenis_kelamin,
            tgl_ukur: moment(item.tanggal_pencatatan).format("DD-MM-YYYY"),
            umur: item.umur_bulan + " Bln",
            bb: item.berat_badan + " Kg",
            tb: item.tinggi_badan + " Cm",
            status_gizi: giziDesc,
            catatan: item.catatan_petugas || "-"
        };
    });

    // 5. Siapkan Data Template
    const context = {
        nama_posyandu: posyandu.nama_posyandu,
        nama_desa: posyandu.Desa.nama_desa,
        periode: moment(`${tahun}-${bulan}-01`).locale('id').format('MMMM YYYY'),
        tanggal_cetak: moment().locale('id').format('DD MMMM YYYY'),
        data: reportData
    };

    // 6. Generate HTML -> PDF
    const htmlContent = await compile("laporan_bulanan", context);

    const browser = await puppeteer.launch({ 
        headless: "new",
        args: ['--no-sandbox'] 
    });
    const page = await browser.newPage();
    
    await page.setContent(htmlContent);
    const pdfBuffer = await page.pdf({
        format: "A4",
        printBackground: true,
        margin: { top: "20px", bottom: "40px", left: "20px", right: "20px" }
    });

    await browser.close();

    // 7. Kirim PDF ke Client
    res.set({
        "Content-Type": "application/pdf",
        "Content-Disposition": `attachment; filename=Laporan_Posyandu_${bulan}_${tahun}.pdf`,
        "Content-Length": pdfBuffer.length
    });

    res.send(pdfBuffer);

  } catch (error) {
    console.error("Error cetak pdf:", error);
    res.status(500).json({ message: "Gagal mencetak laporan", error: error.message });
  }
};

module.exports = { cetakLaporanBulanan };