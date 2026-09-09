# 🔐 راهنمای رسمی امضای ریلیز (RELEASE_SIGNING_SETUP)

> **اصل طلایی:** کلید امضای ریلیز (`release.keystore`) **هرگز** در گیت کامیت نمی‌شود.
> تداوم امضای ثابت (نصب‌به‌روزرسانی نسخه‌ها روی هم) از **تکرار همانی secretها** در
> همه‌ی بیلدهای CI به دست می‌آید، نه از وجود فایل در ریپو.

## ۱. Secretهای الزامی ریپو (GitHub → Settings → Secrets and variables → Actions)

| نام Secret | محتوا | نمونه‌ی قالب (بدون مقدار واقعی) |
|---|---|---|
| `KEYSTORE_BASE64` | کل فایل keystore به‌صورت Base64 | رشته‌ی Base64 — تولید با دستور بخش ۲ |
| `STORE_PASSWORD` | رمز keystore | رشته‌ی قوی ۲۰+ کاراکتری |
| `KEY_ALIAS` | نام alias کلید درون keystore | `upload` |
| `KEY_PASSWORD` | رمز خود کلید (معمولاً برابر STORE_PASSWORD) | رشته‌ی قوی ۲۰+ کاراکتری |

> نام‌ها دقیقاً باید همین‌ها باشند؛ `release-apk.yml` و `app/build.gradle.kts`
> همین نام‌ها را از محیط می‌خوانند. مقادیر هرگز در Issue/PR/Log درج نشود.

## ۲. تولید keystore رسمی (یک‌بار — توسط مالک پروژه، خارج از ریپو)

```bash
keytool -genkeypair -v \
  -keystore release.keystore \
  -alias upload \
  -keyalg RSA -keysize 4096 -validity 10950 \
  -storepass "<STORE_PASSWORD>" -keypass "<KEY_PASSWORD>" \
  -dname "CN=IranBinaryOption Trading Signals, OU=Mobile, O=IBO, L=Tehran, C=IR"
```

- `validity` برابر ۳۰ سال تا ابد پشتیبانی به‌روزرسانی تضمین شود.
- خروجی Base64 برای Secret:

```bash
base64 -w0 release.keystore        # خروجی این دستور = مقدار KEYSTORE_BASE64
```

- **بکاپ اجباری:** فایل اصلی keystore در مخزن رمزنگاری‌شده‌ی شخصی (مثلاً
  password manager با attachment یا دیسک آفلاین) نگهداری شود. گم شدن کلید =
  عدم امکان انتشار به‌روزرسانی روی نسخه‌های قبلی برای همیشه.

## ۳. فینگرپرینت رسمی امضای جاری (SHA-256)

```
SHA-256: 49:C4:8E:92:D5:EA:2E:BD:C1:8E:3F:0B:B3:DE:B6:4E:70:A0:26:C4:33:79:31:42:5C:13:95:6D:BD:2C:34:5D
```

- Alias: `upload`
- Validity: 2026-09-09 تا 2056-09-01
- الگوریتم: RSA 4096-bit (SHA384withRSA)
- هر Release فایل `SIGNING_FINGERPRINT.txt` دارد؛ کاربران باید مقدار داخل آن
  را با خط بالا مقایسه کنند (دستور: `keytool -printcert -jarfile app.apk`).
- در صورت چرخش کلید (بخش ۵)، این فینگرپرینت باید همزمان به‌روز شود.

## ۴. جریان CI (خودکار — `release-apk.yml`)

1. Decode از `KEYSTORE_BASE64` به `$GITHUB_WORKSPACE/release.keystore` (گام اول، قبل از گیت تست).
2. `verify-full-10level.sh` → `assembleRelease` با secretهای env امضا می‌کند؛
   در صورت نبود هر secret بیلد **با خطای صریح** رد می‌شود (fail-loud در build.gradle.kts).
3. فینگرپرینت SHA-256 از همان keystore استخراج و در `SIGNING_FINGERPRINT.txt` منتشر می‌شود.

## ۵. چک‌لیست چرخش (Rotation) — فقط در صورت لو رفتن کلید

- [ ] keystore جدید با تاریخ اعتبار جدید بساز (بخش ۲).
- [ ] چهار secret ریپو را با مقادیر جدید به‌روز کن (همزمان).
- [ ] فینگرپرینت جدید را در همین سند و README به‌روز کن؛ در یادداشت Release اعلان کن
      نصب جدید (حذف نسخه قبل) برای یک بار لازم است.
- [ ] secretهای لو رفته را از همه‌ی لاگ‌ها/گفتگوها پاک کن و دسترسی‌ها را ممیزی کن.