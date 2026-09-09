package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

/**
 * Representation of the 3 major Iranian Android App Stores:
 * - کافه بازار (Cafe Bazaar)
 * - مایکت (Myket)
 * - ایران اپس (IranApps)
 */
enum class IranianMarket(
    val id: String,
    val titleFa: String,
    val titleEn: String,
    val packageName: String,
    val appSchemeUri: String,
    val rateSchemeUri: String,
    val webUrl: String,
    val brandColorHex: Long,
    val tagline: String,
    val description: String
) {
    CAFE_BAZAAR(
        id = "bazaar",
        titleFa = "کافه بازار",
        titleEn = "Cafe Bazaar",
        packageName = "com.farsitel.bazaar",
        appSchemeUri = "bazaar://details?id=",
        rateSchemeUri = "bazaar://details?id=",
        webUrl = "https://cafebazaar.ir/app/",
        brandColorHex = 0xFF107C41,
        tagline = "بزرگ‌ترین استور اندروید ایران",
        description = "بیش از ۴۰ میلیون کاربر، تاییدیه امنیتی بازار شیلد و نصب سریع"
    ),
    MYKET(
        id = "myket",
        titleFa = "مایکت",
        titleEn = "Myket",
        packageName = "ir.mservices.market",
        appSchemeUri = "myket://details?id=",
        rateSchemeUri = "myket://comment?id=",
        webUrl = "https://myket.ir/app/",
        brandColorHex = 0xFF0288D1,
        tagline = "به‌روزرسانی پرسرعت با ترافیک نیم‌بهاء",
        description = "مارکت معتبر ایرانی با دانلود نیم‌بهاء و سیستم نظرات فعال"
    ),
    IRAN_APPS(
        id = "iranapps",
        titleFa = "ایران اپس",
        titleEn = "IranApps",
        packageName = "ir.tgbs.android.iranapp",
        appSchemeUri = "iranapps://app/",
        rateSchemeUri = "iranapps://usercomments/",
        webUrl = "https://iranapps.ir/app/",
        brandColorHex = 0xFFE65100,
        tagline = "استور باسابقه و تخصصی فارسی",
        description = "مارکت تخصصی ایرانی با پشتیبانی فنی و بررسی سازگاری اپ‌ها"
    )
}

/**
 * Utility manager handling deep linking, package detection, reviews, and updates
 * across Cafe Bazaar, Myket, and IranApps.
 */
object IranianMarketManager {

    /**
     * Checks if a specific market application is installed on the device.
     */
    fun isMarketInstalled(context: Context, market: IranianMarket): Boolean {
        return try {
            val pm: PackageManager = context.packageManager
            pm.getPackageInfo(market.packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Opens the app's detail/download/update page in the selected market app.
     * If the market app is not installed, seamlessly falls back to the official web browser page.
     */
    fun openAppPage(
        context: Context,
        market: IranianMarket,
        targetPackageName: String = context.packageName
    ): Boolean {
        val targetAppId = if (targetPackageName.isNotBlank()) targetPackageName else "com.example"
        val marketUri = "${market.appSchemeUri}$targetAppId"

        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(marketUri)).apply {
                setPackage(market.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // Market app not installed or URI handling failed -> Open browser fallback
            openWebFallback(context, market, targetAppId)
            false
        }
    }

    /**
     * Opens the direct review & 5-star rating flow for the application.
     */
    fun openRatingPage(
        context: Context,
        market: IranianMarket,
        targetPackageName: String = context.packageName
    ): Boolean {
        val targetAppId = if (targetPackageName.isNotBlank()) targetPackageName else "com.example"

        return try {
            val intent = when (market) {
                IranianMarket.CAFE_BAZAAR -> {
                    // Cafe Bazaar uses ACTION_EDIT for direct review dialog
                    Intent(Intent.ACTION_EDIT, Uri.parse("bazaar://details?id=$targetAppId")).apply {
                        setPackage(market.packageName)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
                IranianMarket.MYKET -> {
                    // Myket uses myket://comment?id=...
                    Intent(Intent.ACTION_VIEW, Uri.parse("myket://comment?id=$targetAppId")).apply {
                        setPackage(market.packageName)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
                IranianMarket.IRAN_APPS -> {
                    // IranApps uses iranapps://usercomments/...
                    Intent(Intent.ACTION_VIEW, Uri.parse("iranapps://usercomments/$targetAppId")).apply {
                        setPackage(market.packageName)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // Fallback to the standard app page
            openAppPage(context, market, targetAppId)
            false
        }
    }

    /**
     * Opens the web page for the app in browser when market app is not installed.
     */
    private fun openWebFallback(context: Context, market: IranianMarket, targetAppId: String) {
        try {
            val webUrl = "${market.webUrl}$targetAppId"
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
            Toast.makeText(
                context,
                "در حال بازگشایی وب‌سایت ${market.titleFa}...",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "مرورگری برای نمایش صفحه ${market.titleFa} یافت نشد.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Shares the official download links from all 3 Iranian markets with friends.
     */
    fun shareDownloadLinks(context: Context, targetPackageName: String = context.packageName) {
        val targetAppId = if (targetPackageName.isNotBlank()) targetPackageName else "com.example"
        val shareMessage = buildString {
            appendLine("🌟 اپلیکیشن رسمی ایران باینری آپشن (Iran Binary Option)")
            appendLine("سامانه هوشمند سیگنال‌های فارکس، ارز دیجیتال و باینری آپشن با نرخ برد بالا")
            appendLine()
            appendLine("📲 دانلود و به‌روزرسانی از معتبرترین مارکت‌های ایرانی:")
            appendLine("🟢 کافه بازار:")
            appendLine("${IranianMarket.CAFE_BAZAAR.webUrl}$targetAppId")
            appendLine("🔵 مایکت:")
            appendLine("${IranianMarket.MYKET.webUrl}$targetAppId")
            appendLine("🟠 ایران اپس:")
            appendLine("${IranianMarket.IRAN_APPS.webUrl}$targetAppId")
            appendLine()
            appendLine("🛡️ تایید شده از لحاظ اصالت و امنیت در استورهای ایرانی")
        }

        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareMessage)
                type = "text/plain"
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val shareChooser = Intent.createChooser(sendIntent, "اشتراک‌گذاری لینک اپلیکیشن در مارکت‌ها")
            shareChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareChooser)
        } catch (e: Exception) {
            Toast.makeText(context, "خطا در فراخوانی اشتراک‌گذاری.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Returns the list of all supported Iranian markets.
     */
    fun getAllMarkets(): List<IranianMarket> = IranianMarket.values().toList()
}
