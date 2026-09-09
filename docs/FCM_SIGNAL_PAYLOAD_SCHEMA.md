# 📡 FCM Signal Payload Schema (v1.0 — رسمی و پایدار)

> **قرارداد رسمی کانال اعلان سیگنال:** این سند تنها مرجع معتبر ساختار پیام‌های FCM
> اپلیکیشن «ایران باینری آپشن» است. سمت سرور (Cloud Function / Backend Worker) و
> سمت کلاینت (`IranBinaryMessagingService`) موظف به رعایت دقیق این قرارداد هستند.

## 1. نکات کلیدی رفتار کلاینت

- **فقط `data messages`** پشتیبانی می‌شوند (Data-only payload). نوتیفیکیشن‌های خالص
  `notification` پردازش نمی‌شوند؛ این کار تضمین می‌کند `onMessageReceived` حتی وقتی
  اپ در Foreground/Background/ killed است هم اجرا شود.
- **Topic-based subscribe:** کلاینت پس از ورود، بسته به پلن اشتراک روی یکی از این
  تاپیک‌ها subscribe می‌شود:
  - `high_accuracy_signals` → سیگنال‌های با `confidenceScore >= 80` (پلن‌های پولی)
  - `all_signals` → همه‌ی سیگنال‌ها (پلن رایگان)
- **Missing-key policy:** هر فیلد اختیاری، مقدار پیش‌فرض امن دارد (جدول زیر). سرور
  هرگز نباید به رفتار پیش‌فرض کلاینت تکیه کند و باید همه‌ی فیلدها را ارسال کند.
- **Security note:** کلاینت هیچ فیلدی را به‌عنوان دستور مالی معتبر نمی‌پذیرد؛ اتصال
  واقعی سفارش فقط از طریق API سرور و با توکن کاربر انجام می‌شود.

## 2. اسکیمای کامل فیلدهای `data`

| کلید | نوع | الزامی | پیش‌فرض کلاینت | توضیح |
|---|---|---|---|---|
| `id` | String(Long) | ✅ بله | `System.currentTimeMillis()` | شناسه‌ی یکتای سیگنال — باید با `signals.id` دیتابیس یکسان باشد |
| `asset` | String | ✅ بله | `"EUR/USD (OTC)"` | نماد دارایی + برچسب بازار، مثال: `EUR/USD (OTC)`، `GBP/USD` |
| `direction` | String enum | ✅ بله | `"CALL"` | یکی از دقیق: `CALL` (بالا)، `PUT` (پایین)، `NO_TRADE` (وتوی معامله) |
| `category` | String | ✅ بله | `"OTC"` | دسته‌ی بازار: `OTC`، `FOREX`، `CRYPTO`، `COMMODITY` |
| `strikePrice` | String | ✅ بله | `"1.08500"` | قیمت استرایک (نقطه ورود) — رشته‌ی قالب‌بندی‌شده |
| `currentPrice` | String | ⬜ اختیاری | `strikePrice` | قیمت لحظه‌ی انتشار سیگنال |
| `expiry` | String enum | ✅ بله | `"1m"` | زمان انقضا: `1m`، `5m`، `15m` (فقط این سه مقدار معتبرند) |
| `payoutRate` | String | ✅ بله | `"۹۵٪"` | نرخ بازده بروکر پیشنهادی — با ارقام فارسی، مثال: `۹۵٪` |
| `marketRegime` | String | ✅ بله | `"شکست تثبیت‌شده"` | رژیم بازار به فارسی: `شکست تثبیت‌شده`، `روند صعودی`، `روند نزولی`، `رنج` |
| `confidenceScore` | String(Int) | ✅ بله | `"90"` | عدد صحیح ۰ تا ۱۰۰ به‌صورت رشته؛ `>= 80` یعنی High-Accuracy |
| `riskScore` | String enum | ✅ بله | `"کم ریسک"` | یکی از: `کم ریسک`، `متوسط`، `پر ریسک` |
| `vetoStatus` | String enum | ✅ بله | `"تایید شده"` | یکی از: `تایید شده`، `وتو شده` — `وتو شده` همیشه با `direction=NO_TRADE` همراه است |
| `rationale` | String | ⬜ اختیاری | متن تایید AI | دلیل تحلیلی سیگنال به فارسی (حداکثر ۲۲۰ کاراکتر برای UI نوتیفیکیشن) |
| `recommendedBrokers` | String CSV | ⬜ اختیاری | `"Quotex, Pocket Option"` | نام لاتین بروکرها با جداکننده‌ی `", "` — مثال: `"Quotex, Pocket Option, IQ Option"` |

