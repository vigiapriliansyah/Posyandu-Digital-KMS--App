Proyek Posyandu Digital
Selamat datang di repository Proyek Posyandu Digital. Ini adalah sebuah monorepo yang berisi dua proyek utama: aplikasi mobile untuk kader Posyandu (Frontend) dan server yang mengelola data (Backend).

📝 Deskripsi Proyek
Aplikasi ini bertujuan untuk mendigitalisasi proses pencatatan dan pemantauan tumbuh kembang balita di Posyandu. Dengan pendekatan offline-first, kader dapat mencatat data penimbangan (berat badan, tinggi badan, dll.) bahkan di lokasi tanpa koneksi internet. Data akan disinkronkan secara otomatis ke server pusat ketika perangkat kembali online.

📂 Struktur Repository
Repository ini berisi dua folder utama:

PosyanduDigital/ - Proyek Frontend aplikasi mobile Android yang dibangun menggunakan Jetpack Compose.

Posyandu-Digital-Backend/ - Proyek Backend server yang dibangun menggunakan Express.js.

Setiap proyek memiliki file .gitignore dan konfigurasinya masing-masing.

🚀 Teknologi yang Digunakan
Frontend (PosyanduDigital)
Bahasa: Kotlin

UI Framework: Jetpack Compose

Arsitektur: MVVM (Model-View-ViewModel)

Database Lokal: Room

Networking: Retrofit & OkHttp

Background Jobs: WorkManager

Navigasi: Jetpack Navigation Component

Backend (Posyandu-Digital-Backend)
Framework: Express.js

Bahasa: JavaScript (Node.js)

Database: SQLite (untuk development)

Otentikasi: JWT (JSON Web Tokens) & bcrypt.js

ORM: Sequelize

⚙️ Cara Menjalankan Proyek
1. Menjalankan Backend Server
Pastikan Anda sudah menginstal Node.js dan npm.

Masuk ke folder backend:

cd Posyandu-Digital-Backend

Install semua dependensi:

npm install

Buat file .env:
Buat sebuah file bernama .env di dalam folder Posyandu-Digital-Backend dan isi dengan konfigurasi berikut:

PORT=3000
JWT_SECRET=kunci-rahasia-anda-yang-sangat-aman

Jalankan server:

nodemon server.js

Server akan berjalan di http://localhost:3000.

2. Menjalankan Frontend (Aplikasi Android)
Pastikan Anda sudah menginstal Android Studio.

Buka proyek Android:

Buka Android Studio.

Pilih Open.

Arahkan dan pilih folder PosyanduDigital (bukan folder induk). Android Studio akan mengimpor dan membangun proyek.

Pastikan Server Backend Berjalan:
Aplikasi Android akan berkomunikasi dengan server di http://10.0.2.2:3000. Pastikan server backend Anda sudah berjalan dari langkah sebelumnya.

Jalankan Aplikasi:

Pilih target (Emulator atau perangkat fisik).

Klik tombol Run 'app' (ikon ▶️ hijau).

🔑 API Endpoints Utama (Otentikasi)
Gunakan Postman atau alat sejenis untuk berinteraksi dengan backend.

POST /api/auth/register: Mendaftarkan pengguna baru.

Body (JSON): { "username": "namauser", "password": "passwordnya", "role": "kader" }

Role yang tersedia: superAdmin, admin, kader.

POST /api/auth/login: Melakukan login untuk mendapatkan token.

Body (JSON): { "username": "namauser", "password": "passwordnya" }
