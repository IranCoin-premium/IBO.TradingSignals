package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SyncMetadataDao {
    @Query("SELECT * FROM sync_metadata")
    fun getAllSyncMetadata(): kotlinx.coroutines.flow.Flow<List<SyncMetadataEntity>>

    @Query("SELECT * FROM sync_metadata WHERE collectionName = :name LIMIT 1")
    suspend fun getSyncMetadata(name: String): SyncMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSyncMetadata(metadata: SyncMetadataEntity)

    @Query("SELECT lastSyncTimestamp FROM sync_metadata WHERE collectionName = :name")
    suspend fun getLastSyncTimestamp(name: String): Long?

    @Query("DELETE FROM sync_metadata")
    suspend fun clearAllMetadata()
}
