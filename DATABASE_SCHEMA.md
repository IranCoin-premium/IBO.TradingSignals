# طراحی دیتابیس PostgreSQL — پلتفرم IBO Binary Option Trading Signals (Part 03)

> **افشای ریسک قانونی (الزامی و دائمی):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»

این سند تشریح‌کننده ساختار نرمال‌شده (3NF)، نوع داده‌ها، روابط کلیدهای خارجی و ایندکس‌های پایگاه داده PostgreSQL برای سامانه سیگنال‌دهی باینری آپشن بر اساس الزامات Part 03 است.

---

## ۱. دیاگرام رابطه موجودیت‌ها (ER Diagram)

### الف) دیاگرام متنی ASCII ERD
```text
+---------------------+             +---------------------+
|      languages      |             |        users        |
+---------------------+             +---------------------+
| PK code (VARCHAR)   |<-----------\| PK id (UUID)        |<-------------+
|    name_native      |             |    phone_or_email   |              |
|    direction        |             |    password_hash    |              |
|    is_active        |             |    role (user/admin)|              |
+---------------------+             | FK preferred_lang   |              |
           |                        |    country_code     |              |
           | 1                      |    created_at       |              |
           |                        +---------------------+              |
           |                                   |                         |
           | N                                 | 1                       |
+---------------------+                        |                         |
|    translations     |                        |                         |
+---------------------+                        | N                       |
| PK id (UUID)        |             +---------------------+              |
|    entity_type      |             |       orders        |              |
|    entity_id        |             +---------------------+              |
| FK language_code    |             | PK id (UUID)        |              |
|    content (TEXT)   |             | FK user_id          |              |
|    updated_at       |      +----->| FK plan_id          |              |
+---------------------+      |      |    payment_method   |              |
                             |      |    status           |              |
+---------------------+      |      |    amount (18,8)    |              |
|        plans        |      |      |    currency         |              |
+---------------------+      |      |    created_at       |              |
| PK id (UUID)        |------+      +---------------------+              |
|    name             |                        |                         |
|    duration_days    |                        | 1                       |
|    price_irr (18,8) |         +--------------+--------------+          |
|    price_usd (18,8) |         | N                           | N        |
|    is_installment   | +---------------------+     +------------------+ |
|    installment_cnt  | |  payment_receipts   |     |   installments   | |
+---------------------+ +---------------------+     +------------------+ |
           |            | PK id (UUID)        |     | PK id (UUID)     | |
           | 1          | FK order_id         |     | FK order_id      | |
           |            |    file_path        |     |    inst_number   | |
           |            |    tracking_code    |     |    due_date      | |
           | N          | FK reviewed_by -----\     |    status        | |
+---------------------+ |    review_status    |     |    amount (18,8) | |
|    subscriptions    | |    reviewed_at      |     |    paid_at       | |
+---------------------+ +---------------------+     +------------------+ |
| PK id (UUID)        |                                                  |
| FK user_id ---------+--------------------------------------------------+
| FK plan_id          |
|    status           |
|    start_at         |
|    end_at           |
|    payment_method   |
+---------------------+

+---------------------+             +---------------------+
|      analysts       |             |       signals       |
+---------------------+             +---------------------+
| PK id (UUID)        | 1         N | PK id (UUID)        |
| FK user_id ---------+------------>| FK analyst_id       |
|    display_name     |             |    symbol           |
|    verified (bool)  |             |    option_type      |
+---------------------+             |    entry_reference  |
                                    |    strike_reference |
                                    |    expiry_at        |
                                    |    payout_rate      |
                                    |    confidence_level |
                                    |    ai_review_status |
                                    |    published_at     |
                                    |    expires_at       |
                                    +---------------------+
                                               | 1
                                               |
                                               | N
+---------------------+             +---------------------+
|     audit_logs      |             |  signal_deliveries  |
+---------------------+             +---------------------+
| PK id (UUID)        |             | PK id (UUID)        |
|    actor (human/ai) |             | FK signal_id        |
|    agent_name       |             | FK user_id ---------+---> users(id)
|    action           |             |    delivered_at     |
|    target_entity    |             +---------------------+
|    before_state     |
|    after_state      |
|    created_at       |             +---------------------+
+---------------------+             |    webhooks_log     |
                                    +---------------------+
+---------------------+             | PK id (UUID)        |
|   admin_settings    |             |    source (enum)    |
+---------------------+             |    raw_payload (JSON|
| PK key_name         |             |    processed (bool) |
|    secret_ref       |             |    received_at      |
|    service_name     |             +---------------------+
|    updated_by       |
|    updated_at       |             +---------------------+
+---------------------+             |    media_assets     |
                                    +---------------------+
                                    | PK id (UUID)        |
                                    |    type (img/video) |
                                    |    purpose          |
                                    |    source_url       |
                                    |    has_watermark    |
                                    |    has_logo         |
                                    |    created_by_agent |
                                    | FK style_ref_id     |
                                    |    created_at       |
                                    +---------------------+
```

