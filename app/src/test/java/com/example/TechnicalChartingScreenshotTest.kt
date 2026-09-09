package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.components.TechnicalChartingCanvas
import com.example.ui.components.TechnicalDataPoint
import com.example.ui.theme.IranBinaryTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Part 29: Automated QA & UI Verification
 * Screenshot test for the Technical Charting Canvas.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class TechnicalChartingScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun technical_charting_canvas_screenshot() {
        val mockData = List(20) { i ->
            TechnicalDataPoint(
                timestamp = 1700000000000L + i * 60000,
                close = 1.0850f + (Math.sin(i.toDouble() / 3.0) * 0.001).toFloat(),
                rsi = 50f + (Math.sin(i.toDouble() / 2.0) * 20).toFloat(),
                bbUpper = 1.0880f,
                bbLower = 1.0820f,
                bbMiddle = 1.0850f
            )
        }

        composeTestRule.setContent {
            IranBinaryTheme {
                TechnicalChartingCanvas(data = mockData)
            }
        }

        composeTestRule.waitForIdle()

        // Capture to specified path for Part 29 audit
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/technical_charting_canvas.png")
    }
}
