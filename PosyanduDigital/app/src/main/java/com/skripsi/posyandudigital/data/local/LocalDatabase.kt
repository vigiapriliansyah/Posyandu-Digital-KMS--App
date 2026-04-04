package com.skripsi.posyandudigital.data.local

import android.content.Context
import androidx.room.*

// --- 1. ENTITAS KMS ---
@Entity(tableName = "kms_records")
data class KmsEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val apiId: Int? = null,
    val anakId: Int,
    val tanggalPencatatan: String,
    val umurBulan: Int,
    val beratBadan: Double,
    val tinggiBadan: Double?,
    val kbm: Int?,
    val statusNaikTurun: String?,
    val asiEksklusif: Boolean?,
    val statusGiziRaw: String?,
    val catatan: String?,
    val isSynced: Boolean = false
)

// --- 2. ENTITAS DAFTAR ANAK ---
@Entity(tableName = "anak_records")
data class AnakEntity(
    @PrimaryKey val id: Int,
    val namaAnak: String,
    val jenisKelamin: String,
    val tanggalLahir: String?,
    val umurBulan: Int,
    val namaIbu: String,
    // --- PERBAIKAN: Menambahkan parameter yang diminta oleh AnakRepository ---
    val statusGiziTerakhir: String? = null
)

// --- 3. ENTITAS CACHE DASHBOARD ---
@Entity(tableName = "dashboard_cache")
data class DashboardCacheEntity(
    @PrimaryKey val role: String, // cth: "kader", "admin"
    val jsonData: String // Menyimpan respon API dalam bentuk teks JSON
)

// --- DAOs ---
@Dao
interface KmsDao {
    @Query("SELECT * FROM kms_records WHERE anakId = :anakId ORDER BY tanggalPencatatan DESC, localId DESC")
    suspend fun getRiwayatByAnak(anakId: Int): List<KmsEntity>
    @Query("SELECT * FROM kms_records WHERE isSynced = 0")
    suspend fun getUnsyncedRecords(): List<KmsEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: KmsEntity): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<KmsEntity>)
    @Query("DELETE FROM kms_records WHERE anakId = :anakId AND isSynced = 1")
    suspend fun deleteSyncedRecordsByAnak(anakId: Int)
    @Query("UPDATE kms_records SET isSynced = 1, apiId = :apiId, statusGiziRaw = :statusGizi WHERE localId = :localId")
    suspend fun markAsSynced(localId: Int, apiId: Int, statusGizi: String?)
}

@Dao
interface AnakDao {
    @Query("SELECT * FROM anak_records ORDER BY namaAnak ASC")
    suspend fun getAllAnak(): List<AnakEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(anak: List<AnakEntity>)
    @Query("DELETE FROM anak_records")
    suspend fun deleteAll()
}

@Dao
interface DashboardCacheDao {
    @Query("SELECT jsonData FROM dashboard_cache WHERE role = :role")
    suspend fun getDashboardData(role: String): String?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDashboardData(cache: DashboardCacheEntity)
}

// --- INISIALISASI DATABASE ---
@Database(
    entities = [KmsEntity::class, AnakEntity::class, DashboardCacheEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun kmsDao(): KmsDao
    abstract fun anakDao(): AnakDao
    abstract fun dashboardCacheDao(): DashboardCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "posyandu_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}