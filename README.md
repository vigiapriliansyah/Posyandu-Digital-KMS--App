## Proyek Posyandu Digital

Selamat datang di repository Proyek Posyandu Digital. Ini adalah monorepo berisi dua proyek utama: aplikasi mobile untuk kader Posyandu (Frontend) dan server pengelola data (Backend).

### 📝 Deskripsi Proyek

Aplikasi ini bertujuan mendigitalisasi pencatatan dan pemantauan tumbuh kembang balita di Posyandu. Dengan pendekatan offline-first, kader dapat mencatat penimbangan (berat badan, tinggi badan, dll.) tanpa koneksi internet. Data akan tersinkron otomatis ke server saat perangkat kembali online.

### 📂 Struktur Repository

Repository ini berisi dua folder utama:

- `PosyanduDigital/`: Proyek Frontend aplikasi Android menggunakan Jetpack Compose.
- `Posyandu-Digital-Backend/`: Proyek Backend server menggunakan Express.js.

Setiap proyek memiliki `.gitignore` dan konfigurasi masing-masing.

### 🚀 Teknologi yang Digunakan

#### Frontend (`PosyanduDigital`)

- **Bahasa**: Kotlin
- **UI**: Jetpack Compose
- **Arsitektur**: MVVM (Model-View-ViewModel)
- **Database Lokal**: Room
- **Networking**: Retrofit & OkHttp
- **Background Jobs**: WorkManager
- **Navigasi**: Jetpack Navigation Component

#### Backend (`Posyandu-Digital-Backend`)

- **Framework**: Express.js
- **Bahasa**: JavaScript (Node.js)
- **Database**: SQLite (development)
- **Otentikasi**: JWT & bcrypt.js
- **ORM**: Sequelize

### ⚙️ Cara Menjalankan Proyek

#### 1) Menjalankan Backend Server

Pastikan Node.js dan npm sudah terpasang.

Masuk ke folder backend dan pasang dependensi:

```bash
cd Posyandu-Digital-Backend
npm install
```

Buat file `.env` di dalam folder `Posyandu-Digital-Backend` dengan isi contoh berikut:

```env
PORT=3000
JWT_SECRET=kunci-rahasia-anda-yang-sangat-aman
```

Jalankan server (mode pengembangan):

```bash
nodemon server.js
```

Server berjalan di `http://localhost:3000`.

#### 2) Menjalankan Frontend (Aplikasi Android)

Pastikan Android Studio sudah terpasang.

Langkah membuka proyek Android:

1. Buka Android Studio
2. Pilih Open
3. Arahkan ke folder `PosyanduDigital` (bukan folder induk) untuk impor dan build proyek

Pastikan server backend berjalan. Aplikasi Android berkomunikasi dengan `http://10.0.2.2:3000` (alamat khusus emulator Android).

Menjalankan aplikasi:

1. Pilih target (Emulator atau perangkat fisik)
2. Klik tombol Run 'app' (ikon ▶️ hijau)

### 🔑 API Endpoints Utama (Otentikasi)

Gunakan Postman atau alat sejenis untuk berinteraksi dengan backend.

- **POST** `/api/auth/register`: Mendaftarkan pengguna baru.

  - Body (JSON):
    ```json
    { "username": "namauser", "password": "passwordnya", "role": "kader" }
    ```
  - Role tersedia: `superAdmin`, `admin`, `kader`.

- **POST** `/api/auth/login`: Mendapatkan token login.
  - Body (JSON):
    ```json
    { "username": "namauser", "password": "passwordnya" }
    ```
