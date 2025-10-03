require("dotenv").config();
const express = require("express");
const { connectDB, sequelize } = require("./config/database");
const Kecamatan = require("./models/Kecamatan");
const Desa = require("./models/Desa");

const authRoutes = require("./routes/authRoutes");
const dashboardRoutes = require("./routes/dashboardRoutes");
const userRoutes = require("./routes/userRoutes");
const desaRoutes = require("./routes/desaRoutes");
const kecamatanRoutes = require("./routes/kecamatanRoutes");

// --- FUNGSI SEEDER UNTUK KECAMATAN (TETAP SAMA) ---
const seedKecamatan = async () => {
  const daftarKecamatan = [
    "Sagalaherang",
    "Cisalak",
    "Subang",
    "Kalijati",
    "Pabuaran",
    "Purwadadi",
    "Pagaden",
    "Binong",
    "Ciasem",
    "Pusakanagara",
    "Pamanukan",
    "Jalancagak",
    "Blanakan",
    "Tanjungsiang",
    "Compreng",
    "Patokbeusi",
    "Cibogo",
    "Cipunagara",
    "Cijambe",
    "Cipeundeuy",
    "Legonkulon",
    "Cikaum",
    "Serangpanjang",
    "Sukasari",
    "Tambakdahan",
    "Kasomalang",
    "Dawuan",
    "Pagaden Barat",
    "Ciater",
    "Pusakajaya",
  ];

  try {
    // --- DIKEMBALIKAN KE BAHASA INDONESIA ---
    console.log("Memulai seeding data kecamatan...");
    for (const nama of daftarKecamatan) {
      await Kecamatan.findOrCreate({ where: { nama_kecamatan: nama } });
    }
    console.log("Seeding data kecamatan selesai.");
  } catch (error) {
    console.error("Gagal melakukan seeding kecamatan:", error);
  }
};

