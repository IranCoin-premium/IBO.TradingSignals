package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val category: String, // "OTC", "FOREX", "CRYPTO", "MACRO"
    val summary: String,
    val fullContent: String,
    val source: String,
    val impact: String, // "HIGH", "MEDIUM", "LOW"
    val sentiment: String, // "صعودی (Bullish)", "نزولی (Bearish)", "خنثی (Neutral)"
    val timeAgo: String,
    val timestamp: Long = System.currentTimeMillis()
)
