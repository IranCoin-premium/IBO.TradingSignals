package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SignalDao {
    @Query("SELECT * FROM signals ORDER BY timestamp DESC")
    fun getAllSignals(): Flow<List<SignalEntity>>

    @Query("SELECT * FROM signals WHERE status = :status ORDER BY timestamp DESC")
    fun getSignalsByStatus(status: String): Flow<List<SignalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignal(signal: SignalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(signals: List<SignalEntity>)

    @Update
    suspend fun updateSignal(signal: SignalEntity)

    @Query("DELETE FROM signals WHERE id = :id")
    suspend fun deleteSignalById(id: Long)

    @Query("SELECT COUNT(*) FROM signals")
    suspend fun getCount(): Int
}
