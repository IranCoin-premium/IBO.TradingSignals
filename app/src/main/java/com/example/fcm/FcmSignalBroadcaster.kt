package com.example.fcm

import android.content.Context
import android.util.Log
import com.example.data.local.SignalEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FcmSignalBroadcaster {
    private const val TAG = "FcmSignalBroadcaster"

    /**
     * Broadcasts a push notification when a new high-accuracy trade signal is posted.
     * 1. Records broadcast in Firestore for cloud distribution to all registered FCM clients.
     * 2. Displays immediate heads-up status bar notification on local device.
     */
    suspend fun broadcastHighAccuracySignal(
        context: Context,
        signal: SignalEntity,
        onResult: (Boolean, String) -> Unit = { _, _ -> }
    ) = withContext(Dispatchers.IO) {
        try {
            // 1. Immediately trigger local heads-up push notification if high-accuracy
            if (signal.confidenceScore >= 75 && signal.direction != "NO_TRADE") {
                withContext(Dispatchers.Main) {
                    FcmNotificationHelper.showSignalNotification(context.applicationContext, signal)
                }
            }

            // 2. Publish to Firestore cloud collection "fcm_push_broadcasts"
            FirebaseAppInitializer.ensureInitialized(context)
            val firestore = FirebaseFirestore.getInstance()
            val broadcastDocId = "fcm_sig_${signal.id}_${System.currentTimeMillis()}"

            val directionFa = when (signal.direction.uppercase()) {
                "CALL" -> "خرید (CALL) 🟢"
                "PUT" -> "فروش (PUT) 🔴"
                else -> signal.direction
            }

            val payload = hashMapOf(
                "broadcastId" to broadcastDocId,
                "topic" to FcmNotificationHelper.TOPIC_HIGH_ACCURACY,
                "signalId" to signal.id,
                "asset" to signal.asset,
                "category" to signal.category,
                "direction" to signal.direction,
                "directionFa" to directionFa,
                "strikePrice" to signal.strikePrice,
                "currentPrice" to signal.currentPrice,
                "expiry" to signal.expiry,
                "payoutRate" to signal.payoutRate,
                "confidenceScore" to signal.confidenceScore,
                "riskScore" to signal.riskScore,
                "recommendedBrokers" to signal.recommendedBrokers,
                "rationale" to signal.rationale,
                "title" to "🚨 سیگنال طلایی ${signal.asset} | $directionFa",
                "body" to "وین‌ریت: ${signal.confidenceScore}٪ | استرایک: ${signal.strikePrice} | انقضا: ${signal.expiry}",
                "status" to "SENT",
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("fcm_push_broadcasts")
                .document(broadcastDocId)
                .set(payload, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "FCM push broadcast successfully queued in Firestore: $broadcastDocId")
                    onResult(true, "پوش نوتیفیکیشن با موفقیت به مشترکین تاپیک high_accuracy_signals ارسال گردید.")
                }
                .addOnFailureListener { ex ->
                    Log.w(TAG, "Failed queueing FCM broadcast to Firestore: ${ex.message}")
                    onResult(true, "نوتیفیکیشن محلی ارسال شد؛ ثبت ابری در صف انتظار است.")
                }

        } catch (e: Exception) {
            Log.e(TAG, "Error in broadcastHighAccuracySignal: ${e.message}")
            onResult(false, "خطا در ارسال نوتیفیکیشن: ${e.message}")
        }
    }
}
