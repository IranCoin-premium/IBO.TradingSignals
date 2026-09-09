package com.example

import android.app.Application
import android.util.Log
import com.example.fcm.FcmNotificationHelper
import com.example.fcm.FirebaseAppInitializer
import com.example.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TradingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@TradingApplication)
            modules(appModule)
        }

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
