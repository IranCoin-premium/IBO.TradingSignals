package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trade_logs")
data class TradeLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val asset: String,                 // e.g. "EUR/USD (OTC)", "GOLD", "BTC/USDT"
    val direction: String,             // "CALL", "PUT"
    val result: String,                // "WIN", "LOSS", "TIE"
    val tradeAmount: Double,           // e.g. 20.0
    val payoutPercent: Int,            // e.g. 88
    val profitOrLoss: Double,          // e.g. +17.6 or -20.0
    val broker: String,                // "Pocket Option", "Quotex", "Deriv", "Nadex", "Olymp Trade", "دیگر"
    val entryPrice: String = "",       // e.g. "1.08450"
    val exitPrice: String = "",        // e.g. "1.08480"
    val expiry: String = "1m",         // "1m", "2m", "3m", "5m", "15m"
    val strategy: String = "پرایس اکشن", // "شکست و پولبک", "حمایت و مقاومت", "واگرایی RSI", "سیگنال هوش مصنوعی", "پین‌بار"
    val notes: String = "",            // Free-form user analysis notes
    val photoUri: String? = null,      // URI string from Photo Picker
    val emotionalState: String = "منضبط و آرام", // "منضبط و آرام", "طمع / هیجان‌زده", "انتقام‌جویانه (Revenge)", "تردید و اضطراب"
    val timestamp: Long = System.currentTimeMillis()
)
