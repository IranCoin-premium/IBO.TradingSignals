package com.example

import com.example.data.local.SignalEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignalHistoryUnitTest {

    @Test
    fun testSignalFilterOutcomeWon() {
        val signals = listOf(
            SignalEntity(
                id = 1,
                asset = "EUR/USD (OTC)",
                category = "OTC",
                direction = "CALL",
                strikePrice = "1.08450",
                currentPrice = "1.08450",
                expiry = "1m",
                payoutRate = "۹۲٪",
                marketRegime = "روند صعودی",
                confidenceScore = 91,
                riskScore = "کم ریسک",
                vetoStatus = "تایید شده",
                rationale = "برخورد با حمایت",
                recommendedBrokers = "Pocket Option",
                status = "WON",
                timestamp = 1000L
            ),
            SignalEntity(
                id = 2,
                asset = "GBP/USD",
                category = "FOREX",
                direction = "PUT",
                strikePrice = "1.26300",
                currentPrice = "1.26300",
                expiry = "5m",
                payoutRate = "۸۸٪",
                marketRegime = "روند نزولی",
                confidenceScore = 84,
                riskScore = "متوسط",
                vetoStatus = "تایید شده",
                rationale = "واگرایی RSI",
                recommendedBrokers = "Quotex",
                status = "LOST",
                timestamp = 2000L
            ),
            SignalEntity(
                id = 3,
                asset = "BTC/USDT",
                category = "CRYPTO",
                direction = "NO_TRADE",
                strikePrice = "64200",
                currentPrice = "64200",
                expiry = "1m",
                payoutRate = "۹۰٪",
                marketRegime = "نوسانات شدید",
                confidenceScore = 40,
                riskScore = "بسیار پرخطر",
                vetoStatus = "رد شده توسط AI",
                rationale = "اسپرد نامتعارف",
                recommendedBrokers = "Deriv",
                status = "NO_TRADE",
                timestamp = 3000L
            )
        )

        val wonList = signals.filter { it.status == "WON" }
        val lostList = signals.filter { it.status == "LOST" }
        val vetoList = signals.filter { it.status == "NO_TRADE" || it.direction == "NO_TRADE" }

        assertEquals(1, wonList.size)
        assertEquals("EUR/USD (OTC)", wonList[0].asset)

        assertEquals(1, lostList.size)
        assertEquals("GBP/USD", lostList[0].asset)

        assertEquals(1, vetoList.size)
        assertEquals("BTC/USDT", vetoList[0].asset)

        // Calculate win rate: 1 won / (1 won + 1 lost) = 50%
        val totalDecided = wonList.size + lostList.size
        val winRate = (wonList.size.toFloat() / totalDecided * 100).toInt()
        assertEquals(50, winRate)
    }

    @Test
    fun testSignalSearchFiltering() {
        val signals = listOf(
            SignalEntity(
                id = 1,
                asset = "GOLD (XAU/USD)",
                category = "COMMODITIES",
                direction = "CALL",
                strikePrice = "2340.50",
                currentPrice = "2340.50",
                expiry = "5m",
                payoutRate = "۹۰٪",
                marketRegime = "صعودی",
                confidenceScore = 88,
                riskScore = "کم",
                vetoStatus = "تایید شده",
                rationale = "بریک‌اوت کانال طلا",
                recommendedBrokers = "Pocket Option",
                status = "WON",
                timestamp = 1000L
            )
        )

        val query = "طلا"
        val filtered = signals.filter { signal ->
            signal.asset.contains(query, ignoreCase = true) ||
            signal.rationale.contains(query, ignoreCase = true)
        }

        assertEquals(1, filtered.size)
        assertTrue(filtered[0].rationale.contains("طلا"))
    }
}
