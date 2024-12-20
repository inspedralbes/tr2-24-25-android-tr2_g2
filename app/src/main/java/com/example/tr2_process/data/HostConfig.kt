package com.example.tr2_process.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Database(entities = [HostConfigEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hostConfigDao(): HostConfigDao
}

@Entity(tableName = "host_config")
data class HostConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Auto-generated ID
    val name: String,
    val host: String,
    val port: String
)

@Dao
interface HostConfigDao {
    @Insert
    suspend fun insertData(process: HostConfigEntity)

    @Query("SELECT * FROM host_config WHERE id = :id")
    suspend fun getById(id: Int): HostConfigEntity

    @Query("SELECT * FROM host_config")
    suspend fun getAll(): List<HostConfigEntity>

    @Query("DELETE FROM host_config")
    suspend fun deleteAll()
}