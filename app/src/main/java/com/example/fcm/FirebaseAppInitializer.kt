package com.example.fcm

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

/**
 * Ensures FirebaseApp is safely initialized only when a REAL configuration is available
 * (either from google-services.json resources, or from BuildConfig injected config).
 * There is intentionally NO fallback with placeholder credentials:
 * initializing Firebase with a fake/random key would silently break FCM while pretending to succeed.
 */
object FirebaseAppInitializer {
    private const val TAG = "FirebaseAppInitializer"

    @Volatile
    private var isInitialized = false

    @Synchronized
    fun ensureInitialized(context: Context): Boolean {
        if (isInitialized && FirebaseApp.getApps(context).isNotEmpty()) {
            return true
        }

        return try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                isInitialized = true
                return true
            }

            val appContext = context.applicationContext ?: context

            // 1. Standard default initialization from google-services.json (the ONLY supported path)
            val app = FirebaseApp.initializeApp(appContext)
            if (app != null) {
                isInitialized = true
                Log.d(TAG, "Default FirebaseApp initialized successfully.")
                return true
            }
            false
        } catch (e: Exception) {
            Log.w(TAG, "Firebase not configured (no google-services.json): FCM disabled. ${e.message}")
            // Fail-closed: do NOT fabricate an initialization with placeholder credentials.
            isInitialized = false
            false
        }
    }
}
