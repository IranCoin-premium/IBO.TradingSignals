package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.FeedbackEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class FeedbackRepository(
    private val db: AppDatabase
) {
    fun getFeedbacks(): Flow<List<FeedbackEntity>> = db.feedbackDao().getAllFeedback()

    fun getFeedbackCount(): Flow<Int> = db.feedbackDao().getFeedbackCount()

    suspend fun addFeedback(feedback: FeedbackEntity) = db.feedbackDao().insertFeedback(feedback)

    suspend fun deleteFeedback(id: Long) = db.feedbackDao().deleteFeedback(id)
}
