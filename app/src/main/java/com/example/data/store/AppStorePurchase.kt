package com.example.data.store

import androidx.annotation.DrawableRes
import com.example.R

/**
 * فروشگاه‌های اپلیکیشن منطقه‌ای منطبق با زبان‌های رابط کاربری اپ (fa/ar/en/es/ru/tr).
 * جزئیات انتشار برای هر فروشگاه + منابع رسمی: STORE_PUBLISHING_AND_IAB.md
 *
 * applicationId مرجع: com.aistudio.iranbinaryoption.trdsig
 */
enum class AppStore(
    val methodId: String,
    val titleFa: String,
    @DrawableRes val logoRes: Int,
    val brandColor: Long,
    val packageName: String?,
    val deepLinkUri: String,
    val webFallbackUrl: String,
    val billingSupported: Boolean,
    val isIos: Boolean = false
) {
    CAFE_BAZAAR(
        methodId = "STORE_CAFEBAZAAR",
        titleFa = "کافه بازار",
        logoRes = R.drawable.store_ic_cafebazaar,
        brandColor = 0xFF00A550,
        packageName = "com.farsitel.bazaar",
        deepLinkUri = "bazaar://details?id=com.aistudio.iranbinaryoption.trdsig",
        webFallbackUrl = "https://cafebazaar.ir/app/com.aistudio.iranbinaryoption.trdsig",
        billingSupported = true
    ),
    MYKET(
        methodId = "STORE_MYKET",
        titleFa = "مایکت",
        logoRes = R.drawable.store_ic_myket,
        brandColor = 0xFF7B2BFF,
        packageName = "ir.mservices.market",
        deepLinkUri = "myket://details?id=com.aistudio.iranbinaryoption.trdsig",
        webFallbackUrl = "https://myket.ir/app/com.aistudio.iranbinaryoption.trdsig",
        billingSupported = true
    ),
    APKPURE(
        methodId = "STORE_APKPURE",
        titleFa = "APKPure",
        logoRes = R.drawable.store_ic_apkpure,
        brandColor = 0xFF1E7AFF,
        packageName = "com.apkpure.aegon",
        deepLinkUri = "apkpure://search/iran%20binary%20option",
        webFallbackUrl = "https://apkpure.com/search?q=iran%20binary%20option",
        billingSupported = false
    ),
    IRAN_APPS(
        methodId = "STORE_IRANAPPS",
        titleFa = "ایران اپس (iApps)",
        logoRes = R.drawable.store_ic_iapps,
        brandColor = 0xFFFF5A4A,
        packageName = "ir.tgbs.android.iranapp",
        deepLinkUri = "iranapps://app/com.aistudio.iranbinaryoption.trdsig",
        webFallbackUrl = "https://iapps.ir/search/iran-binary-option",
        billingSupported = false,
        isIos = true
    );

    companion object {
        fun fromMethodId(id: String): AppStore? = entries.firstOrNull { it.methodId == id }
    }
}