// --- FUNGSI SEEDER DESA DENGAN DATA LENGKAP ---
const seedDesa = async () => {
  const daftarDesa = [
    // Binong
    { nama: "Binong", kecamatan: "Binong" },
    { nama: "Cicadas", kecamatan: "Binong" },
    { nama: "Citrajaya", kecamatan: "Binong" },
    { nama: "Karangsari", kecamatan: "Binong" },
    { nama: "Karangwangi", kecamatan: "Binong" },
    { nama: "Kediri", kecamatan: "Binong" },
    { nama: "Kihiyang", kecamatan: "Binong" },
    { nama: "Mulyasari", kecamatan: "Binong" },
    { nama: "Nangerang", kecamatan: "Binong" },
    // Blanakan
    { nama: "Blanakan", kecamatan: "Blanakan" },
    { nama: "Cilamaya Girang", kecamatan: "Blanakan" },
    { nama: "Cilamaya Hilir", kecamatan: "Blanakan" },
    { nama: "Jayamukti", kecamatan: "Blanakan" },
    { nama: "Langensari", kecamatan: "Blanakan" },
    { nama: "Muara", kecamatan: "Blanakan" },
    { nama: "Rawamekar", kecamatan: "Blanakan" },
    { nama: "Rawameneng", kecamatan: "Blanakan" },
    { nama: "Tanjungtiga", kecamatan: "Blanakan" },
    // Ciasem
    { nama: "Ciasem Hilir", kecamatan: "Ciasem" },
    { nama: "Ciasem Tengah", kecamatan: "Ciasem" },
    { nama: "Ciasembaru", kecamatan: "Ciasem" },
    { nama: "Dukuh", kecamatan: "Ciasem" },
    { nama: "Jatibaru", kecamatan: "Ciasem" },
    { nama: "Pinangsari", kecamatan: "Ciasem" },
    { nama: "Sukahaji", kecamatan: "Ciasem" },
    { nama: "Sukamandijaya", kecamatan: "Ciasem" },
    { nama: "Ciasem Girang", kecamatan: "Ciasem" },
    // Ciater
    { nama: "Ciater", kecamatan: "Ciater" },
    { nama: "Cibeusi", kecamatan: "Ciater" },
    { nama: "Cibitung", kecamatan: "Ciater" },
    { nama: "Cisaat", kecamatan: "Ciater" },
    { nama: "Nagrak", kecamatan: "Ciater" },
    { nama: "Palasari", kecamatan: "Ciater" },
    { nama: "Sanca", kecamatan: "Ciater" },
    // Cibogo
    { nama: "Belendung", kecamatan: "Cibogo" },
    { nama: "Cibalandong Jaya", kecamatan: "Cibogo" },
    { nama: "Cibogo", kecamatan: "Cibogo" },
    { nama: "Cinangsi", kecamatan: "Cibogo" },
    { nama: "Cisaga", kecamatan: "Cibogo" },
    { nama: "Majasari", kecamatan: "Cibogo" },
    { nama: "Padaasih", kecamatan: "Cibogo" },
    { nama: "Sadawarna", kecamatan: "Cibogo" },
    { nama: "Sumurbarang", kecamatan: "Cibogo" },
    // Cijambe
    { nama: "Bantarsari", kecamatan: "Cijambe" },
    { nama: "Cijambe", kecamatan: "Cijambe" },
    { nama: "Cikadu", kecamatan: "Cijambe" },
    { nama: "Cimenteng", kecamatan: "Cijambe" },
    { nama: "Cirangkong", kecamatan: "Cijambe" },
    { nama: "Gunungtua", kecamatan: "Cijambe" },
    { nama: "Sukahurip", kecamatan: "Cijambe" },
    { nama: "Tanjungwangi", kecamatan: "Cijambe" },
    // Cikaum
    { nama: "Cikaum Barat", kecamatan: "Cikaum" },
    { nama: "Cikaum Timur", kecamatan: "Cikaum" },
    { nama: "Gandasari", kecamatan: "Cikaum" },
    { nama: "Kaunganten", kecamatan: "Cikaum" },
    { nama: "Mekarsari", kecamatan: "Cikaum" },
    { nama: "Pasirmuncang", kecamatan: "Cikaum" },
    { nama: "Sindangsari", kecamatan: "Cikaum" },
    { nama: "Tanjungsari Barat", kecamatan: "Cikaum" },
    { nama: "Tanjungsari Timur", kecamatan: "Cikaum" },
    // Cipeundeuy
    { nama: "Cimayasari", kecamatan: "Cipeundeuy" },
    { nama: "Cipeundeuy", kecamatan: "Cipeundeuy" },
    { nama: "Lengkong", kecamatan: "Cipeundeuy" },
    { nama: "Sawangan", kecamatan: "Cipeundeuy" },
    { nama: "Wantilan", kecamatan: "Cipeundeuy" },
    { nama: "Karangmukti", kecamatan: "Cipeundeuy" },
    { nama: "Kosar", kecamatan: "Cipeundeuy" },
    // Cipunagara
    { nama: "Jati", kecamatan: "Cipunagara" },
    { nama: "Kosambi", kecamatan: "Cipunagara" },
    { nama: "Manyingsal", kecamatan: "Cipunagara" },
    { nama: "Padamulya", kecamatan: "Cipunagara" },
    { nama: "Parigimulya", kecamatan: "Cipunagara" },
    { nama: "Sidajaya", kecamatan: "Cipunagara" },
    { nama: "Sidamulya", kecamatan: "Cipunagara" },
    { nama: "Simpar", kecamatan: "Cipunagara" },
    { nama: "Tanjung", kecamatan: "Cipunagara" },
    { nama: "Wanasari", kecamatan: "Cipunagara" },
    // Cisalak
    { nama: "Cigadog", kecamatan: "Cisalak" },
    { nama: "Cimanggu", kecamatan: "Cisalak" },
    { nama: "Cisalak", kecamatan: "Cisalak" },
    { nama: "Cupunagara", kecamatan: "Cisalak" },
    { nama: "Darmaga", kecamatan: "Cisalak" },
    { nama: "Gardusayang", kecamatan: "Cisalak" },
    { nama: "Mayang", kecamatan: "Cisalak" },
    { nama: "Pakuhaji", kecamatan: "Cisalak" },
    { nama: "Sukakerti", kecamatan: "Cisalak" },
    // Compreng
    { nama: "Compreng", kecamatan: "Compreng" },
    { nama: "Jatimulya", kecamatan: "Compreng" },
    { nama: "Jatireja", kecamatan: "Compreng" },
    { nama: "Kalensari", kecamatan: "Compreng" },
    { nama: "Kiarasari", kecamatan: "Compreng" },
    { nama: "Mekarjaya", kecamatan: "Compreng" },
    { nama: "Sukadana", kecamatan: "Compreng" },
    { nama: "Sukatani", kecamatan: "Compreng" },
    // Dawuan
    { nama: "Batusari", kecamatan: "Dawuan" },
    { nama: "Cisampih", kecamatan: "Dawuan" },
    { nama: "Dawuan Kaler", kecamatan: "Dawuan" },
    { nama: "Dawuan Kidul", kecamatan: "Dawuan" },
    { nama: "Jambelaer", kecamatan: "Dawuan" },
    { nama: "Manyeti", kecamatan: "Dawuan" },
    { nama: "Margasari", kecamatan: "Dawuan" },
    { nama: "Rawalele", kecamatan: "Dawuan" },
    { nama: "Situsari", kecamatan: "Dawuan" },
    { nama: "Sukasari", kecamatan: "Dawuan" },
    // Jalancagak
    { nama: "Bunihayu", kecamatan: "Jalancagak" },
    { nama: "Curugrendeng", kecamatan: "Jalancagak" },
    { nama: "Jalancagak", kecamatan: "Jalancagak" },
    { nama: "Kumpay", kecamatan: "Jalancagak" },
    { nama: "Sarireja", kecamatan: "Jalancagak" },
    { nama: "Tambakan", kecamatan: "Jalancagak" },
    { nama: "Tambakmekar", kecamatan: "Jalancagak" },
    // Kalijati
    { nama: "Banggalamulya", kecamatan: "Kalijati" },
    { nama: "Caracas", kecamatan: "Kalijati" },
    { nama: "Ciruluk", kecamatan: "Kalijati" },
    { nama: "Jalupang", kecamatan: "Kalijati" },
    { nama: "Marengmang", kecamatan: "Kalijati" },
    { nama: "Kaliangsana", kecamatan: "Kalijati" },
    { nama: "Kalijati Barat", kecamatan: "Kalijati" },
    { nama: "Kalijati Timur", kecamatan: "Kalijati" },
    { nama: "Tanggulun Barat", kecamatan: "Kalijati" },
    { nama: "Tanggulun Timur", kecamatan: "Kalijati" },
    // Kasomalang
    { nama: "Bojongloa", kecamatan: "Kasomalang" },
    { nama: "Cimanglid", kecamatan: "Kasomalang" },
    { nama: "Kasomalang Kulon", kecamatan: "Kasomalang" },
    { nama: "Kasomalang Wetan", kecamatan: "Kasomalang" },
    { nama: "Pasanggrahan", kecamatan: "Kasomalang" },
    { nama: "Sindangsari", kecamatan: "Kasomalang" },
    { nama: "Sukamelang", kecamatan: "Kasomalang" },
    { nama: "Tenjolaya", kecamatan: "Kasomalang" },
    // Legonkulon
    { nama: "Bobos", kecamatan: "Legonkulon" },
    { nama: "Karangmulya", kecamatan: "Legonkulon" },
    { nama: "Legon Kulon", kecamatan: "Legonkulon" },
    { nama: "Legon Wetan", kecamatan: "Legonkulon" },
    { nama: "Mayangan", kecamatan: "Legonkulon" },
    { nama: "Pangarengan", kecamatan: "Legonkulon" },
    { nama: "Tegalurung", kecamatan: "Legonkulon" },
    // Pabuaran
    { nama: "Balebandung Jaya", kecamatan: "Pabuaran" },
    { nama: "Cihambulu", kecamatan: "Pabuaran" },
    { nama: "Kadawung", kecamatan: "Pabuaran" },
    { nama: "Karanghegar", kecamatan: "Pabuaran" },
    { nama: "Pabuaran", kecamatan: "Pabuaran" },
    { nama: "Pringkasap", kecamatan: "Pabuaran" },
    { nama: "Salamjaya", kecamatan: "Pabuaran" },
    { nama: "Siluman", kecamatan: "Pabuaran" },
    // Pagaden
    { nama: "Gambarsari", kecamatan: "Pagaden" },
    { nama: "Gembor", kecamatan: "Pagaden" },
    { nama: "Gunungsari", kecamatan: "Pagaden" },
    { nama: "Gunungsembung", kecamatan: "Pagaden" },
    { nama: "Jabong", kecamatan: "Pagaden" },
    { nama: "Kamarung", kecamatan: "Pagaden" },
    { nama: "Neglasari", kecamatan: "Pagaden" },
    { nama: "Pagaden", kecamatan: "Pagaden" },
    { nama: "Sukamulya", kecamatan: "Pagaden" },
    { nama: "Sumbersari", kecamatan: "Pagaden" },
    // Pagaden Barat
    { nama: "Balingbing", kecamatan: "Pagaden Barat" },
    { nama: "Bendungan", kecamatan: "Pagaden Barat" },
    { nama: "Cidadap", kecamatan: "Pagaden Barat" },
    { nama: "Cidahu", kecamatan: "Pagaden Barat" },
    { nama: "Margahayu", kecamatan: "Pagaden Barat" },
    { nama: "Mekarwangi", kecamatan: "Pagaden Barat" },
    { nama: "Munjul", kecamatan: "Pagaden Barat" },
    { nama: "Pangsor", kecamatan: "Pagaden Barat" },
    { nama: "Sumurgintung", kecamatan: "Pagaden Barat" },
    // Pamanukan
    { nama: "Bongas", kecamatan: "Pamanukan" },
    { nama: "Lengkongjaya", kecamatan: "Pamanukan" },
    { nama: "Mulyasari", kecamatan: "Pamanukan" },
    { nama: "Pamanukan", kecamatan: "Pamanukan" },
    { nama: "Pamanukan Hilir", kecamatan: "Pamanukan" },
    { nama: "Pamanukan Sebrang", kecamatan: "Pamanukan" },
    { nama: "Rancahilir", kecamatan: "Pamanukan" },
    { nama: "Rancasari", kecamatan: "Pamanukan" },
    // Patokbeusi
    { nama: "Ciberes", kecamatan: "Patokbeusi" },
    { nama: "Gempolsari", kecamatan: "Patokbeusi" },
    { nama: "Jatiragas Hilir", kecamatan: "Patokbeusi" },
    { nama: "Rancaasih", kecamatan: "Patokbeusi" },
    { nama: "Rancabango", kecamatan: "Patokbeusi" },
    { nama: "Rancajaya", kecamatan: "Patokbeusi" },
    { nama: "Rancamulya", kecamatan: "Patokbeusi" },
    { nama: "Tambakjati", kecamatan: "Patokbeusi" },
    { nama: "Tanjungrasa", kecamatan: "Patokbeusi" },
    { nama: "Tanjungrasa Kidul", kecamatan: "Patokbeusi" },
    // Purwadadi
    { nama: "Belendung", kecamatan: "Purwadadi" },
    { nama: "Koranji", kecamatan: "Purwadadi" },
    { nama: "Pagon", kecamatan: "Purwadadi" },
    { nama: "Panyingkiran", kecamatan: "Purwadadi" },
    { nama: "Parapatan", kecamatan: "Purwadadi" },
    { nama: "Pasirbungur", kecamatan: "Purwadadi" },
    { nama: "Purwadadi Barat", kecamatan: "Purwadadi" },
    { nama: "Purwadadi Timur", kecamatan: "Purwadadi" },
    { nama: "Rancamahi", kecamatan: "Purwadadi" },
    { nama: "Wanakerta", kecamatan: "Purwadadi" },
    // Pusakajaya
    { nama: "Bojong Tengah", kecamatan: "Pusakajaya" },
    { nama: "Bojongjaya", kecamatan: "Pusakajaya" },
    { nama: "Cigugur", kecamatan: "Pusakajaya" },
    { nama: "Cigugur Kaler", kecamatan: "Pusakajaya" },
    { nama: "Karanganyar", kecamatan: "Pusakajaya" },
    { nama: "Kebondanas", kecamatan: "Pusakajaya" },
    { nama: "Pusakajaya", kecamatan: "Pusakajaya" },
    { nama: "Rangdu", kecamatan: "Pusakajaya" },
    // Pusakanagara
    { nama: "Gempol", kecamatan: "Pusakanagara" },
    { nama: "Kalentambo", kecamatan: "Pusakanagara" },
    { nama: "Kotasari", kecamatan: "Pusakanagara" },
    { nama: "Mundusari", kecamatan: "Pusakanagara" },
    { nama: "Patimban", kecamatan: "Pusakanagara" },
    { nama: "Pusakaratu", kecamatan: "Pusakanagara" },
    { nama: "Rancadaka", kecamatan: "Pusakanagara" },
    // Sagalaherang
    { nama: "Cicadas", kecamatan: "Sagalaherang" },
    { nama: "Curugagung", kecamatan: "Sagalaherang" },
    { nama: "Dayeuhkolot", kecamatan: "Sagalaherang" },
    { nama: "Leles", kecamatan: "Sagalaherang" },
    { nama: "Sagalaherang", kecamatan: "Sagalaherang" },
    { nama: "Sagalaherang Kaler", kecamatan: "Sagalaherang" },
    { nama: "Sukamandi", kecamatan: "Sagalaherang" },
    // Serangpanjang
    { nama: "Cijengkol", kecamatan: "Serangpanjang" },
    { nama: "Cikujang", kecamatan: "Serangpanjang" },
    { nama: "Cintamekar", kecamatan: "Serangpanjang" },
    { nama: "Cipancar", kecamatan: "Serangpanjang" },
    { nama: "Ponggang", kecamatan: "Serangpanjang" },
    { nama: "Talagasari", kecamatan: "Serangpanjang" },
    // Subang
    { nama: "Cigadung", kecamatan: "Subang" },
    { nama: "Dangdeur", kecamatan: "Subang" },
    { nama: "Karanganyar", kecamatan: "Subang" },
    { nama: "Parung", kecamatan: "Subang" },
    { nama: "Pasirkareumbi", kecamatan: "Subang" },
    { nama: "Soklat", kecamatan: "Subang" },
    { nama: "Sukamelang", kecamatan: "Subang" },
    { nama: "Wanareja", kecamatan: "Subang" },
    // Sukasari
    { nama: "Anggasari", kecamatan: "Sukasari" },
    { nama: "Batangsari", kecamatan: "Sukasari" },
    { nama: "Curugreja", kecamatan: "Sukasari" },
    { nama: "Mandalawangi", kecamatan: "Sukasari" },
    { nama: "Sukamaju", kecamatan: "Sukasari" },
    { nama: "Sukareja", kecamatan: "Sukasari" },
    { nama: "Sukasari", kecamatan: "Sukasari" },
    // Tambakdahan
    { nama: "Bojonegara", kecamatan: "Tambakdahan" },
    { nama: "Bojongkeding", kecamatan: "Tambakdahan" },
    { nama: "Gardumukti", kecamatan: "Tambakdahan" },
    { nama: "Kertajaya", kecamatan: "Tambakdahan" },
    { nama: "Mariuk", kecamatan: "Tambakdahan" },
    { nama: "Rancaudik", kecamatan: "Tambakdahan" },
    { nama: "Tambakdahan", kecamatan: "Tambakdahan" },
    { nama: "Tanjungrasa", kecamatan: "Tambakdahan" },
    { nama: "Wanajaya", kecamatan: "Tambakdahan" },
    // Tanjungsiang
    { nama: "Buniara", kecamatan: "Tanjungsiang" },
    { nama: "Cibuluh", kecamatan: "Tanjungsiang" },
    { nama: "Cikawung", kecamatan: "Tanjungsiang" },
    { nama: "Cimeuhmal", kecamatan: "Tanjungsiang" },
    { nama: "Gandasoli", kecamatan: "Tanjungsiang" },
    { nama: "Kawungluwuk", kecamatan: "Tanjungsiang" },
    { nama: "Rancamanggung", kecamatan: "Tanjungsiang" },
    { nama: "Sindanglaya", kecamatan: "Tanjungsiang" },
    { nama: "Sirap", kecamatan: "Tanjungsiang" },
    { nama: "Tanjung Siang", kecamatan: "Tanjungsiang" },
  ];

  try {
    // --- DIKEMBALIKAN KE BAHASA INDONESIA ---
    console.log("Memulai seeding data desa...");
    for (const desaInfo of daftarDesa) {
      const kecamatan = await Kecamatan.findOne({
        where: { nama_kecamatan: desaInfo.kecamatan },
      });
      if (kecamatan) {
        await Desa.findOrCreate({
          where: {
            nama_desa: desaInfo.nama,
            kecamatan_id: kecamatan.id,
          },
        });
      } else {
        console.warn(
          `Peringatan: Kecamatan "${desaInfo.kecamatan}" untuk desa "${desaInfo.nama}" tidak ditemukan.`
        );
      }
    }
    console.log("Seeding data desa selesai.");
  } catch (error) {
    console.error("Gagal melakukan seeding desa:", error);
  }
};

connectDB().then(() => {
  sequelize.sync().then(async () => {
    await seedKecamatan();
    await seedDesa();
  });
});

const app = express();
app.use(express.json());

app.use("/api/auth", authRoutes);
app.use("/api/dashboard", dashboardRoutes);
app.use("/api/users", userRoutes);
app.use("/api/desa", desaRoutes);
app.use("/api/kecamatan", kecamatanRoutes);

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server is running on port http://localhost:${PORT}`);
});
