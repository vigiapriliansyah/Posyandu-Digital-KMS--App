/**
 * DATA STANDAR WHO (Berat Badan menurut Umur - BB/U)
 * Sumber: Keputusan Menteri Kesehatan RI
 */

// DATA FULL 0-24 Bulan agar perhitungan Backend 100% sama dengan Android
const DATA_WHO_LAKI = [
    [0, 2.1, 2.5, 3.3, 3.9, 4.4], [1, 2.9, 3.4, 4.5, 5.1, 5.8], [2, 3.8, 4.3, 5.6, 6.3, 7.1],
    [3, 4.4, 5.0, 6.4, 7.2, 8.0], [4, 4.9, 5.6, 7.0, 7.8, 8.7], [5, 5.3, 6.0, 7.5, 8.4, 9.3],
    [6, 5.7, 6.4, 7.9, 8.8, 9.8], [7, 6.0, 6.7, 8.3, 9.2, 10.3], [8, 6.3, 7.1, 8.6, 9.6, 10.7],
    [9, 6.5, 7.3, 8.9, 9.9, 11.0], [10, 6.6, 7.4, 9.2, 10.2, 11.4], [11, 6.8, 7.6, 9.4, 10.5, 11.7],
    [12, 6.9, 7.7, 9.6, 10.8, 12.0], [13, 7.1, 7.9, 9.9, 11.0, 12.3], [14, 7.2, 8.1, 10.1, 11.3, 12.6],
    [15, 7.4, 8.3, 10.3, 11.5, 12.8], [16, 7.5, 8.4, 10.5, 11.7, 13.1], [17, 7.7, 8.6, 10.7, 12.0, 13.4],
    [18, 7.7, 8.8, 10.9, 12.2, 13.7], [19, 8.0, 8.9, 11.1, 12.5, 13.9], [20, 8.1, 9.1, 11.3, 12.7, 14.2],
    [21, 8.2, 9.2, 11.5, 12.9, 14.5], [22, 8.4, 9.4, 11.8, 13.2, 14.7], [23, 8.5, 9.5, 12.0, 13.4, 15.0],
    [24, 8.6, 9.7, 12.2, 13.6, 15.3]
];

const DATA_WHO_PEREMPUAN = [
    [0, 2.0, 2.4, 3.2, 3.7, 4.2], [1, 2.7, 3.2, 4.2, 4.8, 5.5], [2, 3.4, 3.9, 5.1, 5.8, 6.6],
    [3, 4.0, 4.5, 5.8, 6.6, 7.5], [4, 4.4, 5.0, 6.4, 7.3, 8.2], [5, 4.8, 5.4, 6.9, 7.8, 8.8],
    [6, 5.1, 5.7, 7.3, 8.2, 9.3], [7, 5.3, 6.0, 7.6, 8.6, 9.8], [8, 5.6, 6.2, 8.0, 9.0, 10.2],
    [9, 5.8, 6.5, 8.2, 9.3, 10.5], [10, 6.0, 6.6, 8.5, 9.6, 10.9], [11, 6.1, 6.8, 8.7, 9.9, 11.2],
    [12, 6.3, 7.0, 8.9, 10.1, 11.5], [13, 6.4, 7.2, 9.2, 10.4, 11.7], [14, 6.6, 7.4, 9.4, 10.6, 12.0],
    [15, 6.7, 7.6, 9.6, 10.9, 12.2], [16, 6.9, 7.7, 9.8, 11.1, 12.5], [17, 7.0, 7.9, 10.0, 11.4, 12.8],
    [18, 7.0, 8.1, 10.2, 11.6, 13.2], [19, 7.3, 8.2, 10.4, 11.8, 13.5], [20, 7.4, 8.4, 10.6, 12.1, 13.7],
    [21, 7.6, 8.6, 10.9, 12.4, 14.0], [22, 7.7, 8.7, 11.1, 12.6, 14.3], [23, 7.8, 8.9, 11.3, 12.8, 14.6],
    [24, 7.9, 9.0, 11.5, 13.0, 14.8]
];

// Helper mencari data referensi terdekat
const getRefData = (tabel, umur) => {
    const found = tabel.find(row => row[0] === umur);
    if (found) return found;
    return tabel.filter(row => row[0] <= umur).pop();
};

const getKBM = (umurBulan) => {
    if (umurBulan === 1) return 800;
    if (umurBulan === 2) return 900;
    if (umurBulan === 3) return 800;
    if (umurBulan === 4) return 600;
    if (umurBulan === 5) return 500;
    if (umurBulan === 6) return 400;
    if (umurBulan >= 7 && umurBulan <= 10) return 300;
    if (umurBulan >= 11 && umurBulan <= 24) return 200;
    return 0;
};

const hitungStatusGizi = (jenisKelamin, umurBulan, berat, tinggi) => {
    const tabelRef = (jenisKelamin === "L") ? DATA_WHO_LAKI : DATA_WHO_PEREMPUAN;
    const refData = getRefData(tabelRef, umurBulan) || tabelRef[0]; 

    const sd3neg = refData[1];
    const sd2neg = refData[2]; 
    const sd2pos = refData[4]; 

    let statusBBU = "Gizi Baik (Normal)";
    let kategoriWarna = "Hijau";

    if (berat < sd3neg) {
        statusBBU = "Gizi Buruk";
        kategoriWarna = "Hitam"; 
    } else if (berat >= sd3neg && berat < sd2neg) {
        statusBBU = "Gizi Kurang";
        kategoriWarna = "Kuning"; 
    } else if (berat >= sd2neg && berat <= sd2pos) {
        statusBBU = "Gizi Baik (Normal)";
        kategoriWarna = "Hijau"; 
    } else if (berat > sd2pos) {
        statusBBU = "Risiko Gizi Lebih";
        kategoriWarna = "Kuning";
    }

    let statusTBU = "Normal";
    if (tinggi > 0) {
        let standarTinggi = 50 + (umurBulan * 2); 
        if (tinggi < (standarTinggi * 0.85)) statusTBU = "Pendek (Stunting)";
    }

    return { bb_u: statusBBU, tb_u: statusTBU, bb_tb: "Normal", warna_kms: kategoriWarna };
};

module.exports = { hitungStatusGizi, getKBM };