## 3. نمونه‌ی کامل Payload (سرور → FCM API)

```json
{
  "message": {
    "topic": "high_accuracy_signals",
    "priority": "high",
    "data": {
      "id": "1042",
      "asset": "EUR/USD (OTC)",
      "direction": "CALL",
      "category": "OTC",
      "strikePrice": "1.08500",
      "currentPrice": "1.08500",
      "expiry": "1m",
      "payoutRate": "۹۵٪",
      "marketRegime": "شکست تثبیت‌شده",
      "confidenceScore": "92",
      "riskScore": "کم ریسک",
      "vetoStatus": "تایید شده",
      "rationale": "شکست مومنتوم قوی با RSI در ناحیه تایید",
      "recommendedBrokers": "Quotex, Pocket Option"
    }
  }
}
```

### نمونه‌ی سیگنال وتو شده (NO_TRADE)

```json
{
  "message": {
    "topic": "all_signals",
    "priority": "normal",
    "data": {
      "id": "1043",
      "asset": "GBP/USD",
      "direction": "NO_TRADE",
      "category": "FOREX",
      "strikePrice": "1.26500",
      "currentPrice": "1.26480",
      "expiry": "5m",
      "payoutRate": "۸۹٪",
      "marketRegime": "رنج",
      "confidenceScore": "64",
      "riskScore": "پر ریسک",
      "vetoStatus": "وتو شده",
      "rationale": "بی‌ثباتی نوسان در آستانه خبر اقتصادی",
      "recommendedBrokers": "IQ Option"
    }
  }
}
```

## 4. خطای رایج و رد قرارداد

سرور در صورت نقض هر یک از بندهای زیر، اعلان باید در گیت انتشار (L2/L7) رد شود:

1. ارسال `notification` به‌جای/همراه `data` برای سیگنال‌ها.
2. `direction` خارج از `{CALL, PUT, NO_TRADE}` یا `expiry` خارج از `{1m, 5m, 15m}`.
3. `confidenceScore` غیرعددی یا خارج از بازه‌ی ۰..۱۰۰.
4. `id` تکراری (باعث رد شدن به‌عنوان سیگنال جدید در dedup کلاینت می‌شود).

## 5. نگاشت به UI

| فیلد Payload | مقصد در UI |
|---|---|
| `asset` | عنوان نوتیفیکیشن + عنوان کارت سیگنال |
| `direction` | آیکون/رنگ پیکان (`CALL`=سبز، `PUT`=قرمز، `NO_TRADE`=خاکستری) |
| `expiry` | چیپ «زمان انقضا» در کارت سیگنال |
| `confidenceScore` | نشان درصدی دقت (رنگ سبز `>=80`، زرد `60-79`) |
| `rationale` | زیرنویس نوتیفیکیشن + بخش تحلیل در SignalDetailBottomSheet |
| `recommendedBrokers` | لیست بروکر پیشنهادی در جزئیات سیگنال |

— انتهای سند. هر تغییر در این اسکیما باید با تغییر همزمان در `IranBinaryMessagingService.kt`
و `FcmNotificationHelper.kt` و همین فایل اعمال و در PR جداگانه ثبت شود.