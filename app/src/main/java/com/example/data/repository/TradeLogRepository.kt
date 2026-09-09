package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.TradeLogEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class TradeLogRepository(
    private val db: AppDatabase
) {
    fun getTradeLogs(): Flow<List<TradeLogEntity>> = db.tradeLogDao().getAllTradeLogs()

    suspend fun addTradeLog(log: TradeLogEntity) = db.tradeLogDao().insertTradeLog(log)

    suspend fun updateTradeLog(log: TradeLogEntity) = db.tradeLogDao().updateTradeLog(log)

    suspend fun deleteTradeLog(id: Long) = db.tradeLogDao().deleteTradeLogById(id)

    suspend fun clearAll() = db.tradeLogDao().clearAllTradeLogs()

    suspend fun seedLogs(logs: List<TradeLogEntity>) {
        if (db.tradeLogDao().getCount() == 0) {
            db.tradeLogDao().insertAll(logs)
        }
    }
}
