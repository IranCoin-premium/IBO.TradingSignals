package com.example

import android.app.Application
import android.util.Log
import com.example.fcm.FcmNotificationHelper
import com.example.fcm.FirebaseAppInitializer

class TradingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            // Safely initialize FirebaseApp before any activity or background service requests it
            FirebaseAppInitializer.ensureInitialized(this)
            // Create notification channel for Android 8.0+
            FcmNotificationHelper.initNotificationChannel(this)
        } catch (e: Exception) {
            Log.w("TradingApplication", "Startup initialization warning: ${e.message}")
        }
    }
}
