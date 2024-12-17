package com.example.tr2_process.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import retrofit2.http.DELETE

@Database(entities = [HostConfigEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hostConfigDao(): HostConfigDao
}

@Entity(tableName = "host_config")
data class HostConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Auto-generated ID
    val name: String,
    val host: String,
    val port: String,
    val enable: Boolean = false
)

@Dao
interface HostConfigDao {
    @Insert
    suspend fun insertData(process: HostConfigEntity)

    @Query("DELETE FROM host_config WHERE id = :id")
    suspend fun deleteData(id: Int)

    @Query("SELECT * FROM host_config WHERE id = :id")
    suspend fun getById(id: Int): HostConfigEntity

    @Query("SELECT * FROM host_config WHERE enable = 1")
    suspend fun getEnabled(): HostConfigEntity

    @Query("SELECT * FROM host_config")
    suspend fun getAll(): List<HostConfigEntity>

    @Query("UPDATE host_config SET enable = 0")
    suspend fun disableAll()

    @Query("UPDATE host_config SET enable = 1 WHERE id = :id")
    suspend fun enableById(id: Int)

    @Query("DELETE FROM host_config")
    suspend fun deleteAll()
}