package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news ORDER BY timestamp DESC")
    fun getAllNews(): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(item: NewsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<NewsEntity>)

    @Update
    suspend fun updateNews(item: NewsEntity)

    @Query("DELETE FROM news WHERE id = :id")
    suspend fun deleteNewsById(id: Long)

    @Query("SELECT COUNT(*) FROM news")
    suspend fun getCount(): Int
}
