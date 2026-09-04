package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_subscriptions")
data class UserSubscriptionEntity(
    @PrimaryKey
    val id: String, // UUID or Firestore document ID
    val userId: String,
    val userEmail: String,
    val planTitle: String, // "یک هفته‌ای", "یک ماهه", "سه ماهه", "شش ماهه", "یک ساله سازمانی"
    val durationDays: Int,
    val priceToman: String,
    val priceUsdt: String,
    val status: String, // "ACTIVE", "EXPIRED", "PENDING"
    val startDate: Long = System.currentTimeMillis(),
    val expiryDate: Long,
    val paymentMethod: String = "CRYPTO_USDT", // "CRYPTO_USDT", "TOMAN_CARD", "PROMO_GRANT"
    val transactionRef: String = "",
    val isCachedLocally: Boolean = true,
    val lastSyncedTimestamp: Long = System.currentTimeMillis()
)
