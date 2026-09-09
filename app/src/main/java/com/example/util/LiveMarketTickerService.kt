package com.example.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

data class LiveTickerQuote(
    val asset: String,
    val price: Double,
    val changePercent: Double,
    val candleSecondsRemaining: Int,
    val isUp: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

object LiveMarketTickerService {
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private val _quotes = MutableStateFlow<Map<String, LiveTickerQuote>>(emptyMap())
    val quotes: StateFlow<Map<String, LiveTickerQuote>> = _quotes.asStateFlow()

    private val basePrices = mapOf(
        "EUR/USD (OTC)" to 1.08542,
        "GBP/USD (OTC)" to 1.29410,
        "USD/JPY (OTC)" to 154.230,
        "BTC/USDT" to 94250.00,
        "GOLD (XAU/USD)" to 2748.50,
        "AUD/CAD (OTC)" to 0.89210
    )

    private val currentPrices = basePrices.toMutableMap()
    private var tickerJob: Job? = null

    fun startStreaming() {
        if (tickerJob?.isActive == true) return

        tickerJob = scope.launch {
            while (isActive) {
                val nowSeconds = (System.currentTimeMillis() / 1000 % 60).toInt()
                val secondsRemainingInCandle = 60 - nowSeconds

                val updated = currentPrices.mapValues { (asset, price) ->
                    val delta = when {
                        "BTC" in asset -> (Random.nextDouble(-12.0, 12.0))
                        "GOLD" in asset -> (Random.nextDouble(-0.85, 0.85))
                        "JPY" in asset -> (Random.nextDouble(-0.04, 0.04))
                        else -> (Random.nextDouble(-0.00015, 0.00015))
                    }
                    val newPrice = (price + delta).coerceAtLeast(0.00001)
                    currentPrices[asset] = newPrice

                    val base = basePrices[asset] ?: newPrice
                    val changePct = ((newPrice - base) / base) * 100

                    LiveTickerQuote(
                        asset = asset,
                        price = newPrice,
                        changePercent = changePct,
                        candleSecondsRemaining = secondsRemainingInCandle,
                        isUp = delta >= 0
                    )
                }

                _quotes.value = updated
                delay(1000)
            }
        }
    }

    fun getFormattedPrice(asset: String, fallback: String): String {
        val quote = _quotes.value[asset] ?: return fallback
        return when {
            "BTC" in asset -> String.format(Locale.US, "%.2f", quote.price)
            "GOLD" in asset -> String.format(Locale.US, "%.2f", quote.price)
            "JPY" in asset -> String.format(Locale.US, "%.3f", quote.price)
            else -> String.format(Locale.US, "%.5f", quote.price)
        }
    }
}
