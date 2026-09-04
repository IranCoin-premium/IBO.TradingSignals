package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_settings_prefs")

data class NotificationSettings(
    val masterEnabled: Boolean = true,
    val otcEnabled: Boolean = true,
    val forexEnabled: Boolean = true,
    val cryptoEnabled: Boolean = true,
    val commoditiesEnabled: Boolean = true,
    val highAccuracyOnly: Boolean = false,
    val riskWarningsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
) {
    /**
     * Checks whether a notification should be presented for a given signal category and accuracy.
     */
    fun isSignalAllowed(category: String, confidenceScore: Int, direction: String): Boolean {
        if (!masterEnabled) return false
        if (direction == "NO_TRADE" && !riskWarningsEnabled) return false
        if (highAccuracyOnly && confidenceScore < 85) return false

        return when (category.uppercase()) {
            "OTC" -> otcEnabled
            "FOREX" -> forexEnabled
            "CRYPTO" -> cryptoEnabled
            "COMMODITIES" -> commoditiesEnabled
            else -> true
        }
    }
}

class NotificationPreferencesRepository private constructor(private val context: Context) {

    private object PreferencesKeys {
        val KEY_MASTER_ENABLED = booleanPreferencesKey("notif_master_enabled")
        val KEY_OTC_ENABLED = booleanPreferencesKey("notif_category_otc")
        val KEY_FOREX_ENABLED = booleanPreferencesKey("notif_category_forex")
        val KEY_CRYPTO_ENABLED = booleanPreferencesKey("notif_category_crypto")
        val KEY_COMMODITIES_ENABLED = booleanPreferencesKey("notif_category_commodities")
        val KEY_HIGH_ACCURACY_ONLY = booleanPreferencesKey("notif_high_accuracy_only")
        val KEY_RISK_WARNINGS = booleanPreferencesKey("notif_risk_warnings")
        val KEY_SOUND_ENABLED = booleanPreferencesKey("notif_sound_enabled")
        val KEY_VIBRATION_ENABLED = booleanPreferencesKey("notif_vibration_enabled")
    }

    val settingsFlow: Flow<NotificationSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            NotificationSettings(
                masterEnabled = preferences[PreferencesKeys.KEY_MASTER_ENABLED] ?: true,
                otcEnabled = preferences[PreferencesKeys.KEY_OTC_ENABLED] ?: true,
                forexEnabled = preferences[PreferencesKeys.KEY_FOREX_ENABLED] ?: true,
                cryptoEnabled = preferences[PreferencesKeys.KEY_CRYPTO_ENABLED] ?: true,
                commoditiesEnabled = preferences[PreferencesKeys.KEY_COMMODITIES_ENABLED] ?: true,
                highAccuracyOnly = preferences[PreferencesKeys.KEY_HIGH_ACCURACY_ONLY] ?: false,
                riskWarningsEnabled = preferences[PreferencesKeys.KEY_RISK_WARNINGS] ?: true,
                soundEnabled = preferences[PreferencesKeys.KEY_SOUND_ENABLED] ?: true,
                vibrationEnabled = preferences[PreferencesKeys.KEY_VIBRATION_ENABLED] ?: true
            )
        }

    suspend fun setMasterEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_MASTER_ENABLED] = enabled
        }
    }

    suspend fun setOtcEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_OTC_ENABLED] = enabled
        }
    }

    suspend fun setForexEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_FOREX_ENABLED] = enabled
        }
    }

    suspend fun setCryptoEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_CRYPTO_ENABLED] = enabled
        }
    }

    suspend fun setCommoditiesEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_COMMODITIES_ENABLED] = enabled
        }
    }

    suspend fun setCategoryEnabled(category: String, enabled: Boolean) {
        when (category.uppercase()) {
            "OTC" -> setOtcEnabled(enabled)
            "FOREX" -> setForexEnabled(enabled)
            "CRYPTO" -> setCryptoEnabled(enabled)
            "COMMODITIES" -> setCommoditiesEnabled(enabled)
        }
    }

    suspend fun setHighAccuracyOnly(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_HIGH_ACCURACY_ONLY] = enabled
        }
    }

    suspend fun setRiskWarningsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_RISK_WARNINGS] = enabled
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_SOUND_ENABLED] = enabled
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun resetToDefaults() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_MASTER_ENABLED] = true
            preferences[PreferencesKeys.KEY_OTC_ENABLED] = true
            preferences[PreferencesKeys.KEY_FOREX_ENABLED] = true
            preferences[PreferencesKeys.KEY_CRYPTO_ENABLED] = true
            preferences[PreferencesKeys.KEY_COMMODITIES_ENABLED] = true
            preferences[PreferencesKeys.KEY_HIGH_ACCURACY_ONLY] = false
            preferences[PreferencesKeys.KEY_RISK_WARNINGS] = true
            preferences[PreferencesKeys.KEY_SOUND_ENABLED] = true
            preferences[PreferencesKeys.KEY_VIBRATION_ENABLED] = true
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: NotificationPreferencesRepository? = null

        fun getInstance(context: Context): NotificationPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = NotificationPreferencesRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
