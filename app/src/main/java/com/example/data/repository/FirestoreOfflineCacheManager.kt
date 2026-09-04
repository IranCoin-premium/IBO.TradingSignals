package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.NewsEntity
import com.example.data.local.SignalEntity
import com.example.data.local.UserSubscriptionEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class OfflineCacheSyncStatus(
    val isOnline: Boolean = true,
    val isSyncing: Boolean = false,
    val lastSyncTime: Long = System.currentTimeMillis(),
    val cachedSignalsCount: Int = 0,
    val cachedNewsCount: Int = 0,
    val cachedSubscriptionsCount: Int = 0,
    val syncMessage: String = "لایه کش آفلاین Room فعال است (ذخیره اشتراک‌ها، سیگنال‌ها و اخبار)"
)

/**
 * Room-based offline caching and Firestore synchronization layer.
 *
 * Implements an Offline-First Single Source of Truth (SSOT) pattern:
 * 1. UI observes Room Local Database flows (instant load, 100% offline availability).
 * 2. Firestore provides real-time cloud sync and persistent local disk caching.
 * 3. User Subscriptions, Historical Signals, and Fundamental News are cached atomically.
 */
class FirestoreOfflineCacheManager(
    private val db: AppDatabase,
    private val context: Context
) {
    private val TAG = "FirestoreOfflineCache"

    private val firestore: FirebaseFirestore by lazy {
        val instance = FirebaseFirestore.getInstance()
        try {
            // Configure modern persistent offline cache in Firestore SDK
            val settings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build()
            instance.firestoreSettings = settings
            Log.d(TAG, "Firestore Persistent Cache configured successfully.")
        } catch (e: Exception) {
            Log.w(TAG, "Default cache settings retained: ${e.message}")
        }
        instance
    }

    private val _syncStatus = MutableStateFlow(OfflineCacheSyncStatus())
    val syncStatus: StateFlow<OfflineCacheSyncStatus> = _syncStatus.asStateFlow()

    private var signalsListener: ListenerRegistration? = null
    private var newsListener: ListenerRegistration? = null
    private var subscriptionsListener: ListenerRegistration? = null

    /**
     * Start background real-time synchronization with Firestore and populate Room cache.
     */
    fun startRealtimeSync(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            refreshCacheStats()
            setupCloudListeners(scope)
            syncAllFromCloud()
        }
    }

    private fun setupCloudListeners(scope: CoroutineScope) {
        try {
            // 1. Listen for Real-time Signals & Historical Updates
            signalsListener = firestore.collection("signals")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "Signals cloud listener failed (offline mode): ${e.message}")
                        _syncStatus.value = _syncStatus.value.copy(
                            isOnline = false,
                            syncMessage = "اتصال با سرور ابری قطع است؛ در حال خواندن از حافظه آفلاین Room"
                        )
                        return@addSnapshotListener
                    }

                    if (snapshots != null && !snapshots.isEmpty) {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val signalsList = snapshots.documents.mapNotNull { doc ->
                                    val data = doc.data ?: return@mapNotNull null
                                    SignalEntity(
                                        id = (data["id"] as? Long) ?: (doc.id.hashCode().toLong().let { if (it < 0) -it else it }),
                                        asset = data["asset"] as? String ?: "EUR/USD",
                                        category = data["category"] as? String ?: "OTC",
                                        direction = data["direction"] as? String ?: "CALL",
                                        strikePrice = data["strikePrice"] as? String ?: "1.0850",
                                        currentPrice = data["currentPrice"] as? String ?: "1.0850",
                                        expiry = data["expiry"] as? String ?: "1m",
                                        payoutRate = data["payoutRate"] as? String ?: "92%",
                                        marketRegime = data["marketRegime"] as? String ?: "Normal",
                                        confidenceScore = (data["confidenceScore"] as? Long)?.toInt() ?: 85,
                                        riskScore = data["riskScore"] as? String ?: "کم ریسک",
                                        vetoStatus = data["vetoStatus"] as? String ?: "تایید شده",
                                        rationale = data["rationale"] as? String ?: "",
                                        recommendedBrokers = data["recommendedBrokers"] as? String ?: "Pocket Option",
                                        status = data["status"] as? String ?: "ACTIVE",
                                        timestamp = (data["timestamp"] as? Long) ?: System.currentTimeMillis()
                                    )
                                }
                                db.signalDao().insertAll(signalsList)
                                refreshCacheStats()
                            } catch (ex: Exception) {
                                Log.e(TAG, "Error caching signals snapshot into Room: ${ex.message}")
                            }
                        }
                    }
                }

            // 2. Listen for Fundamental News updates
            newsListener = firestore.collection("news")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "News cloud listener failed (offline mode): ${e.message}")
                        return@addSnapshotListener
                    }

                    if (snapshots != null && !snapshots.isEmpty) {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val newsList = snapshots.documents.mapNotNull { doc ->
                                    val data = doc.data ?: return@mapNotNull null
                                    NewsEntity(
                                        id = (data["id"] as? Long) ?: (doc.id.hashCode().toLong().let { if (it < 0) -it else it }),
                                        title = data["title"] as? String ?: "",
                                        category = data["category"] as? String ?: "MACRO",
                                        summary = data["summary"] as? String ?: "",
                                        fullContent = data["fullContent"] as? String ?: "",
                                        source = data["source"] as? String ?: "ایران باینری",
                                        impact = data["impact"] as? String ?: "MEDIUM",
                                        sentiment = data["sentiment"] as? String ?: "خنثی",
                                        timeAgo = data["timeAgo"] as? String ?: "لحظاتی پیش",
                                        timestamp = (data["timestamp"] as? Long) ?: System.currentTimeMillis()
                                    )
                                }
                                db.newsDao().insertAll(newsList)
                                refreshCacheStats()
                            } catch (ex: Exception) {
                                Log.e(TAG, "Error caching news snapshot into Room: ${ex.message}")
                            }
                        }
                    }
                }

            // 3. Listen for User Subscriptions
            subscriptionsListener = firestore.collection("subscriptions")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "Subscriptions cloud listener failed (offline mode): ${e.message}")
                        return@addSnapshotListener
                    }

                    if (snapshots != null && !snapshots.isEmpty) {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val subList = snapshots.documents.mapNotNull { doc ->
                                    val data = doc.data ?: return@mapNotNull null
                                    UserSubscriptionEntity(
                                        id = doc.id,
                                        userId = data["userId"] as? String ?: "",
                                        userEmail = data["userEmail"] as? String ?: "",
                                        planTitle = data["planTitle"] as? String ?: "اشتراک",
                                        durationDays = (data["durationDays"] as? Long)?.toInt() ?: 30,
                                        priceToman = data["priceToman"] as? String ?: "",
                                        priceUsdt = data["priceUsdt"] as? String ?: "",
                                        status = data["status"] as? String ?: "ACTIVE",
                                        startDate = (data["startDate"] as? Long) ?: System.currentTimeMillis(),
                                        expiryDate = (data["expiryDate"] as? Long) ?: (System.currentTimeMillis() + 30L * 86400000L),
                                        paymentMethod = data["paymentMethod"] as? String ?: "USDT",
                                        transactionRef = data["transactionRef"] as? String ?: "",
                                        isCachedLocally = true,
                                        lastSyncedTimestamp = System.currentTimeMillis()
                                    )
                                }
                                db.userSubscriptionDao().insertAll(subList)
                                refreshCacheStats()
                            } catch (ex: Exception) {
                                Log.e(TAG, "Error caching subscriptions snapshot into Room: ${ex.message}")
                            }
                        }
                    }
                }

        } catch (e: Exception) {
            Log.w(TAG, "Failed to initialize Firestore listeners: ${e.message}")
        }
    }

    /**
     * One-shot fetch from Firestore with atomic offline caching into Room.
     */
    suspend fun syncAllFromCloud() = withContext(Dispatchers.IO) {
        _syncStatus.value = _syncStatus.value.copy(
            isSyncing = true,
            syncMessage = "در حال دریافت و همگام‌سازی داده‌ها با کش محلی Room..."
        )

        try {
            // Fetch Signals from Firestore
            val signalsSnap = firestore.collection("signals").get().await()
            if (!signalsSnap.isEmpty) {
                val list = signalsSnap.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    SignalEntity(
                        id = (data["id"] as? Long) ?: (doc.id.hashCode().toLong().let { if (it < 0) -it else it }),
                        asset = data["asset"] as? String ?: "EUR/USD",
                        category = data["category"] as? String ?: "OTC",
                        direction = data["direction"] as? String ?: "CALL",
                        strikePrice = data["strikePrice"] as? String ?: "1.0850",
                        currentPrice = data["currentPrice"] as? String ?: "1.0850",
                        expiry = data["expiry"] as? String ?: "1m",
                        payoutRate = data["payoutRate"] as? String ?: "92%",
                        marketRegime = data["marketRegime"] as? String ?: "Normal",
                        confidenceScore = (data["confidenceScore"] as? Long)?.toInt() ?: 85,
                        riskScore = data["riskScore"] as? String ?: "کم ریسک",
                        vetoStatus = data["vetoStatus"] as? String ?: "تایید شده",
                        rationale = data["rationale"] as? String ?: "",
                        recommendedBrokers = data["recommendedBrokers"] as? String ?: "Pocket Option",
                        status = data["status"] as? String ?: "ACTIVE",
                        timestamp = (data["timestamp"] as? Long) ?: System.currentTimeMillis()
                    )
                }
                db.signalDao().insertAll(list)
            }

            // Fetch Fundamental News from Firestore
            val newsSnap = firestore.collection("news").get().await()
            if (!newsSnap.isEmpty) {
                val list = newsSnap.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    NewsEntity(
                        id = (data["id"] as? Long) ?: (doc.id.hashCode().toLong().let { if (it < 0) -it else it }),
                        title = data["title"] as? String ?: "",
                        category = data["category"] as? String ?: "MACRO",
                        summary = data["summary"] as? String ?: "",
                        fullContent = data["fullContent"] as? String ?: "",
                        source = data["source"] as? String ?: "ایران باینری",
                        impact = data["impact"] as? String ?: "MEDIUM",
                        sentiment = data["sentiment"] as? String ?: "خنثی",
                        timeAgo = data["timeAgo"] as? String ?: "لحظاتی پیش",
                        timestamp = (data["timestamp"] as? Long) ?: System.currentTimeMillis()
                    )
                }
                db.newsDao().insertAll(list)
            }

            // Fetch Subscriptions from Firestore
            val subSnap = firestore.collection("subscriptions").get().await()
            if (!subSnap.isEmpty) {
                val list = subSnap.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    UserSubscriptionEntity(
                        id = doc.id,
                        userId = data["userId"] as? String ?: "",
                        userEmail = data["userEmail"] as? String ?: "",
                        planTitle = data["planTitle"] as? String ?: "اشتراک",
                        durationDays = (data["durationDays"] as? Long)?.toInt() ?: 30,
                        priceToman = data["priceToman"] as? String ?: "",
                        priceUsdt = data["priceUsdt"] as? String ?: "",
                        status = data["status"] as? String ?: "ACTIVE",
                        startDate = (data["startDate"] as? Long) ?: System.currentTimeMillis(),
                        expiryDate = (data["expiryDate"] as? Long) ?: (System.currentTimeMillis() + 30L * 86400000L),
                        paymentMethod = data["paymentMethod"] as? String ?: "USDT",
                        transactionRef = data["transactionRef"] as? String ?: "",
                        isCachedLocally = true,
                        lastSyncedTimestamp = System.currentTimeMillis()
                    )
                }
                db.userSubscriptionDao().insertAll(list)
            }

            _syncStatus.value = _syncStatus.value.copy(
                isOnline = true,
                isSyncing = false,
                lastSyncTime = System.currentTimeMillis(),
                syncMessage = "همگام‌سازی ابری با موفقیت انجام شد و کش آفلاین Room بروز گردید."
            )
        } catch (e: Exception) {
            Log.w(TAG, "Cloud sync fallback to offline Room storage: ${e.message}")
            _syncStatus.value = _syncStatus.value.copy(
                isOnline = false,
                isSyncing = false,
                syncMessage = "استفاده از کش آفلاین Room (عدم دسترسی موقت به سرور ابری)"
            )
        } finally {
            refreshCacheStats()
        }
    }

    /**
     * Cache user subscription in Room first (offline availability) and push to Firestore.
     */
    suspend fun cacheAndUploadUserSubscription(subscription: UserSubscriptionEntity) = withContext(Dispatchers.IO) {
        // 1. Immediately store in Room (Offline First)
        db.userSubscriptionDao().insertSubscription(subscription.copy(isCachedLocally = true))
        refreshCacheStats()

        // 2. Upload to Firestore collection "subscriptions"
        try {
            val map = hashMapOf(
                "userId" to subscription.userId,
                "userEmail" to subscription.userEmail,
                "planTitle" to subscription.planTitle,
                "durationDays" to subscription.durationDays,
                "priceToman" to subscription.priceToman,
                "priceUsdt" to subscription.priceUsdt,
                "status" to subscription.status,
                "startDate" to subscription.startDate,
                "expiryDate" to subscription.expiryDate,
                "paymentMethod" to subscription.paymentMethod,
                "transactionRef" to subscription.transactionRef,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("subscriptions")
                .document(subscription.id)
                .set(map, SetOptions.merge())
                .await()
            Log.d(TAG, "Subscription successfully uploaded to Firestore: ${subscription.id}")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to upload subscription to Firestore (stored offline in Room): ${e.message}")
        }
    }

    /**
     * Cache Signal (active or historical) in Room first and push to Firestore.
     */
    suspend fun cacheAndUploadSignal(signal: SignalEntity): Long = withContext(Dispatchers.IO) {
        // 1. Store in Room (SSOT)
        val generatedId = db.signalDao().insertSignal(signal)
        val actualSignal = if (signal.id == 0L) signal.copy(id = generatedId) else signal
        refreshCacheStats()

        // 2. Push to Firestore collection "signals"
        try {
            val map = hashMapOf(
                "id" to actualSignal.id,
                "asset" to actualSignal.asset,
                "category" to actualSignal.category,
                "direction" to actualSignal.direction,
                "strikePrice" to actualSignal.strikePrice,
                "currentPrice" to actualSignal.currentPrice,
                "expiry" to actualSignal.expiry,
                "payoutRate" to actualSignal.payoutRate,
                "marketRegime" to actualSignal.marketRegime,
                "confidenceScore" to actualSignal.confidenceScore,
                "riskScore" to actualSignal.riskScore,
                "vetoStatus" to actualSignal.vetoStatus,
                "rationale" to actualSignal.rationale,
                "recommendedBrokers" to actualSignal.recommendedBrokers,
                "status" to actualSignal.status,
                "timestamp" to actualSignal.timestamp
            )
            firestore.collection("signals")
                .document("sig_${actualSignal.id}")
                .set(map, SetOptions.merge())
                .await()
            Log.d(TAG, "Signal successfully uploaded to Firestore: sig_${actualSignal.id}")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to upload signal to Firestore (retained in Room): ${e.message}")
        }
        generatedId
    }

    /**
     * Cache Fundamental News in Room first and push to Firestore.
     */
    suspend fun cacheAndUploadNews(news: NewsEntity): Long = withContext(Dispatchers.IO) {
        // 1. Store in Room
        val generatedId = db.newsDao().insertNews(news)
        val actualNews = if (news.id == 0L) news.copy(id = generatedId) else news
        refreshCacheStats()

        // 2. Push to Firestore collection "news"
        try {
            val map = hashMapOf(
                "id" to actualNews.id,
                "title" to actualNews.title,
                "category" to actualNews.category,
                "summary" to actualNews.summary,
                "fullContent" to actualNews.fullContent,
                "source" to actualNews.source,
                "impact" to actualNews.impact,
                "sentiment" to actualNews.sentiment,
                "timeAgo" to actualNews.timeAgo,
                "timestamp" to actualNews.timestamp
            )
            firestore.collection("news")
                .document("news_${actualNews.id}")
                .set(map, SetOptions.merge())
                .await()
            Log.d(TAG, "News successfully uploaded to Firestore: news_${actualNews.id}")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to upload news to Firestore (retained in Room): ${e.message}")
        }
        generatedId
    }

    suspend fun deleteCachedSignal(id: Long) = withContext(Dispatchers.IO) {
        db.signalDao().deleteSignalById(id)
        refreshCacheStats()
        try {
            firestore.collection("signals").document("sig_$id").delete().await()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to delete signal from Firestore: ${e.message}")
        }
    }

    suspend fun deleteCachedNews(id: Long) = withContext(Dispatchers.IO) {
        db.newsDao().deleteNewsById(id)
        refreshCacheStats()
        try {
            firestore.collection("news").document("news_$id").delete().await()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to delete news from Firestore: ${e.message}")
        }
    }

    suspend fun refreshCacheStats() = withContext(Dispatchers.IO) {
        try {
            val signalsCount = db.signalDao().getCount()
            val newsCount = db.newsDao().getCount()
            val subsCount = db.userSubscriptionDao().getCount()

            _syncStatus.value = _syncStatus.value.copy(
                cachedSignalsCount = signalsCount,
                cachedNewsCount = newsCount,
                cachedSubscriptionsCount = subsCount
            )
        } catch (e: Exception) {
            Log.w(TAG, "Error calculating cache stats: ${e.message}")
        }
    }

    fun cleanup() {
        signalsListener?.remove()
        newsListener?.remove()
        subscriptionsListener?.remove()
    }
}
