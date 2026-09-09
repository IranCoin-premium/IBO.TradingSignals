# PART 12 — دستور اتصال AI Studio به بک‌اند Referral & Sales Partnership

> مخاطب: جمنای داخل اپ AI Studio («ایران باینری آپشن»)
> وضعیت بک‌اند: **کدنویسی‌شده، تایپ‌چک (0 خطا)، ۵۴/۵۴ تست پاس** — فقط اتصال سمت کلاینت باقی است.

## ۱) منبع حقیقت تب‌ها — Tab Registry
قبل از هر رندر تب، این endpoint را صدا بزن و تب‌ها را از روی آن بساز — هرگز لیست تب را هاردکد نکن:

```
GET /api/v1/tabs/manifest
→ { readiness: 'backend_ready', tabs: [ {tab_key, title_i18n_key, route, icon, audience, status, feature_flags} ] }
```

تب‌های backend_ready فعلی: `referral_partner` (/partner) — `sales_partnership` (/sales-partnership) — `affiliate_dashboard` (/affiliate).
قانون: تب فقط وقتی نمایش داده می‌شود که `status !== 'planned'` و مخاطب (`audience`) با نقش کاربر بخواند.

## ۲) Endpoint های مجاز کلاینت (فقط این‌ها)
| عملیات | متد و مسیر | احراز |
|---|---|---|
| صفحه عمومی همکاری در فروش | `GET /api/v1/referral/partnership` | عمومی |
| ثبت کلیک رفرال | `POST /api/v1/referral/click/:code` | عمومی |
| کد معرف من | `GET /api/v1/referral/my-code` | توکن کاربر |
| داشبورد شریک (کلیک/تبدیل/موجودی/برداشت‌ها) | `GET /api/v1/referral/dashboard` | توکن کاربر |
| درخواست برداشت | `POST /api/v1/referral/payouts` `{amount, method, currency}` | توکن کاربر |
| تصمیم ادمین برداشت | `POST /api/v1/referral/admin/payouts/:id/decision` | توکن ADMIN |
| برگشت کمیسیون (ریفاند) | `POST /api/v1/referral/admin/conversions/reverse` | توکن ADMIN |

## ۳) قوانین غیرقابل نقض کلاینت
1. **محاسبه کمیسیون ممنوع در کلاینت** — فقط نمایش مقادیر دریافتی از بک‌اند (`balance`, `commission_amount`). هر محاسبه محلی = FAIL.
2. **خط ریسک همیشه نمایش داده شود** — مقدار `risk_disclosure` از `GET /referral/partnership`، بدون ویرایش، در صفحه عمومی و داشبورد شریک.
3. **هیچ تضمین درآمدی** — `income_guarantees: 'none'`؛ هر متن وعده سود = FAIL فوری.
4. i18n: کلیدهای `tabs.referralPartner`, `tabs.salesPartnership`, `tabs.affiliateDashboard` در هر ۶ زبان (fa/en/ar/ru/...) اضافه شود.
5. خطاهای بک‌اند (`SELF_REFERRAL_BLOCKED`, `BELOW_MIN_PAYOUT`, `INSUFFICIENT_BALANCE`, `INVALID_PAYOUT_TRANSITION`) عیناً و بدون بازنویسی به کاربر نشان داده شود.

## ۴) چرخه تست الزامی (۱۰ سطح، قبل از اعلام PASS)
L1 manifest سه تب را برمی‌گرداند → L2 صفحه عمومی خط ریسک را نشان می‌دهد → L3 با توکن کاربر کد معرف ساخته می‌شود (`IBO…`) → L4 داشبورد صفرِ اولیه درست است → L5 درخواست برداشت زیر حداقل با کد خطای درست رد می‌شود → L6 مسیرهای admin با توکن USER خطای 403 می‌دهند → L7 بدون توکن 401 → L8 تب‌ها فقط از manifest رندر می‌شوند (بدون هاردکد) → L9 i18n شش‌زبانه کامل → L10 سناریوی کامل E2E (کلیک→ثبت‌نام→تبدیل→موجودی→برداشت) با داده تست.

## ۵) فرمت گزارش پاس Gemini
```
PART12 STATUS: PASSED (10/10 levels) | FAILED at level X
EVIDENCE: <اسکرین‌شات/لاگ هر سطح>
CLAIMS: <ادعاهای قابل راستی‌آزمایی در ریپو>
```
هر ادعای ناسازگار با ریپو توسط ایجنت ناظر در Codespace رد و پرامپت اصلاحی صادر می‌شود (لوپ تا پاس واقعی).
