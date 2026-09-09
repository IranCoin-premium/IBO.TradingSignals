package com.example.fcm

import android.content.Context
import android.util.Log
import com.example.data.local.SignalEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class IranBinaryMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed FCM registration token: $token")

        // Persist token in SharedPreferences
        val prefs = applicationContext.getSharedPreferences(PREFS_FCM, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()

        // Resubscribe to default signal topics
        FcmNotificationHelper.subscribeToTopics(applicationContext)

        // Store device token in Firestore collection "fcm_tokens"
        try {
            FirebaseAppInitializer.ensureInitialized(applicationContext)
            val firestore = FirebaseFirestore.getInstance()
            val tokenData = hashMapOf(
                "token" to token,
                "platform" to "ANDROID",
                "appName" to "Iran Binary Option",
                "updatedAt" to System.currentTimeMillis()
            )
            val docId = token.takeLast(24).replace("/", "_")
            firestore.collection("fcm_tokens").document(docId)
                .set(tokenData, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "FCM token synced to Firestore: $docId")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed syncing FCM token to Firestore: ${e.message}")
                }
        } catch (e: Exception) {
            Log.w(TAG, "Firestore not available for token upload: ${e.message}")
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message received from: ${remoteMessage.from}")

        // 1. Check data payload
        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            Log.d(TAG, "FCM Message data payload: $data")
            handleSignalDataMessage(data, remoteMessage.notification)
        } else {
            // 2. Notification payload only fallback
            remoteMessage.notification?.let { notif ->
                Log.d(TAG, "FCM Notification Title: ${notif.title}, Body: ${notif.body}")
                val fallbackSignal = SignalEntity(
                    id = System.currentTimeMillis(),
                    asset = notif.title ?: "سیگنال طلایی معاملاتی",
                    category = "OTC",
                    direction = "CALL",
                    strikePrice = "1.0000",
                    currentPrice = "1.0000",
                    expiry = "1m",
                    payoutRate = "۹۵٪",
                    marketRegime = "تحلیل هوشمند",
                    confidenceScore = 92,
                    riskScore = "کم ریسک",
                    vetoStatus = "تایید شده",
                    rationale = notif.body ?: "سیگنال جدید با دقت بالا صادر گردید.",
                    recommendedBrokers = "Pocket Option, Quotex",
                    status = "ACTIVE",
                    timestamp = System.currentTimeMillis()
                )
                FcmNotificationHelper.showSignalNotification(
                    context = applicationContext,
                    signal = fallbackSignal,
                    customTitle = notif.title,
                    customBody = notif.body
                )
            }
        }
    }

    private fun handleSignalDataMessage(data: Map<String, String>, notification: RemoteMessage.Notification?) {
        val asset = data["asset"] ?: "EUR/USD (OTC)"
        val direction = data["direction"] ?: "CALL"
        val category = data["category"] ?: "OTC"
        val strikePrice = data["strikePrice"] ?: "1.08500"
        val currentPrice = data["currentPrice"] ?: strikePrice
        val expiry = data["expiry"] ?: "1m"
        val payoutRate = data["payoutRate"] ?: "۹۵٪"
        val marketRegime = data["marketRegime"] ?: "شکست تثبیت‌شده"
        val confidenceScore = data["confidenceScore"]?.toIntOrNull() ?: 90
        val riskScore = data["riskScore"] ?: "کم ریسک"
        val vetoStatus = data["vetoStatus"] ?: "تایید شده"
        val rationale = data["rationale"] ?: notification?.body ?: "فرصت معاملاتی با دقت بالا تایید شده توسط AI"
        val brokers = data["recommendedBrokers"] ?: "Quotex, Pocket Option"
        val signalId = data["id"]?.toLongOrNull() ?: System.currentTimeMillis()

        val signal = SignalEntity(
            id = signalId,
            asset = asset,
            category = category,
            direction = direction,
            strikePrice = strikePrice,
            currentPrice = currentPrice,
            expiry = expiry,
            payoutRate = payoutRate,
            marketRegime = marketRegime,
            confidenceScore = confidenceScore,
            riskScore = riskScore,
            vetoStatus = vetoStatus,
            rationale = rationale,
            recommendedBrokers = brokers,
            status = "ACTIVE",
            timestamp = System.currentTimeMillis()
        )

        // Only show heads-up notification if it's high accuracy or verified
        if (confidenceScore >= 75) {
            FcmNotificationHelper.showSignalNotification(
                context = applicationContext,
                signal = signal,
                customTitle = notification?.title,
                customBody = notification?.body
            )
        }
    }

    companion object {
        private const val TAG = "IranBinaryFCM"
        const val PREFS_FCM = "iran_binary_fcm_prefs"
        const val KEY_FCM_TOKEN = "key_fcm_token"
    }
}
