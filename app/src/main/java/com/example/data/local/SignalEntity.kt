package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signals")
data class SignalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val asset: String,
    val category: String, // "OTC", "FOREX", "CRYPTO", "COMMODITIES"
    val direction: String, // "CALL", "PUT", "NO_TRADE"
    val strikePrice: String,
    val currentPrice: String,
    val expiry: String, // "1m", "3m", "5m", "15m"
    val payoutRate: String, // "92%", "95%"
    val marketRegime: String, // "Trend Bullish", "Range Compression", "Breakout"
    val confidenceScore: Int, // 0-100
    val riskScore: String, // "کم ریسک (Low)", "متوسط (Medium)", "بالا (High)"
    val vetoStatus: String, // "تایید شده", "رد شده با Veto"
    val rationale: String,
    val recommendedBrokers: String, // comma separated
    val status: String, // "ACTIVE", "WON", "LOST", "NO_TRADE"
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
