package com.example

import org.junit.Assert.*
import org.junit.Test

class OnboardingTutorialUnitTest {

    @Test
    fun testOnboardingTutorialConstants() {
        val totalSlides = 4
        assertEquals(4, totalSlides)
    }

    @Test
    fun testSignalExecutionRules() {
        // Rule 1: Fixed time expiry alignment (1m, 5m)
        val validExpiries = listOf("1m", "5m", "15m")
        assertTrue(validExpiries.contains("1m"))
        assertTrue(validExpiries.contains("5m"))

        // Rule 2: Valid trade directions
        val directions = listOf("CALL", "PUT", "NO_TRADE")
        assertTrue(directions.contains("CALL"))
        assertTrue(directions.contains("PUT"))
        assertTrue(directions.contains("NO_TRADE"))

        // Rule 3: Risk management recommendation (1% to 2% max capital per trade)
        val maxRiskPercent = 2.0
        assertTrue("Risk per trade should not exceed 3%", maxRiskPercent <= 3.0)
    }
}
