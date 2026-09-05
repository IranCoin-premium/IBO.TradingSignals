# یکپارچه‌سازی درگاه‌های پرداخت — پلتفرم IBO Binary Option Trading Signals (Part 04)

> **افشای ریسک قانونی (الزامی و دائمی):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»

این سند مشخص‌کننده الزامات فنی، اندپوینت‌های ارتباطی، اعتبارسنجی‌های سرور-به-سرور (Server-to-Server Verification)، امنیت وب‌هووک‌ها و جدول نگاشت وضعیت‌های سفارش برای تمام روش‌های پرداختی سامانه بر اساس الزامات Part 04 است.

---

## ۱. جدول نگاشت روش‌های پرداخت به ماشین وضعیت سفارش (Order States Mapping)

| روش پرداخت (Payment Method) | شناسه در دیتابیس | وضعیت اولیه | وضعیت‌های میانی | وضعیت نهایی موفق | وضعیت نهایی ناموفق | مرجع و شیوه تایید نهایی |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **کافه‌بازار (Bazaar IAP)** | `bazaar` | `pending` | `processing` | `paid` | `failed` | استعلام مستقیم سرور-به-سرور از API کافه‌بازار با Purchase Token |
| **مایکت (Myket IAP)** | `myket` | `pending` | `processing` | `paid` | `failed` | استعلام سرور-به-سرور از API مایکت با X-Access-Token |
| **ایران‌اپس (IranApps IAP)**| `iranapps` | `pending` | `processing` | `paid` | `failed` | استعلام مستقیم سرور-به-سرور با توکن خرید |
| **کارت‌به‌کارت + فیش** | `card_to_card` | `pending` | `awaiting_manual_review` | `paid` | `failed` | **بررسی و تایید انسانی ادمین** در پنل مدیریت پس از تطبیق حساب |
| **حواله شبا / پایا / پل / ساتنا**| `shaba` | `pending` | `awaiting_manual_review` | `paid` | `failed` | ثبت کد رهگیری بانکی + بررسی مالی اپراتور یا وب‌هووک PSP طرف قرارداد |
| **تتر / NowPayments** | `nowpayments` | `pending` | `processing` | `paid` | `failed` | وب‌هووک IPN رسمی با اعتبارسنجی امضای رمزنگاری‌شده `HMAC-SHA512` |
| **اقساط ۴ ماهه دیجی‌پی** | `digipay` | `pending` | `processing` | `paid` (با قسط ۱) | `failed` | وب‌هووک تسویه قسط اول دیجی‌پی + ثبت اقساط ۲ تا ۴ در `installments` |
| **اقساط ۴ ماهه اسنپ‌پی** | `snappay` | `pending` | `processing` | `paid` (با قسط ۱) | `failed` | وب‌هووک تسویه قسط اول اسنپ‌پی + فعال‌سازی اشتراک با Grace Period |
| **گوگل‌پلی (Google Play)** | `googleplay_pending` | `disabled` | - | - | - | **غیرفعال**؛ نمایش پاپ‌آپ چندزبانه `googleplay_coming_soon` |
| **درگاه‌های محلی سایر کشورها**| `other_pending` | `disabled` | - | - | - | **غیرفعال**؛ برچسب "به‌زودی" متناظر با زبان کاربر (ترکیه، روسیه، کشورهای عربی) |

---

## ۲. مشخصات و اندپوینت‌های پیاده‌سازی‌شده درگاه‌ها

### ۱. پرداخت‌های درون‌برنامه‌ای استورها (Myket / Bazaar / IranApps)
* **اندپوینت سرور:** `POST /api/v1/payments/:provider/verify`
* **بدنه درخواست (Body):**
  ```json
  {
    "orderId": "order-uuid-123",
    "productId": "subscription_vip_monthly",
    "purchaseToken": "token_from_client_sdk"
  }
  ```
