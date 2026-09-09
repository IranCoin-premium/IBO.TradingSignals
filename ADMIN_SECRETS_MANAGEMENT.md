# مستند جامع پنل مدیریت کلیدهای محرمانه و دستیاران هوش مصنوعی (Part 09)

> **افشای ریسک قانونی (الزامی و دائمی):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»

---

## ۱. اصل غیرقابل‌مذاکره امنیت Secretها

1. **عدم ذخیره کلید خام در پایگاه‌داده اصلی:** هیچ مقدار کلید خام API Key یا Private Secret در PostgreSQL پروژه ذخیره نمی‌شود. پایگاه‌داده صرفاً `secret_ref` (مانند `GITHUB_SECRET:NOWPAYMENTS_API_KEY` یا `RENDER_ENV:DIGIPAY_CLIENT_SECRET`) و متادیتا (شامل ماسک نمایشی `****ab12` و اطلاعات ادمین ویرایش‌کننده) را ذخیره می‌کند.
2. **عدم دسترسی فرانت‌اند به مقادیر خام:** رابط کاربری در هیچ پاسخی مقدار خام کلیدها را دریافت نمی‌کند. تنها ۴ کاراکتر انتهایی جهت اطمینان از مقداردهی قبلی (`****9f21`) نمایش داده می‌شود.
3. **تفکیک لایه‌ها و نقش‌ها:** فقط ادمین‌های با نقش `ADMIN` یا `SUPER_ADMIN` با لاگین معتبر به این بخش دسترسی دارند. هرگونه اقدام از طرف کاربران عادی با خطای `403 Forbidden` مسدود می‌شود.

---

## ۲. وایرفریم متنی تب "مدیریت دستیاران و کلیدها" در پنل ادمین (UI Wireframe)

```text
+-------------------------------------------------------------------------------------------------------------+
| IBO Admin Panel > Assistants & Secrets Management                                [Admin: ali.khani | Logout] |
+-------------------------------------------------------------------------------------------------------------+
| [Dashboard]  [Signals]  [Subscriptions]  [AI Agents & Supervisors]  [*Assistants & Secrets*]  [Audit Logs]   |
+-------------------------------------------------------------------------------------------------------------+
|                                                                                                             |
|  (!) هشدار امنیتی: مقادیر خام کلیدها هرگز در دیتابیس عمومی ذخیره نمی‌شوند و به صورت مستقیم به Vault منتقل می‌شوند.   |
|                                                                                                             |
|  فیلتر دسته‌بندی: [همه (9)]  [دستیاران هوش مصنوعی (2)]  [درگاه‌های پرداخت (3)]  [استورها و پیام‌رسانی (4)]            |
|                                                                                                             |
|  +-------------------------------------------------------------------------------------------------------+  |
|  | سرویس / دستیار               | دسته‌بندی    | کلید ثبت‌شده | ارائه‌دهنده مقصد | وضعیت   | آخرین بروزرسانی | عملیات |  |
|  +------------------------------+-------------+--------------+-----------------+---------+-----------------+--------+  |
|  | Google Flow (nanobanana)     | ai_assistant| ****bb90     | GitHub Secrets  | [فعال]  | 2026-09-05      | [ویرایش]|  |
|  | Translation LLM Service      | ai_assistant| ****55aa     | Render Env      | [فعال]  | 2026-09-05      | [ویرایش]|  |
|  | NowPayments Gateway API      | payment     | ****9f21     | GitHub Secrets  | [فعال]  | 2026-09-04      | [ویرایش]|  |
|  | NowPayments IPN Secret       | payment     | ****c41a     | GitHub Secrets  | [فعال]  | 2026-09-04      | [ویرایش]|  |
|  | DigiPay / SnapPay API        | payment     | ****88e3     | Render Env      | [فعال]  | 2026-09-04      | [ویرایش]|  |
|  | CafeBazaar / Myket SDK       | app_store   | ****3b77     | Render Env      | [فعال]  | 2026-09-03      | [ویرایش]|  |
|  | Geo-IP Resolver Service      | localization| ****1044     | Render Env      | [فعال]  | 2026-09-05      | [ویرایش]|  |
|  | SMTP & Push Provider         | messaging   | ****77f0     | Render Env      | [فعال]  | 2026-09-01      | [ویرایش]|  |
|  | n8n Instance URL & Webhook   |orchestration| ****ee01     | Render Env      | [فعال]  | 2026-09-05      | [ویرایش]|  |
|  +-------------------------------------------------------------------------------------------------------+  |
|                                                                                                             |
+-------------------------------------------------------------------------------------------------------------+

  [پاپ‌آپ مدال ویرایش امن کلید - هنگام کلیک روی دکمه ویرایش]:
  +-----------------------------------------------------------------------------------------+
  | ویرایش امن کلید: Google Flow (nanobanana)                                         [ X ] |
  +-----------------------------------------------------------------------------------------+
  |  مقصد ذخیره‌سازی ابری: GitHub Actions Secrets (مخزن IBO.TradingSignals)                     |
  |  مقدار قبلی کلید: ****bb90                                                             |
  |                                                                                         |
  |  مقدار جدید کلید (API Key / Secret):                                                    |
  |  [ ****************************************************** ] (تایپ مخفی - Password Mode)  |
  |                                                                                         |
  |  وضعیت فعال‌بودن دستیار: [✓] سرویس فعال است                                             |
  |                                                                                         |
  |  [بخش توسعه‌پذیر پارت ۱۱ - پارامترهای دستیار هوش مصنوعی]:                               |
  |  - مدل فعال: [ nanobanana-v2        ]                                                   |
  |  - سقف مدت زمان تیزر ویدیویی: [ 60 ثانیه ]                                              |
  |  - نسبت‌های ابعاد مجاز: [✓] 16:9  [✓] 9:16  [✓] 1:1  [✓] 4:5  [✓] 3:2                   |
  |                                                                                         |
  |  [لغو و بازگشت]                                          [ذخیره در مخزن امن و ثبت لاگ]  |
  +-----------------------------------------------------------------------------------------+
```

