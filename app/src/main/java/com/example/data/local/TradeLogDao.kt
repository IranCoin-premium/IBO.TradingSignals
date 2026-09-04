package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeLogDao {
    @Query("SELECT * FROM trade_logs ORDER BY timestamp DESC")
    fun getAllTradeLogs(): Flow<List<TradeLogEntity>>

    @Query("SELECT * FROM trade_logs WHERE result = :result ORDER BY timestamp DESC")
    fun getTradeLogsByResult(result: String): Flow<List<TradeLogEntity>>

    @Query("SELECT * FROM trade_logs WHERE id = :id LIMIT 1")
    suspend fun getTradeLogById(id: Long): TradeLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTradeLog(trade: TradeLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trades: List<TradeLogEntity>)

    @Update
    suspend fun updateTradeLog(trade: TradeLogEntity)

    @Query("DELETE FROM trade_logs WHERE id = :id")
    suspend fun deleteTradeLogById(id: Long)

    @Query("DELETE FROM trade_logs")
    suspend fun clearAllTradeLogs()

    @Query("SELECT COUNT(*) FROM trade_logs")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM trade_logs WHERE result = 'WIN'")
    fun getWinCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM trade_logs WHERE result = 'LOSS'")
    fun getLossCount(): Flow<Int>
}
