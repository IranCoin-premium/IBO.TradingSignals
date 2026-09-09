package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.data.local.SignalEntity
import com.example.data.repository.BrokerItem
import com.example.ui.screens.SignalsHomeScreen
import com.example.ui.theme.IranBinaryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SoftUiSignalsHomeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleSignals = listOf(
        SignalEntity(
            id = 1L,
            asset = "EUR/USD",
            category = "FOREX",
            direction = "CALL",
            strikePrice = "1.08450",
            currentPrice = "1.08452",
            expiry = "1m",
            payoutRate = "92%",
            marketRegime = "Bullish Momentum",
            confidenceScore = 95,
            riskScore = "کم ریسک (Low)",
            vetoStatus = "تایید شده",
            rationale = "برخورد به حمایت معتبر",
            recommendedBrokers = "QUOTEX, POCKET_OPTION",
            status = "ACTIVE",
            isFavorite = false
        ),
        SignalEntity(
            id = 2L,
            asset = "BTC/USD",
            category = "CRYPTO",
            direction = "PUT",
            strikePrice = "64250.0",
            currentPrice = "64245.0",
            expiry = "5m",
            payoutRate = "88%",
            marketRegime = "Bearish Breakout",
            confidenceScore = 91,
            riskScore = "متوسط (Medium)",
            vetoStatus = "تایید شده",
            rationale = "شکست خط روند صعودی",
            recommendedBrokers = "POCKET_OPTION, COINEX",
            status = "ACTIVE",
            isFavorite = true
        )
    )

    private val sampleBrokers = listOf(
        BrokerItem(
            id = "quotex",
            name = "Quotex",
            faName = "کوتکس",
            payoutRate = "95%",
            otc247 = true,
            executionSpeed = "< 0.3s",
            minDeposit = "10$",
            status = "ACTIVE",
            badge = "محبوب‌ترین",
            description = "واریز و برداشت تتری فوری"
        ),
        BrokerItem(
            id = "pocket_option",
            name = "Pocket Option",
            faName = "پاکت آپشن",
            payoutRate = "92%",
            otc247 = true,
            executionSpeed = "< 0.5s",
            minDeposit = "5$",
            status = "ACTIVE",
            badge = "سریع‌ترین",
            description = "پشتیبانی عالی و اتصال ترید سوشال"
        )
    )

    @Test
    fun `verify SignalsHomeScreen renders without crash and tabs work`() {
        composeTestRule.setContent {
            IranBinaryTheme {
                SignalsHomeScreen(
                    signals = sampleSignals,
                    brokers = sampleBrokers,
                    userPlan = "FREE",
                    onAddTradeLog = {},
                    onOpenSubscriptions = {},
                    onOpenSupport = {},
                    onOpenMarkets = {},
                    onOpenTutorial = {},
                    onOpenHistory = {},
                    onOpenNotFoundTest = {},
                    onOpenSettings = {},
                    onOpenTradeJournal = {},
                    onOpenArticles = {},
                    onToggleFavorite = {},
                    onSubmitFeedback = { _, _, _, _, _, _, _ -> }
                )
            }
        }

        composeTestRule.waitForIdle()

        // 1. Verify Home dashboard title exists
        composeTestRule.onNodeWithText("داشبورد معاملات هوشمند").assertExists()

        // 2. Click on "سیگنال‌ها" tab
        composeTestRule.onNodeWithText("سیگنال‌ها").performClick()
        composeTestRule.waitForIdle()

        // Verify Signals header and cards exist
        composeTestRule.onNodeWithText("آپشن‌های معاملاتی").assertExists()
        // LazyColumn only materializes viewport-visible items in the (small) headless
        // test window — scroll the lazy list to the signal card before asserting.
        composeTestRule.onNode(hasScrollAction()).performScrollToNode(hasText("EUR/USD"))
        composeTestRule.onNodeWithText("EUR/USD").assertExists()

        // 3. Click on "عملکرد" tab
        composeTestRule.onNodeWithText("عملکرد").performClick()
        composeTestRule.waitForIdle()

        // Verify Performance tab header
        composeTestRule.onNodeWithText("عملکرد من").assertExists()

        // 4. Click on "ابزارها" tab
        composeTestRule.onNodeWithText("ابزارها").performClick()
        composeTestRule.waitForIdle()

        // Verify Tools tab header
        composeTestRule.onNodeWithText("ابزارهای تحلیلی").assertExists()
    }
}
