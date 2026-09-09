package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Query("SELECT * FROM subscription_plans ORDER BY id ASC")
    fun getAllPlans(): Flow<List<PlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plans: List<PlanEntity>)

    @Update
    suspend fun updatePlan(plan: PlanEntity)

    @Query("SELECT COUNT(*) FROM subscription_plans")
    suspend fun getCount(): Int
}
