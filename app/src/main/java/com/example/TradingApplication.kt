package com.example

import android.app.Application
import android.util.Log
import com.example.fcm.FcmNotificationHelper
import com.example.fcm.FirebaseAppInitializer
import com.example.di.appModule
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class TradingApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Idempotent Koin bootstrap: in Robolectric/Roborazzi unit tests every test
        // creates a fresh Application instance, but Koin's GlobalContext is a JVM-wide
        // singleton — calling startKoin unconditionally throws
        // KoinApplicationAlreadyStartedException. Guard keeps prod behaviour identical
        // (onCreate runs once) while making repeated test startups safe.
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidLogger()
                androidContext(this@TradingApplication)
                modules(appModule)
            }
        }

        try {
            // Safely initialize FirebaseApp before any activity or background service requests it
            FirebaseAppInitializer.ensureInitialized(this)
            // Create notification channel for Android 8.0+
            FcmNotificationHelper.initNotificationChannel(this)
            // Crashlytics: enable collection (no-op until google-services.json is present;
            // collection auto-disables in debug builds per default Firebase behavior)
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        } catch (e: Exception) {
            Log.w("TradingApplication", "Startup initialization warning: ${e.message}")
        }
    }
}
