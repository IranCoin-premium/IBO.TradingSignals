package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSubscriptionDao {
    @Query("SELECT * FROM user_subscriptions ORDER BY startDate DESC")
    fun getAllSubscriptions(): Flow<List<UserSubscriptionEntity>>

    @Query("SELECT * FROM user_subscriptions WHERE userEmail = :email ORDER BY startDate DESC")
    fun getSubscriptionsForUser(email: String): Flow<List<UserSubscriptionEntity>>

    @Query("SELECT * FROM user_subscriptions WHERE userEmail = :email AND status = 'ACTIVE' ORDER BY expiryDate DESC LIMIT 1")
    suspend fun getActiveSubscriptionForUser(email: String): UserSubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: UserSubscriptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subscriptions: List<UserSubscriptionEntity>)

    @Update
    suspend fun updateSubscription(subscription: UserSubscriptionEntity)

    @Query("DELETE FROM user_subscriptions WHERE id = :id")
    suspend fun deleteSubscriptionById(id: String)

    @Query("DELETE FROM user_subscriptions")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM user_subscriptions")
    suspend fun getCount(): Int
}