* **امنیت و اعتبارسنجی:**
  * کلاینت اندروید صرفاً توکن دریافتی از استور را ارسال می‌کند.
  * بک‌اند با فراخوانی API رسمی ارائه‌دهنده (کافه‌بازار، مایکت یا ایران‌اپس) به صورت Server-to-Server صحت خرید و فعال بودن اشتراک را استعلام می‌نماید.
  * تنها پس از دریافت تاییدیه `HTTP 200` معتبر از سرور استور، وضعیت سفارش به `PAID` تبدیل شده و رکورد اشتراک فعال ایجاد می‌شود.
  * *مستندات رسمی:*
    * کافه‌بازار: [https://developers.cafebazaar.ir/fa/docs/developer-api/in-app-purchase/](https://developers.cafebazaar.ir/fa/docs/developer-api/in-app-purchase/)
    * مایکت: [https://developer.myket.ir/documentation/in-app-purchase/server-api](https://developer.myket.ir/documentation/in-app-purchase/server-api)
    * ایران‌اپس: [http://developer.iranapps.ir/](http://developer.iranapps.ir/)

---

### ۲. کارت‌به‌کارت و آپلود فیش رسید واریزی
* **اندپوینت ارسال رسید توسط کاربر:** `POST /api/v1/payments/card-transfer/submit-receipt`
  * **بدنه درخواست:** `orderId`, `trackingCode`, `filePath`, `fileMimeType`, `fileSizeBytes`
  * **محدودیت‌های امنیتی:**
    * فرمت‌های مجاز: صرفاً `image/jpeg`, `image/png`, `application/pdf`
    * حداکثر حجم: ۵ مگابایت (`5MB`)
    * فایل در حافظه ابری (Object Storage) ذخیره شده و فقط آدرس فایل در دیتابیس ثبت می‌گردد.
  * **تغییر وضعیت:** سفارش به وضعیت `awaiting_manual_review` منتقل می‌شود و هیچ دسترسی‌ای فعال نمی‌گردد.
* **اندپوینت بازبینی رسید توسط ادمین (Admin Review):**
  * `POST /api/v1/payments/receipts/:receiptId/review` (محدود به دسترسی `ADMIN` و `SUPER_ADMIN`)
  * **بدنه درخواست:** `{"action": "approve" | "reject", "reason": "علت در صورت رد"}`
  * در صورت `approve`: سفارش `PAID` شده، اشتراک فعال و نوتیفیکیشن موفقیت ارسال می‌شود.
  * در صورت `reject`: سفارش `FAILED` شده، علت رد ثبت و به کاربر نمایش داده می‌شود.

---

### ۳. حواله مستقیم بانکی (شبا / پایا / پل / ساتنا)
* **اندپوینت ثبت اطلاعات واریز:** `POST /api/v1/payments/bank-transfer/submit-tracking`
* **بدنه درخواست:** `orderId`, `trackingCode`, `bankName`
* **منطق کارکرد:** سفارش به وضعیت `awaiting_manual_review` رفته تا پس از مشاهده در صورتحساب رسمی بانکی توسط حسابداری، از طریق پنل مدیریت تایید شود.

---

### ۴. درگاه دارایی دیجیتال و رمزارز (NowPayments — Crypto / USDT)
* **اندپوینت ایجاد فاکتور پرداخت:** `POST /api/v1/payments/digital-asset/create-invoice`
  * ایجاد Invoice از طریق API اختصاصی NowPayments با پارامترهای `price_amount`, `price_currency: 'USD'`, `pay_currency: 'USDTTRC20'`
  * بازگردانی لینک صفحه پرداخت و ثبت وضعیت سفارش به `processing`.
* **اندپوینت وب‌هووک (IPN Callback):** `POST /api/v1/webhooks/nowpayments`
  * **اعتبارسنجی رمزنگاری‌شده امضا (HMAC-SHA512):**
    * طبق مستندات رسمی NowPayments، تمام کلیدهای بدنه پیام به ترتیب الفبایی مرتب شده و با کلید محرمانه `NOWPAYMENTS_IPN_SECRET` هش `HMAC-SHA512` محاسبه می‌شود.
    * مقدار محاسبه‌شده با هدر `x-nowpayments-sig` با الگوریتم زمان‌ثابت (`crypto.timingSafeEqual`) مقایسه می‌گردد.
    * در صورت عدم تطابق امضا، درخواست با `HTTP 401 Unauthorized` رد می‌شود.
  * در وضعیت‌های `finished` و `confirmed`: سفارش به `PAID` ارتقا یافته و اشتراک فعال می‌گردد.
  * در وضعیت‌های `waiting` و `confirming`: وضعیت سفارش `processing` باقی می‌ماند.
  * *مستندات رسمی:* [https://nowpayments.io/doc](https://nowpayments.io/doc)

---

### ۵. پرداخت‌های اقساطی ۴ ماهه (BNPL — دیجی‌پی و اسنپ‌پی)
* **اندپوینت ایجاد قرارداد اقساط:** `POST /api/v1/payments/:provider/create-installment-plan` (`digipay` | `snappay`)
* **منطق عملیاتی:**
  * محاسبه مبلغ کل و تقسیم آن به ۴ قسط مساوی با سررسیدهای ۳۰، ۶۰، ۹۰ و ۱۲۰ روزه در جدول `installments`.
* **اندپوینت‌های وب‌هووک تسویه اقساط:**
  * `POST /api/v1/webhooks/digipay` و `POST /api/v1/webhooks/snappay`
  * **تسویه قسط اول:** به محض دریافت تاییدیه قسط اول، سفارش `PAID` شده و اشتراک کاربر `active` می‌شود.
  * **دوره مهلت و سررسید (Grace Period Policy):** در صورت تاخیر بیش از ۵ روز در پرداخت اقساط بعدی، وضعیت اشتراک به `past_due` تغییر یافته و دسترسی سیگنال تا زمان تسویه معلق می‌شود.
  * *مستندات رسمی:* دیجی‌پی: [https://docs.dpay.ir/](https://docs.dpay.ir/) | اسنپ‌پی: [https://snapppay.ir/merchant-api/](https://snapppay.ir/merchant-api/)

---

### ۶. گوگل‌پلی (Google Play Billing — وضعیت غیرفعال)
* در لایه فرانت‌اند/کلاینت، دکمه گوگل‌پلی در حالت `disabled` قرار دارد.
* با کلیک روی دکمه، پاپ‌آپ چندزبانه با ترجمه کلید `googleplay_coming_soon` («به‌زودی راه‌اندازی می‌شود») به زبان جاری کاربر نمایش داده می‌شود.
* **هیچ تراکنش یا اندپوینت پرداخت فعال تجاری برای این گزینه در حال حاضر باز نیست.**

---

### ۷. درگاه‌های محلی بین‌المللی برای زبان‌ها و بازارهای هدف
جهت پوشش زبان‌های هدف غیر از فارسی، درگاه‌های محلی پرکاربرد شناسایی و در وضعیت `coming_soon` ساختاربندی شده‌اند:
* **ترکیه (زبان ترکی `tr`):** درگاه‌های بومی `Papara` و `Troy`
* **روسیه (زبان روسی `ru`):** درگاه‌های بومی `Mir` و سامانه پرداخت سریع `SBP`
* **کشورهای عربی (زبان عربی `ar`):** سامانه‌های بومی `STC Pay` و `Fawry`

---

## ۳. فهرست اندپوینت‌های API پیاده‌سازی شده در Part 04

```text
POST /api/v1/payments/:provider/verify              -> تایید خرید درون‌برنامه‌ای استورها (myket / bazaar / iranapps)
POST /api/v1/payments/card-transfer/submit-receipt  -> ارسال تصویر رسید کارت‌به‌کارت
POST /api/v1/payments/receipts/:receiptId/review   -> بازبینی و تایید/رد فیش توسط ادمین
POST /api/v1/payments/bank-transfer/submit-tracking -> ثبت کد رهگیری حواله بانکی (شبا/پایا/ساتنا)
POST /api/v1/payments/digital-asset/create-invoice  -> ایجاد فاکتور خرید تتر (NowPayments)
POST /api/v1/webhooks/nowpayments                   -> وب‌هووک امضاشده کریپتو (HMAC-SHA512)
POST /api/v1/payments/:provider/create-installment-plan -> ایجاد قرارداد اقساطی ۴ ماهه (دیجی‌پی / اسنپ‌پی)
POST /api/v1/webhooks/digipay                       -> وب‌هووک تسویه اقساط دیجی‌پی
POST /api/v1/webhooks/snappay                       -> وب‌هووک تسویه اقساط اسنپ‌پی
GET  /api/v1/payments/gateways/available            -> فهرست درگاه‌های فعال و درگاه‌های برچسب «به‌زودی»
```
