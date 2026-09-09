package com.example.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.SignalEntity
import com.google.firebase.messaging.FirebaseMessaging

object FcmNotificationHelper {
    private const val TAG = "FcmNotificationHelper"

    const val CHANNEL_ID_SIGNALS = "signals_channel"
    const val CHANNEL_NAME_SIGNALS = "سیگنال‌های معاملاتی هوشمند"
    const val CHANNEL_DESC_SIGNALS = "دریافت آنی سیگنال‌های معاملاتی باینری آپشن با دقت بالا (High-Accuracy Signals)"

    const val TOPIC_HIGH_ACCURACY = "high_accuracy_signals"
    const val TOPIC_ALL_SIGNALS = "all_signals"

    const val EXTRA_SIGNAL_ID = "extra_signal_id"
    const val EXTRA_SIGNAL_ASSET = "extra_signal_asset"
    const val EXTRA_NAVIGATE_TO = "extra_navigate_to"

    /**
     * Initializes notification channels for Android 8.0 (API 26) and above.
     */
    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_SIGNALS,
                CHANNEL_NAME_SIGNALS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC_SIGNALS
                enableLights(true)
                lightColor = 0xFF10B981.toInt()
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel $CHANNEL_ID_SIGNALS created successfully.")
        }
    }

    /**
     * Subscribes the current device to FCM topics for signals.
     */
    fun subscribeToTopics(context: Context) {
        try {
            if (!FirebaseAppInitializer.ensureInitialized(context)) {
                Log.w(TAG, "FirebaseApp could not be initialized; skipping topic subscription.")
                return
            }

            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_HIGH_ACCURACY)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Subscribed successfully to FCM topic: $TOPIC_HIGH_ACCURACY")
                    } else {
                        Log.w(TAG, "Failed subscribing to topic $TOPIC_HIGH_ACCURACY: ${task.exception?.message}")
                    }
                }

            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ALL_SIGNALS)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Subscribed successfully to FCM topic: $TOPIC_ALL_SIGNALS")
                    } else {
                        Log.w(TAG, "Failed subscribing to topic $TOPIC_ALL_SIGNALS: ${task.exception?.message}")
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Notice in subscribeToTopics: ${e.message}")
        }
    }

    /**
     * Retrieves the current FCM Device Registration Token.
     */
    fun fetchFcmToken(context: Context? = null, onResult: (String?) -> Unit) {
        try {
            if (context != null && !FirebaseAppInitializer.ensureInitialized(context)) {
                onResult(null)
                return
            }

            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        Log.d(TAG, "FCM Token fetched: $token")
                        onResult(token)
                    } else {
                        Log.w(TAG, "Failed to get FCM token: ${task.exception?.message}")
                        onResult(null)
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Notice fetching FCM token: ${e.message}")
            onResult(null)
        }
    }

    fun fetchFcmToken(onResult: (String?) -> Unit) {
        fetchFcmToken(null, onResult)
    }

    /**
     * Checks if notification permission is granted on Android 13+ (API 33).
     */
    fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * Displays a rich, high-priority heads-up push notification for high-accuracy trade signals.
     */
    fun showSignalNotification(
        context: Context,
        signal: SignalEntity,
        customTitle: String? = null,
        customBody: String? = null
    ) {
        if (!isNotificationPermissionGranted(context)) {
            Log.w(TAG, "Notification permission not granted, skipping notification display.")
            return
        }

        initNotificationChannel(context)

        val directionFa = when (signal.direction.uppercase()) {
            "CALL" -> "خرید (CALL) 🟢"
            "PUT" -> "فروش (PUT) 🔴"
            "NO_TRADE" -> "هشدار عدم معامله 🛡️"
            else -> signal.direction
        }

        val primaryColor = when (signal.direction.uppercase()) {
            "CALL" -> 0xFF10B981.toInt() // Emerald Neon
            "PUT" -> 0xFFEF4444.toInt()  // Crimson Red
            else -> 0xFFF59E0B.toInt()   // Amber Gold
        }

        val title = customTitle ?: "🚨 سیگنال طلایی ${signal.asset} | $directionFa"
        val bodySummary = customBody ?: "دقت و وین‌ریت: ${signal.confidenceScore}٪ | زمان انقضا: ${signal.expiry}"

        val bigTextContent = buildString {
            append("🎯 جهت: $directionFa\n")
            append("📊 وین‌ریت پیش‌بینی: ${signal.confidenceScore}٪ (دقت بالا)\n")
            append("⏱️ تایم‌فریم انقضا: ${signal.expiry} | نقطه ورود: ${signal.strikePrice}\n")
            append("🏢 بروکرهای پیشنهادی: ${signal.recommendedBrokers}\n")
            if (signal.rationale.isNotBlank()) {
                append("💡 منطق تکنیکال: ${signal.rationale}")
            }
        }

        // Tap action intent
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_SIGNAL_ID, signal.id)
            putExtra(EXTRA_SIGNAL_ASSET, signal.asset)
            putExtra(EXTRA_NAVIGATE_TO, "signals")
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            signal.id.toInt().let { if (it == 0) System.currentTimeMillis().toInt() else it },
            intent,
            pendingIntentFlags
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID_SIGNALS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(bodySummary)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(bigTextContent)
                    .setSummaryText("سیگنال معاملاتی با دقت بالا")
            )
            .setColor(primaryColor)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        val notificationId = if (signal.id != 0L) signal.id.toInt() else System.currentTimeMillis().toInt()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, notificationBuilder.build())
            Log.d(TAG, "Notification delivered successfully for signal ID: $notificationId")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException while notifying: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to display push notification: ${e.message}")
        }
    }
}