---

## ۳. مشخصات API لایه Backend (OpenAPI Specification)

### ۱. دریافت لیست سرویس‌ها و متادیتا
* **متد و مسیر:** `GET /api/v1/admin/secrets`
* **سطح دسترسی:** فقط `ADMIN` و `SUPER_ADMIN` (از طریق هدر `Authorization: Bearer <JWT>`)
* **پاسخ نمونه (کد 200):**
```json
{
  "status": "success",
  "data": {
    "total": 9,
    "services": [
      {
        "key_name": "GOOGLE_FLOW_API_KEY",
        "service_name": "google_flow_media",
        "display_name": "Google Flow (nanobanana) Image/Video Generator",
        "category": "ai_assistant",
        "is_active": true,
        "masked_hint": "****bb90",
        "provider_target": "github_secrets",
        "secret_ref": "GITHUB_SECRET:GOOGLE_FLOW_API_KEY",
        "assistant_metadata": {
          "model": "nanobanana-v2",
          "maxDuration": 60
        },
        "updated_by": "admin@ibo.ir",
        "updated_at": "2026-09-05T11:17:34.000Z"
      }
    ]
  }
}
```

### ۲. به‌روزرسانی کلید و متادیتای دستیار
* **متد و مسیر:** `POST /api/v1/admin/secrets/:key_name`
* **سطح دسترسی:** فقط `ADMIN` و `SUPER_ADMIN`
* **بدنه درخواست (Request Body):**
```json
{
  "raw_secret_value": "sk-flow-nanobanana-sample-key-1234",
  "is_active": true,
  "assistant_metadata": {
    "model": "nanobanana-v2",
    "maxDuration": 45
  }
}
```
* **پاسخ موفقیت (کد 200):**
```json
{
  "status": "success",
  "message": "تنظیمات کلید با موفقیت در مخزن امن به‌روزرسانی شد.",
  "data": {
    "key_name": "GOOGLE_FLOW_API_KEY",
    "service_name": "google_flow_media",
    "secret_ref": "GITHUB_SECRET:GOOGLE_FLOW_API_KEY",
    "masked_hint": "****1234",
    "is_active": true,
    "assistant_metadata": {
      "model": "nanobanana-v2",
      "maxDuration": 45
    },
    "updated_by": "admin@ibo.ir",
    "updated_at": "2026-09-05T11:17:34.000Z"
  }
}
```

---

## ۴. نحوه اتصال امن به GitHub Actions Secrets API و Render API

### الف) اتصال به GitHub Actions Secrets API
در این سناریو، کلیدها از طریق الگوریتم رمزنگاری نامتقارن Libsodium (Sealed Box) در مبدا رمزگذاری می‌شوند، به طوری که حتی سرور واسط بعد از رمزگذاری قادر به خواندن مقدار نیست:
1. دریافت کلید عمومی ریپازیتوری با متد `GET`:
   ```http
   GET https://api.github.com/repos/{owner}/{repo}/actions/secrets/public-key
   Authorization: Bearer {GITHUB_ADMIN_PAT}
   ```
2. رمزگذاری مقدار خام کلید با الگوریتم `crypto_box_seal` با استفاده از `key_id` و کلید عمومی دریافت شده.
3. ارسال مقدار رمزگذاری شده با متد `PUT`:
   ```http
   PUT https://api.github.com/repos/{owner}/{repo}/actions/secrets/{SECRET_NAME}
   Authorization: Bearer {GITHUB_ADMIN_PAT}
   Content-Type: application/json

   {
     "encrypted_value": "base64_encoded_sealed_box_payload",
     "key_id": "key_id_from_step_1"
   }
   ```

### ب) اتصال به Render REST API
برای متغیرهای محیطی سرویس Backend هاست‌شده روی Render:
```http
PUT https://api.render.com/v1/services/{SERVICE_ID}/env-vars
Authorization: Bearer {RENDER_API_KEY}
Content-Type: application/json

[
  {
    "key": "DIGIPAY_CLIENT_SECRET",
    "value": "new_secret_value_here"
  }
]
```

---

## ۵. ثبت واقعه‌نگاری و ره‌گیری ممیزی (Audit Logging)

به ازای هرگونه تغییر، یک رکورد قطعی با مشخصات زیر در جدول `audit_logs` درج می‌گردد:
* `actor`: `'human_admin'`
* `action`: `'UPDATE_SECRET'`
* `target_entity`: نام کلید (مثلاً `'GOOGLE_FLOW_API_KEY'`)
* `before_state` و `after_state`: حاوی `secret_ref` و `masked_hint` (بدون مقدار خام).