---

## ۲. مشخصات کامل جداول پایگاه داده

### ۱. جدول `users` (کاربران سیستم)
* `id` (UUID, Primary Key): شناسه یکتا تولیدشده با `gen_random_uuid()`
* `phone_or_email` (VARCHAR(255), UNIQUE, NOT NULL): شماره همراه یا پست الکترونیکی
* `password_hash` (VARCHAR(255), NOT NULL): هش امن رمز عبور (Argon2id یا Bcrypt)
* `role` (VARCHAR(20), NOT NULL): نقش کاربر با قید `CHECK (role IN ('user', 'analyst', 'admin'))`
* `preferred_language` (VARCHAR(10), FK -> `languages(code)`): زبان منتخب کاربر (پیش‌فرض: `fa`)
* `country_code` (VARCHAR(10)): پیش‌شماره کشور برای تعیین درگاه‌های بومی مجاز
* `created_at` (TIMESTAMP WITH TIME ZONE): زمان ثبت‌نام

### ۲. جدول `plans` (پلن‌های اشتراک)
* `id` (UUID, Primary Key): شناسه پلن
* `name` (VARCHAR(100), NOT NULL): عنوان تجاری پلن
* `duration_days` (INTEGER, NOT NULL): مدت اعتبار به روز
* `price_irr` (NUMERIC(18, 8), NOT NULL): قیمت به ریال با دقت اعشاری بالا
* `price_usd_equivalent` (NUMERIC(18, 8), NOT NULL): معادل دلاری (جهت پرداخت تتر)
* `is_installment` (BOOLEAN): آیا امکان پرداخت اقساطی (BNPL) دارد؟
* `installment_count` (INTEGER): تعداد اقساط (پیش‌فرض ۱)

### ۳. جدول `subscriptions` (اشتراک‌های فعال و منقضی)
* `id` (UUID, Primary Key): شناسه یکتای اشتراک
* `user_id` (UUID, FK -> `users(id)`): شناسه کاربر دارنده اشتراک
* `plan_id` (UUID, FK -> `plans(id)`): شناسه پلن خریداری‌شده
* `status` (VARCHAR(20)): قید `CHECK (status IN ('active', 'expired', 'cancelled'))`
* `start_at` (TIMESTAMP WITH TIME ZONE): زمان دقیق شروع دسترسی
* `end_at` (TIMESTAMP WITH TIME ZONE): زمان دقیق پایان و انقضا
* `payment_method` (VARCHAR(50)): روش پرداختی تسویه‌شده

### ۴. جدول `orders` (سفارش‌های خرید)
* `id` (UUID, Primary Key): شناسه سفارش
* `user_id` (UUID, FK -> `users(id)`): شناسه کاربر خریدار
* `plan_id` (UUID, FK -> `plans(id)`): شناسه پلن درخواستی
* `payment_method` (VARCHAR(50)): قید `CHECK (payment_method IN ('myket', 'bazaar', 'iranapps', 'card_to_card', 'shaba', 'nowpayments', 'googleplay_pending', 'digipay', 'snappay', 'other_pending'))`
* `status` (VARCHAR(40)): قید `CHECK (status IN ('pending', 'awaiting_manual_review', 'processing', 'paid', 'failed'))`
* `amount` (NUMERIC(18, 8), NOT NULL): مبلغ دقیق تراکنش
* `currency` (VARCHAR(10), NOT NULL): واحد پولی (IRR / USDT / USD)
* `created_at`, `updated_at`: ثبت تاریخچه‌های تغییر وضعیت

