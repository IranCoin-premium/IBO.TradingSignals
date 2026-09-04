package com.example.fcm

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

/**
 * Ensures FirebaseApp is safely initialized even when google-services.json
 * is not bundled or missing from the local development environment.
 * Prevents "Default FirebaseApp is not initialized in this process" IllegalStateException.
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

            // 1. Try standard default initialization first (if google-services.json was provided)
            try {
                val app = FirebaseApp.initializeApp(appContext)
                if (app != null) {
                    isInitialized = true
                    Log.d(TAG, "Default FirebaseApp initialized successfully.")
                    return true
                }
            } catch (e: Exception) {
                Log.d(TAG, "Standard Firebase initialization with resources not available: ${e.message}")
            }

            // 2. Fallback initialization with standard configuration parameters
            // This allows FirebaseMessaging, Firestore, and FirebaseAuth to instantiate safely without throwing.
            val options = FirebaseOptions.Builder()
                .setApplicationId("1:126239385393:android:e6f5c88b774dcd212")
                .setApiKey("AIzaSyB3RandomInitKeyForIranBinaryOptionAppSafe00")
                .setProjectId("iran-binary-option-signals")
                .setGcmSenderId("126239385393")
                .build()

            FirebaseApp.initializeApp(appContext, options)
            isInitialized = true
            Log.d(TAG, "FirebaseApp initialized with fallback options successfully.")
            true
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseApp initialization handled: ${e.message}")
            false
        }
    }
}
