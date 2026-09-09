package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscription_plans")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val durationText: String,
    val durationDays: Int,
    val priceToman: String,
    val priceUsdt: String,
    val discountPercent: Int,
    val isPopular: Boolean,
    val features: String, // Comma separated list of features
    val badge: String
)
