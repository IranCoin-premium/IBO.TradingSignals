package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SignalEntity::class,
        NewsEntity::class,
        PlanEntity::class,
        UserEntity::class,
        UserSubscriptionEntity::class,
        FeedbackEntity::class,
        TradeLogEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun signalDao(): SignalDao
    abstract fun newsDao(): NewsDao
    abstract fun planDao(): PlanDao
    abstract fun userDao(): UserDao
    abstract fun userSubscriptionDao(): UserSubscriptionDao
    abstract fun feedbackDao(): FeedbackDao
    abstract fun tradeLogDao(): TradeLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "iran_binary_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
