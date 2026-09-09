# 📡 انتشار در فروشگاه‌های اپلیکیشن + خرید درون‌برنامه‌ای (IAB) + حذف هشدار نصب

این سند طبق Skill خط لوله انتشار (`SKILL_RELEASE_PIPELINE.md`) مسیرهای رسمی انتشار را دربردارد.
تخصیص هر فروشگاه به زبان/کشور کاری اپ (fa/ar/en/es/ru/tr) — یعنی «فروشگاه ترندِ کشورهای زبان‌های اپ».

---

## ۱) نقشه فروشگاه‌ها ↔ زبان‌های رابط کاربری

| فروشگاه | کشور/منطقه | زبان | نوع پلتفرم | قابلیت پرداخت درون‌برنامه‌ای |
|---|---|---|---|---|
| کافه بازار | ایران | fa | Android | ✅ IAB (PAY_THROUGH_BAZAAR) |
| مایکت | ایران | fa | Android | ✅ IAB v3 (ir.mservices.market.BILLING) |
| ایران اپس (iApps) | ایران | fa | **iOS** | ❌ (فقط iOS — مسیر نسخه iOS آینده) |
| APKPure | جهانی (بدیل گوگل‌پلی) | en/سایر | Android | ❌ (کانال توزیع) |
| Google Play | جهانی | en/es/ar/tr | Android | ✅ (فروشگاه اصلی — نیاز به انتقال Play Billing) |
| RuStore | روسیه | ru | Android | ✅ (الزامی برای روسیه) |
| Huawei AppGallery | ترکیه/روسیه/عرب | tr/ru/ar | Android | ✅ (دستگاه‌های بدون سرویس‌های گوگل) |
| اپ استار (app-star.store) | ایران | fa | iOS | ❌ |

> **نکته:** «ایران اپس» یعنی `iapps.ir` یک مارکت **iOS** است؛ برای نسخه‌اندروید هدف‌اصلی ایران همان دو فروشگاه کافه بازار و مایکت هستند (هر دو مجوز و اینتنت IAB در `AndroidManifest.xml` اضافه شده‌اند).

---

## ۲) کافه بازار (Android — fa)
- **پنل توسعه‌دهنده رسمی:** https://developers.cafebazaar.ir (لاگین با حساب بازار؛ ساخت اکانت توسعه‌دهنده و تفاهم‌نامه مالی)
- **قدم‌ها:** پروفایل → افزودن اپ (بسته `com.aistudio.iranbinaryoption.trdsig`) → آپ‌لود APK → اطلاعات (آیکون، اسکرین‌شات، دسته‌بندی) → ارسال نسخه → انتشار
- **اپ داخلی بازار:** بسته `com.farsitel.bazaar`
- **دیپ‌لینک صفحه اپ:** `bazaar://details?id=com.aistudio.iranbinaryoption.trdsig`
- **IAB:** مجوز `<uses-permission android:name="com.farsitel.bazaar.permission.PAY_THROUGH_BAZAAR"/>` + اینتنت سرویس `com.farsitel.bazaar.InAppBillingService.BIND` (هر دو در manifest اپ اضافه شده‌اند). SDK مرجع در پنل (IabHelper + کلید عمومی) — ربط کد نهایی با کلید اختصاصی اکانت‌تان انجام می‌شود.
- **کارمزد:** نرخ سهم بازار در توافق‌نامه پنل (به‌طور معمول ۳۰٪؛ گاهی کمپین تخفیف) — در پنل راستی‌آزمایی شود.

## ۳) مایکت (Android — fa)
- **پنل توسعه‌دهنده رسمی:** https://developer.myket.ir
- **مستندات:** https://myket.ir/kb/pages/developer-panel-guide-fa/
- **قدم‌ها (رسمی):** ثبت‌نام → پنل → «آماده‌سازی برنامه» (شناسه، عنوان، اسکرین‌شات، دسته) → آپ‌لود بسته → «راهنمای قدم به قدم تا انتشار»
- **اپ داخلی مایکت:** بسته `ir.mservices.market`
### ۳) مایکت: IAB (رسمی)
مجوز `<uses-permission android:name="ir.mservices.market.BILLING" />` و برای `targetSdkVersion > 29` بلاک visibility:
```xml
<queries>
  <package android:name="ir.mservices.market" />
  <intent>
    <action android:name="ir.mservices.market.InAppBillingService.BIND" />
    <data android:mimeType="*/*" />
  </intent>
</queries>
```
هر دو در `AndroidManifest.xml` اپ اضافه شده‌اند. پیاده‌سازی: `IabHelper` (`startSetup` / `OnIabSetupFinishedListener`) + کلید عمومی base64 از پنل — مستند «پیاده‌سازی پرداخت درون‌برنامه‌ای مایکت».
**مالیات:** کارمزد شاپرک + سهم توسعه‌دهنده (۳۰٪ استاندارد، با کمپین‌های تخفیف تاریخی) + مالیات ارزش افزوده ۱۰٪ (از ۱۴۰۳) — جزئیات دقیق در پنل.

