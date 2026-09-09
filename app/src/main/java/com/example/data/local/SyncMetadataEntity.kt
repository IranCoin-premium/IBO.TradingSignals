package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity to track the synchronization state of different data collections.
 * Part of the Room Optimization & Offline-First State Sync (Directive 22, Part 23).
 */
@Entity(tableName = "sync_metadata")
data class SyncMetadataEntity(
    @PrimaryKey val collectionName: String, // e.g., "signals", "news", "subscriptions"
    val lastSyncTimestamp: Long,
    val totalItemsSynced: Int,
    val syncStatus: String = "SUCCESS" // SUCCESS, FAILED, IN_PROGRESS
)
