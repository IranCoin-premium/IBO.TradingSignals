package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_feedback")
data class FeedbackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val feedbackType: String, // "SIGNAL_INACCURACY", "IMPROVEMENT_SUGGESTION", "BROKER_ISSUE", "GENERAL"
    val asset: String? = null,
    val signalId: Long? = null,
    val reasonCategory: String? = null,
    val description: String,
    val rating: Int = 5,
    val contactInfo: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