### ۵. جدول `payment_receipts` (رسیدهای کارت‌به‌کارت و واریز مستقیم)
* `id` (UUID, Primary Key): شناسه فیش
* `order_id` (UUID, FK -> `orders(id)`): شناسه سفارش متناظر
* `file_path` (VARCHAR(500), NOT NULL): مسیر ذخیره در Object Storage امن (نه فایل خام در دیتابیس)
* `tracking_code` (VARCHAR(100), NOT NULL): کد رهگیری یا شماره ارجاع بانکی
* `reviewed_by` (UUID, FK -> `users(id)`): اپراتور یا ادمین بررسی‌کننده
* `review_status` (VARCHAR(20)): قید `CHECK (review_status IN ('pending', 'approved', 'rejected'))`
* `reviewed_at`: زمان بازبینی

### ۶. جدول `installments` (اقساط دیجی‌پی / اسنپ‌پی)
* `id` (UUID, Primary Key): شناسه قسط
* `order_id` (UUID, FK -> `orders(id)`): شناسه سفارش اصلی
* `installment_number` (INTEGER, NOT NULL): شماره قسط (۱ تا ۴)
* `due_date` (TIMESTAMP WITH TIME ZONE, NOT NULL): موعد سررسید قسط
* `status` (VARCHAR(20)): قید `CHECK (status IN ('pending', 'paid', 'overdue'))`
* `amount` (NUMERIC(18, 8), NOT NULL): مبلغ قسط با دقت بالا
* `paid_at`: تاریخ پرداخت موفق

### ۷. جدول `webhooks_log` (ثبت و دیباگ کال‌بک درگاه‌ها)
* `id` (UUID, Primary Key): شناسه لاگ
* `source` (VARCHAR(30)): قید `CHECK (source IN ('myket', 'bazaar', 'iranapps', 'nowpayments', 'digipay', 'snappay', 'other'))`
* `raw_payload` (JSONB, NOT NULL): بدنه خام دریافتی از درگاه
* `processed` (BOOLEAN, DEFAULT FALSE): آیا توسط وب‌هووک هَندلر پردازش شده است؟
* `received_at`: زمان دریافت درخواست

### ۸. جدول `analysts` (تحلیلگران انسانی مجاز)
* `id` (UUID, Primary Key): شناسه تحلیلگر
* `user_id` (UUID, UNIQUE, FK -> `users(id)`): ارجاع به حساب کاربری با نقش `analyst`
* `display_name` (VARCHAR(100), NOT NULL): نام عمومی نمایش‌داده‌شده برای تحلیلگر
* `verified` (BOOLEAN): وضعیت تایید صلاحیت توسط ناظر ارشد

### ۹. جدول `signals` (سیگنال‌های معاملاتی باینری آپشن)
* `id` (UUID, Primary Key): شناسه سیگنال
* `analyst_id` (UUID, FK -> `analysts(id)`): تحلیلگر انسانی ارائه‌دهنده
* `symbol` (VARCHAR(30), NOT NULL): جفت‌ارز یا دارایی (مانند `EUR/USD`, `BTC/USDT`)
* `option_type` (VARCHAR(10), NOT NULL): جهت آپشن با قید `CHECK (option_type IN ('call', 'put'))`
* `entry_reference` (NUMERIC(18, 8), NOT NULL): قیمت مرجع ورود
* `strike_reference` (NUMERIC(18, 8), NOT NULL): نرخ استرایک
* `expiry_at` (TIMESTAMP WITH TIME ZONE, NOT NULL): زمان انقضای قرارداد باینری
* `payout_rate` (NUMERIC(5, 2), NOT NULL): درصد بازدهی مورد انتظار (مثلاً ۸۵.۰۰)
* `confidence_level` (NUMERIC(5, 2), NOT NULL): درصد اطمینان تحلیلگر
* `ai_review_status` (VARCHAR(20)): تایید هوش مصنوعی با قید `CHECK (ai_review_status IN ('approved', 'rejected', 'pending'))`
* `published_at`, `expires_at`: زمان انتشار عمومی به کاربران فعال و زمان اعتبار سیگنال

### ۱۰. جدول `signal_deliveries` (ردیابی تحویل سیگنال)
* `id` (UUID, Primary Key): شناسه ردیابی
* `signal_id` (UUID, FK -> `signals(id)`): سیگنال ارسال‌شده
* `user_id` (UUID, FK -> `users(id)`): کاربر دریافت‌کننده
* `delivered_at`: زمان ثبت دریافت موفق در دستگاه کاربر

