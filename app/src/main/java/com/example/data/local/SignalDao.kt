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

    @Query("SELECT * FROM signals WHERE status IN ('WON', 'LOST', 'NO_TRADE') ORDER BY timestamp DESC")
    fun getHistoricalSignals(): Flow<List<SignalEntity>>

    @Query("SELECT * FROM signals WHERE status IN ('WON', 'LOST', 'NO_TRADE') AND (asset LIKE '%' || :query || '%' OR rationale LIKE '%' || :query || '%') ORDER BY timestamp DESC")
    fun searchHistoricalSignals(query: String): Flow<List<SignalEntity>>

    @Query("SELECT * FROM signals WHERE status = 'ACTIVE' ORDER BY timestamp DESC")
    fun getActiveSignals(): Flow<List<SignalEntity>>

    @Query("SELECT COUNT(*) FROM signals WHERE status = 'WON'")
    fun getWonCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM signals WHERE status = 'LOST'")
    fun getLostCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM signals WHERE status = 'NO_TRADE'")
    fun getVetoCountFlow(): Flow<Int>

    @Query("DELETE FROM signals WHERE status IN ('WON', 'LOST', 'NO_TRADE')")
    suspend fun clearHistory()

    @Query("SELECT * FROM signals WHERE id = :id LIMIT 1")
    suspend fun getSignalById(id: Long): SignalEntity?

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
