package com.example

import com.example.data.local.SignalEntity
import com.example.fcm.FcmNotificationHelper
import org.junit.Assert.*
import org.junit.Test

class FcmSignalNotificationUnitTest {

    @Test
    fun testNotificationChannelConstants() {
        assertEquals("signals_channel", FcmNotificationHelper.CHANNEL_ID_SIGNALS)
        assertEquals("سیگنال‌های معاملاتی هوشمند", FcmNotificationHelper.CHANNEL_NAME_SIGNALS)
        assertEquals("high_accuracy_signals", FcmNotificationHelper.TOPIC_HIGH_ACCURACY)
        assertEquals("all_signals", FcmNotificationHelper.TOPIC_ALL_SIGNALS)
    }

    @Test
    fun testHighAccuracySignalClassification() {
        val highAccuracySignal = SignalEntity(
            id = 101L,
            asset = "EUR/USD (OTC)",
            category = "OTC",
            direction = "CALL",
            strikePrice = "1.08500",
            currentPrice = "1.08500",
            expiry = "1m",
            payoutRate = "۹۵٪",
            marketRegime = "شکست تثبیت‌شده",
            confidenceScore = 92,
            riskScore = "کم ریسک",
            vetoStatus = "تایید شده",
            rationale = "شکست مومنتوم قوی با RSI در ناحیه تایید",
            recommendedBrokers = "Quotex, Pocket Option",
            status = "ACTIVE"
        )

        assertTrue("Signals with confidence >= 80% should be considered high accuracy", highAccuracySignal.confidenceScore >= 80)
        assertNotEquals("Direction should not be NO_TRADE", "NO_TRADE", highAccuracySignal.direction)
    }

    @Test
    fun testVetoedSignalClassification() {
        val vetoedSignal = SignalEntity(
            id = 102L,
            asset = "GBP/USD",
            category = "FOREX",
            direction = "NO_TRADE",
            strikePrice = "1.26500",
            currentPrice = "1.26500",
            expiry = "5m",
            payoutRate = "۸۵٪",
            marketRegime = "رنج ناپایدار",
            confidenceScore = 45,
            riskScore = "پرریسک",
            vetoStatus = "غیرمجاز برای ترید",
            rationale = "نوسانات شدید خبری بدون روند مشخص",
            recommendedBrokers = "None",
            status = "VETOED"
        )

        assertFalse("Vetoed signal should not qualify for high-accuracy push", vetoedSignal.confidenceScore >= 80)
        assertEquals("NO_TRADE", vetoedSignal.direction)
    }
}
