package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.NewsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class NewsRepository(
    private val db: AppDatabase
) {
    fun getNews(): Flow<List<NewsEntity>> = db.newsDao().getAllNews()

    suspend fun addNews(news: List<NewsEntity>) = db.newsDao().insertAll(news)

    suspend fun seedNews(news: List<NewsEntity>) {
        if (db.newsDao().getCount() == 0) {
            db.newsDao().insertAll(news)
        }
    }

    suspend fun refreshLatestFinancialNews(): Int {
        val newNews = listOf(
            NewsEntity(
                title = "نوسانات شدید در جفت ارز EUR/USD پس از اعلام نرخ بهره",
                summary = "بازار جفت ارز یورو به دلار پس از بیانیه اخیر فدرال رزرو با نوسانات شدیدی همراه بود...",
                impact = "HIGH",
                category = "FOREX",
                fullContent = "متن کامل خبر در این بخش قرار می‌گیرد.",
                source = "ایران باینری",
                sentiment = "خنثی",
                timeAgo = "لحظاتی پیش",
                timestamp = System.currentTimeMillis()
            )
        )
        db.newsDao().insertAll(newNews)
        return newNews.size
    }

    suspend fun deleteNews(id: Long) = db.newsDao().deleteNewsById(id)
}
