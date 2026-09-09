package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.data.local.SignalEntity
import java.util.Locale

/**
 * Audio Alert Helper for Iran Binary Option Signals.
 * 1. Speaks Persian voice notifications when high-accuracy signals are issued.
 * 2. Plays high-tech audio chime tones to alert traders within the 00-02s golden candle entry window.
 */
object SignalAudioAlertHelper {
    private const val TAG = "SignalAudioAlertHelper"
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    fun init(context: Context) {
        if (tts == null) {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale("fa", "IR"))
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts?.language = Locale.ENGLISH
                    }
                    isTtsInitialized = true
                    Log.d(TAG, "TTS initialized successfully.")
                } else {
                    Log.w(TAG, "TTS initialization failed.")
                }
            }
        }
    }

    /**
     * Plays a high-tech dual-chime alert sound.
     */
    fun playChimeAlert() {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90)
            toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 250)
        } catch (e: Exception) {
            Log.w(TAG, "Failed playing chime alert: ${e.message}")
        }
    }

    /**
     * Speaks Persian signal voice notification.
     */
    fun speakSignalAlert(context: Context, signal: SignalEntity) {
        init(context)
        playChimeAlert()

        val directionFa = when (signal.direction.uppercase()) {
            "CALL" -> "جهت خرید، بالا"
            "PUT" -> "جهت فروش، پایین"
            else -> "هشدار، عدم معامله"
        }

        val speechText = "سیگنال جدید ${signal.asset}. $directionFa. تایم انقضا ${signal.expiry}."

        if (isTtsInitialized && tts != null) {
            try {
                tts?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "SignalAlertId_${signal.id}")
            } catch (e: Exception) {
                Log.w(TAG, "Error speaking alert: ${e.message}")
            }
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isTtsInitialized = false
        } catch (e: Exception) {
            Log.w(TAG, "TTS shutdown error: ${e.message}")
        }
    }
}
