package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackEntity): Long

    @Query("SELECT * FROM user_feedback ORDER BY timestamp DESC")
    fun getAllFeedback(): Flow<List<FeedbackEntity>>

    @Query("SELECT COUNT(*) FROM user_feedback")
    fun getFeedbackCount(): Flow<Int>

    @Query("DELETE FROM user_feedback WHERE id = :id")
    suspend fun deleteFeedback(id: Long)
}
