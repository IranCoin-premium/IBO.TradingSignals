package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val role: String, // "ADMIN", "STAFF", "USER"
    val activePlan: String, // "رایگان", "یک هفته‌ای", "یک ماهه", "سه ماهه", "شش ماهه", "یک ساله"
    val planExpiryTimestamp: Long = 0L,
    val loginProvider: String = "MANUAL", // "MANUAL", "GOOGLE", "GITHUB"
    val createdAt: Long = System.currentTimeMillis()
)