## ۴) ایران اپس (iOS — fa)
- **وب رسمی:** https://iapps.ir — مارکت iOS ایرانی. برای نسخه iOS اپ (آینده) از طریق فرآیند پنل آی‌اپس منتشر شود. در نسخه Android این گزینه به‌عنوان «کانال دریافت» در UI نمایش داده می‌شود (بدون IAB).

## ۵) APKPure (توزیع جهانی — en)
- **پنل رسمی:** https://developer.apkpure.com — ثبت‌نام و ارسال بسته در https://apkpure.com/submit-apk
- رایگان و سریع؛ برای کاربران خارج از ایران که به گوگل‌پلی دسترسی ندارند. بدون IAB؛ در UI به‌عنوان کانال دریافت.

## ۶) فروشگاه‌های ترند منطقه‌ای (کشورهای زبان‌های اپ)
| فروشگاه | کشور | لینک رسمی توسعه‌دهنده | نکته |
|---|---|---|---|
| Google Play | جهانی | https://play.google.com/console | تنها مسیر «هشدار صفر هنگام نصب»؛ نیاز به اکانت ۲۵$ و مرور |
| RuStore | روسیه | https://www.rustore.ru/developers | الزام قانونی برای توزیع در روسیه |
| AppGallery (Huawei) | ترکیه/روسیه/عرب | https://developer.huawei.com/appgallery | برای دستگاه‌های بدون GMS؛ قابلیت IAB |
| اپ استار | ایران (iOS) | https://app-star.store | مسیر iOS تکمیلی |

---

## ۷) هشدار هنگام نصب (Play Protect) — واقعیت + تکمیلی که انجام شده
**واقعیت فنی:** گوگل برای APKهای ساید‌لود هیچ‌گاه «هشدار صفر» تضمین نمی‌دهد؛ اما نکته‌های زیر
هشدار را از «مخرب/بلاک» به «نصب از منبع ناشناس» (بی‌خطر) کاهش می‌دهد — و هنگام نصب از فروشگاه‌های معتبر اساساً هشدار نمی‌بینید:
1. ✅ **امضای release ثابت** (`release.keystore` — همان کلید برای همه نسخه‌ها) → نصب‌به‌روزرسانی بدون «بسته خراب».
2. ✅ **targetSdk 36 / minSdk 24** (SDK خیلی کهنه علامت ناامنی است).
3. ✅ **no debug flag** در build release (تأیید: `debuggable=false`).
4. ✅ **مجوزهای کمینه** (فقط INTERNET/NETWORK_STATE/POST_NOTIFICATIONS/VIBRATE + دو مجوز IAB فروشگاه‌ها).
5. ✅ **بدون بارگذاری کد پویا/تغییر بسته** — باینری ثابت و قابل تکرار.
6. 📌 **آینده:** پس از پذیرش در فروشگاه‌ها (بخش ۲–۶) نصب مستقیماً از کلاینت همان فروشگاه انجام می‌شود (هشدار Play Protect در آن مسیر رخ نمی‌دهد). برای «هشدار صفر مطلق» در کل دنیا مسیر Google Play لازم است (نیاز به انتقال به Play Billing).
7. **راستی‌آزمایی امضا:** `apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk` — فینگرپرینت سرتیفیکت باید با `SIGNING_FINGERPRINT.txt` در ریلیس یکی باشد.

---

## ۸) چک‌لیست نهایی پیش از هر فرستادن به فروشگاه
- [ ] `bash scripts/verify-full-10level.sh` → `ALL 10 LEVELS: 100% PASSED`
- [ ] آیکون (`ic_launcher`) استاندارد ۵۱۲×۵۱۲/۲۵۶ — قابل قبول در همه فروشگاه‌ها
- [ ] اسکرین‌شات طرح Soft-UI (صفحه Home/Subscription) — بدون واترمارک
- [ ] خط ریسک باینری آپشن در توضیحات فروشگاه (مطابق قوانین محتوا)
- [ ] جدا کردن `versionCode`/`versionName` در هر نسخه (فعلاً: ۱.۱.۰، بعدی ۱.۲.۰)
- [ ] امضای release با کلید ثابت + مقایسه فینگرپرینت
- [ ] در پنل مایکت/بازار: ساخت «محصولات درون‌برنامه‌ای» منطبق با پلن‌های ۵گانه و اتصال `IabHelper` کلید خصوصی همان اکانت
- **دیپ‌لینک‌ها (رسمی):** باز کردن صفحه: `myket://details?id=[PACKAGE_NAME]` — باز + دانلود: `myket://download/[PACKAGE_NAME]`