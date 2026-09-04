package com.example

import com.example.util.IranianMarket
import com.example.util.IranianMarketManager
import org.junit.Assert.*
import org.junit.Test

class IranianMarketUnitTest {

    @Test
    fun testAllThreeMajorIranianMarketsConfigured() {
        val markets = IranianMarketManager.getAllMarkets()
        assertEquals(3, markets.size)
        assertTrue(markets.contains(IranianMarket.CAFE_BAZAAR))
        assertTrue(markets.contains(IranianMarket.MYKET))
        assertTrue(markets.contains(IranianMarket.IRAN_APPS))
    }

    @Test
    fun testCafeBazaarConfiguration() {
        val bazaar = IranianMarket.CAFE_BAZAAR
        assertEquals("com.farsitel.bazaar", bazaar.packageName)
        assertEquals("bazaar://details?id=", bazaar.appSchemeUri)
        assertEquals("https://cafebazaar.ir/app/", bazaar.webUrl)
        assertEquals("کافه بازار", bazaar.titleFa)
        assertEquals("Cafe Bazaar", bazaar.titleEn)
        assertEquals(0xFF107C41, bazaar.brandColorHex)
    }

    @Test
    fun testMyketConfiguration() {
        val myket = IranianMarket.MYKET
        assertEquals("ir.mservices.market", myket.packageName)
        assertEquals("myket://details?id=", myket.appSchemeUri)
        assertEquals("myket://comment?id=", myket.rateSchemeUri)
        assertEquals("https://myket.ir/app/", myket.webUrl)
        assertEquals("مایکت", myket.titleFa)
        assertEquals("Myket", myket.titleEn)
        assertEquals(0xFF0288D1, myket.brandColorHex)
    }

    @Test
    fun testIranAppsConfiguration() {
        val iranApps = IranianMarket.IRAN_APPS
        assertEquals("ir.tgbs.android.iranapp", iranApps.packageName)
        assertEquals("iranapps://app/", iranApps.appSchemeUri)
        assertEquals("iranapps://usercomments/", iranApps.rateSchemeUri)
        assertEquals("https://iranapps.ir/app/", iranApps.webUrl)
        assertEquals("ایران اپس", iranApps.titleFa)
        assertEquals("IranApps", iranApps.titleEn)
        assertEquals(0xFFE65100, iranApps.brandColorHex)
    }

    @Test
    fun testMarketIdsAndPackageNamesAreUnique() {
        val markets = IranianMarketManager.getAllMarkets()
        val ids = markets.map { it.id }.toSet()
        val packageNames = markets.map { it.packageName }.toSet()

        assertEquals(markets.size, ids.size)
        assertEquals(markets.size, packageNames.size)
    }
}