### ۱۱. جدول `languages` (زبان‌های پشتیبانی‌شده)
* `code` (VARCHAR(10), Primary Key): کد زبان (`fa`, `en`, `ar`, `tr`, `ru`, `es`)
* `name_native` (VARCHAR(100), NOT NULL): نام زبان به زبان بومی (فارسی، English، العربية...)
* `direction` (VARCHAR(10)): جهت چیدمان با قید `CHECK (direction IN ('rtl', 'ltr'))`
* `is_active` (BOOLEAN): وضعیت فعال بودن در سوییچر اپلیکیشن

### ۱۲. جدول `translations` (محتوای چندزبانه پویا)
* `id` (UUID, Primary Key): شناسه ترجمه
* `entity_type` (VARCHAR(30)): قید `CHECK (entity_type IN ('ui_string', 'news', 'journal'))`
* `entity_id` (VARCHAR(100), NOT NULL): کلید یا شناسه ردیف اصلی
* `language_code` (VARCHAR(10), FK -> `languages(code)`): زبان مقصد
* `content` (TEXT, NOT NULL): متن ترجمه‌شده
* `updated_at`: تاریخ به‌روزرسانی

### ۱۳. جدول `media_assets` (دارایی‌های تصویری و ویدیویی)
* `id` (UUID, Primary Key): شناسه دارایی
* `type` (VARCHAR(10)): قید `CHECK (type IN ('image', 'video'))`
* `purpose` (VARCHAR(20)): قید `CHECK (purpose IN ('news', 'journal', 'marketing'))`
* `source_url` (VARCHAR(500), NOT NULL): آدرس امن دسترسی به مدیا
* `has_watermark` (BOOLEAN): بررسی درج واترمارک رسمی
* `has_logo` (BOOLEAN): بررسی انطباق با دی‌ان‌ای برند و لوگو
* `created_by_agent` (VARCHAR(100), NOT NULL): شناسه ایجنت تولیدکننده
* `style_reference_id` (UUID, FK -> `media_assets(id)`): ارجاع به استایل مرجع برند
* `created_at`: زمان تولید

### ۱۴. جدول `admin_settings` (ارجاع به متغیرهای محرمانه)
* `key_name` (VARCHAR(100), Primary Key): نام کلید پیکربندی (مانند `NOWPAYMENTS_IPN_SECRET_REF`)
* `secret_ref` (VARCHAR(255), NOT NULL): **اشاره‌گر به Secret Manager یا متغیرهای سیستمی (اکیداً فاقد مقدار خام کلید)**
* `service_name` (VARCHAR(100), NOT NULL): نام سرویس مرتبط
* `updated_by` (VARCHAR(100), NOT NULL): شناسه تغییردهنده
* `updated_at`: زمان تغییر

### ۱۵. جدول `audit_logs` (ثبت تغییرات و ممیزی سیستم)
* `id` (UUID, Primary Key): شناسه لاگ ممیزی
* `actor` (VARCHAR(20), NOT NULL): قید `CHECK (actor IN ('human_admin', 'ai_agent'))`
* `agent_name` (VARCHAR(100)): در صورت اقدام هوش مصنوعی، نام دقیق ایجنت
* `action` (VARCHAR(100), NOT NULL): عملیات انجام‌شده (مانند `UPDATE_PRICE`, `ROLLBACK_LAYOUT`)
* `target_entity` (VARCHAR(100), NOT NULL): موجودیت هدف
* `before_state` (JSONB): وضعیت داده‌ها قبل از تغییر (امکان بازگشت و Rollback)
* `after_state` (JSONB): وضعیت داده‌ها پس از تغییر
* `created_at`: زمان ثبت تغییر

---

## ۳. تضمین‌های امنیتی و طراحی معماری داده

1. **حفاظت از مبالغ با دقت بالا (High Precision):** فیلدهای `amount` در `orders` و `installments` به همراه نرخ‌های ورودی سیگنال از نوع `NUMERIC(18, 8)` تعیین شده‌اند تا خطای گردکردن (Rounding Issues) در هیچ تراکنشی رخ ندهد.
2. **عدم ذخیره‌سازی مقادیر خام (Zero Raw Secrets):** جدول `admin_settings` تنها نام متغیرهای محیطی یا مسیرهای Vault را نگهداری می‌کند و هیچ مقدار کلیدی در آن ذخیره نمی‌شود.
3. **ممیزی کامل تغییرات ایجنت‌ها (AI Agent Auditability):** هر عملیاتی که توسط AI Agentها روی پروداکشن انجام شود در جدول `audit_logs` به همراه `before_state` و `after_state` ضبط می‌شود تا در صورت مغایرت در سطوح تست، بازگردانی (Rollback) آنی میسر باشد.
