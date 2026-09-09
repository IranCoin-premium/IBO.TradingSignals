package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.SignalEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class SignalRepository(
    private val db: AppDatabase
) {
    fun getSignals(): Flow<List<SignalEntity>> = db.signalDao().getAllSignals()
    fun getHistoricalSignals(): Flow<List<SignalEntity>> = db.signalDao().getHistoricalSignals()
    fun getActiveSignals(): Flow<List<SignalEntity>> = db.signalDao().getActiveSignals()
    fun getFavoriteSignals(): Flow<List<SignalEntity>> = db.signalDao().getFavoriteSignals()

    fun getWonCount(): Flow<Int> = db.signalDao().getWonCountFlow()
    fun getLostCount(): Flow<Int> = db.signalDao().getLostCountFlow()
    fun getVetoCount(): Flow<Int> = db.signalDao().getVetoCountFlow()

    suspend fun addSignal(signal: SignalEntity) = db.signalDao().insertSignal(signal)
    suspend fun updateSignal(signal: SignalEntity) = db.signalDao().updateSignal(signal)

    suspend fun deleteSignal(id: Long) = db.signalDao().deleteSignalById(id)

    suspend fun clearHistory() = db.signalDao().clearHistory()

    suspend fun toggleFavorite(signal: SignalEntity) {
        val updated = signal.copy(isFavorite = !signal.isFavorite)
        db.signalDao().updateSignal(updated)
    }

    suspend fun seedSignals(signals: List<SignalEntity>) {
        if (db.signalDao().getCount() == 0) {
            db.signalDao().insertAll(signals)
        }
    }
}
