const getKaderDashboard = (req, res) => {
  const dummyAnakData = [
    { id: 1, nama_anak: "Budi Santoso", status_gizi: "Normal" },
    { id: 2, nama_anak: "Ani Lestari", status_gizi: "Kurang Gizi" },
    { id: 3, nama_anak: "Charlie", status_gizi: "Normal" },
  ];

  res.status(200).json({
    nama_posyandu: "Posyandu Kamboja 1",
    jumlah_anak: dummyAnakData.length,
    daftar_anak: dummyAnakData,
  });
};

const getAdminDashboard = async (req, res) => {
  res.status(200).json({
    nama_desa: "Desa Mawar",
    statistik: {
      jumlah_posyandu: 3,
      jumlah_kader: 15,
      jumlah_anak: 128,
      gizi_buruk: 5,
      stunting: 2,
    },
  });
};

const getSuperAdminDashboard = async (req, res) => {
  res.status(200).json({
    statistik_nasional: {
      total_pengguna: 5320,
      total_admin_desa: 150,
      total_kader: 2500,
      total_anak_terdata: 50000,
    },
  });
};

const getOrangTuaDashboard = async (req, res) => {
  res.status(200).json({
    anak: {
      nama_anak: "Budi Santoso",
      umur_bulan: 12,
    },
    kms_terakhir: {
      tanggal_pencatatan: "2025-09-25",
      berat_badan: 9.5,
      tinggi_badan: 75.5,
      status_gizi: "Normal",
    },
  });
};

module.exports = {
  getKaderDashboard,
  getAdminDashboard,
  getSuperAdminDashboard,
  getOrangTuaDashboard,
};
