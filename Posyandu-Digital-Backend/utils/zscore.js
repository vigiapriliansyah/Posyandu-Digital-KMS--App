// --- DATA STANDAR WHO (BB/U) Sederhana ---
const DATA_WHO_LAKI = [[0,2.1,2.5,3.3,3.9,4.4], [1,2.9,3.4,4.5,5.1,5.8], [2,3.8,4.3,5.6,6.3,7.1], [3,4.4,5.0,6.4,7.2,8.0], [4,4.9,5.6,7.0,7.8,8.7], [5,5.3,6.0,7.5,8.4,9.3], [6,5.7,6.4,7.9,8.8,9.8], [12,6.9,7.7,9.6,10.8,12.0], [24,8.6,9.7,12.2,13.6,15.3], [36,10.0,11.3,14.3,16.0,18.3], [48,11.2,12.7,16.3,18.3,21.2], [60,12.1,14.1,18.3,21.0,24.2]];
const DATA_WHO_PEREMPUAN = [[0,2.0,2.4,3.2,3.7,4.2], [1,2.7,3.2,4.2,4.8,5.5], [2,3.4,3.9,5.1,5.8,6.6], [3,4.0,4.5,5.8,6.6,7.5], [4,4.4,5.0,6.4,7.3,8.2], [5,4.8,5.4,6.9,7.8,8.8], [6,5.1,5.7,7.3,8.2,9.3], [12,6.3,7.0,8.9,10.1,11.5], [24,7.9,9.0,11.5,13.0,14.8], [36,9.4,10.8,13.9,15.8,18.1], [48,10.8,12.3,16.1,18.5,21.5], [60,11.8,13.7,18.2,21.2,24.9]];

const getRefData = (tabel, umur) => tabel.find(row => row[0] === umur) || tabel.filter(row => row[0] <= umur).pop() || tabel[0];

// --- LOGIKA KMS ASLI (Kenaikan Berat Minimal dalam Gram) ---
// Angka ini sesuai dengan tabel bawah di buku KMS
const getKBM = (umurBulan) => {
    if (umurBulan === 1) return 800;
    if (umurBulan === 2) return 900;
    if (umurBulan === 3) return 800;
    if (umurBulan === 4) return 600;
    if (umurBulan === 5) return 500;
    if (umurBulan === 6) return 400;
    if (umurBulan >= 7 && umurBulan <= 10) return 300;
    if (umurBulan >= 11 && umurBulan <= 24) return 200;
    return 0; // Lebih dari 24 bulan biasanya kurvanya landai/berbeda
};

const hitungStatusGizi = (jenisKelamin, umurBulan, berat, tinggi) => {
    const tabelRef = (jenisKelamin === "L") ? DATA_WHO_LAKI : DATA_WHO_PEREMPUAN;
    const refData = getRefData(tabelRef, umurBulan);
    
    // refData: [umur, -3SD, -2SD, Median, +2SD, +3SD]
    const sd3neg = refData[1], sd2neg = refData[2], sd2pos = refData[4];

    let statusBBU = "Gizi Baik", kategoriWarna = "Hijau";
    if (berat < sd3neg) { statusBBU = "Gizi Buruk"; kategoriWarna = "Hitam"; }
    else if (berat < sd2neg) { statusBBU = "Gizi Kurang"; kategoriWarna = "Kuning"; }
    else if (berat > sd2pos) { statusBBU = "Risiko Gizi Lebih"; kategoriWarna = "Kuning"; }

    let statusTBU = "Normal";
    if (tinggi > 0) {
        let standarTinggi = 50 + (umurBulan * 2); 
        if (tinggi < (standarTinggi * 0.85)) statusTBU = "Pendek (Stunting)";
    }

    return { bb_u: statusBBU, tb_u: statusTBU, bb_tb: "Normal", warna_kms: kategoriWarna };
};

module.exports = { hitungStatusGizi, getKBM };