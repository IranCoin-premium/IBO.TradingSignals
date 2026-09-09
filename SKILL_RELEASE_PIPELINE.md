# 🏆 SKILL: خط لوله انتشار + تست ۱۰ سطحی + یکدستی UI/UX
**Skill ID:** `ibo-release-pipeline-v1` — **وضعیت: فعال و الزام‌آور برای هر اِجم (merge/release)**

## قوانین غیرقابل نقض
1. **هر اِجم = تست ۱۰ سطحی تمام‌پروژه؛ همه‌ی ۱۰ سطح باید ۱۰۰٪ پاس شوند.** حتی یک FAIL = رد اِجم.
2. **انتشار فقط از طریق GitHub Release:** پس از سبز شدن هر ۱۰ سطح، نسخه‌ی جدید به‌صورت
   **APK امضاشده و قابل نصب** (release + debug) آپلود می‌شود — کاربر هیچ SDK، JDK، keystore،
   امضا یا ابزاری نصب/اجرا نمی‌کند؛ فقط دانلود و نصب.
3. **امضای ثابت:** keystore پروژه (`release.keystore` — alias `upload`) در ریپو ثابت است تا
   نسخه‌های جدید روی نسخه‌ی قبلی نصب‌به‌روزرسانی شوند (بدون حذف نصب).
4. **قالب بصری واحد = صفحه‌ی Home (Soft-UI روشن نئومورفیک):** هیچ صفحه/کارت/المپسی حق
   خروج از این قالب را ندارد (چک ماشینی `scripts/verify-ui-uniformity.sh`).
5. **سروور-آتوریتیتی:** هیچ داده‌ی ساختگی (کد رفرال، موجودی، کمیسیون) در کلاینت ممنوع.

## سطوح ده‌گانه (`scripts/verify-full-10level.sh`)
| سطح | عنوان | دروازه | معیار پاس ۱۰۰٪ |
|---|---|---|---|
| L1 | بک‌اند TypeScript | `tsc --noEmit` | exit 0 |
| L2 | بک‌اند تست | `jest` | همه‌ی تست‌ها passed |
| L3 | یکپارچگی سرور-آتوریتیتی | `verify-part12.sh` | RESULT: GREEN |
| L4 | امنیت | مخفی‌بودن secrets | `.env`، `local.properties`، توکن در git نیست |
| L5 | مهاجرت‌ها | ترتیب/سینتکس SQL | همه‌ی migrationها قابل parse |
| L6 | یکدستی UI/UX | `verify-ui-uniformity.sh` | RESULT: PASS |
| L7 | تست اندروید | `gradle :app:testDebugUnitTest --continue` | BUILD SUCCESSFUL |
| L8 | بیلد APK | `gradle assembleRelease assembleDebug` | دو APK خروجی |
| L9 | نصب‌پذیری APK | `aapt/apt` dump badging | `package` + `native-code` صحیح |
| L10 | سلامت کامل پروژه | `gradle build -x lint` بدون `--continue` | BUILD SUCCESSFUL |

## جریان انتشار
```
all-10-levels-PASSED → tag (vX.Y.Z) / workflow_dispatch
  → GitHub Actions: دروازه‌های ۱۰گانه → assembleRelease/Debug
  → GitHub Release (tag = versionName) → APKها + SHA-256 امضا + یادداشت
```
## نصب بدون دردسر
APK ریلیس با امضای ثابت پروژه امضا شده؛ Play Protect ممکن است برای اپ‌های خارج از استور
اعلان عمومی بدهد (طبیعی و بی‌خطر) — فینگرپرینت SHA-256 در یادداشت Release برای راستی‌آزمایی درج می‌شود.

## مسیرهای انتشار در فروشگاه‌ها (الزامی در هر ریلیس)
هر نسخه، علاوه بر GitHub Release، باید مسیر انتشار در فروشگاه‌های منطقه‌ای (مطابق زبان‌های اپ)
طبق سند `STORE_PUBLISHING_AND_IAB.md` پیش ببرد: کافه بازار + مایکت (Android/fa با IAB)،
ایران اپس (iOS)، APKPure (توزیع جهانی)، و ترند منطقه‌ای (Google Play/en، RuStore/ru، AppGallery/tr-ar).
حداقل اقدام در هر ریلیس: به‌روزرسانی manifest در پنل فروشگاه‌ها + تأیید مجوزهای IAB و اینتنت‌ها
(در `app/src/main/AndroidManifest.xml` از قبل فعال‌اند) + اتصال `IabHelper` با کلید خصوصی اکانت.

## امنیت: کلیدهای Firebase (الزام‌آور)
- هرگز literal از کلید/رسور ID در سورس نگذارید (`AIza...` / شناسه اپ). مقداردهی Firebase منحصراً
  از `google-services.json` (از طریق پلاگین Google Services — در نبود آن `WARN` و اپ fail-closed اجرا می‌شود).
- هر نسخه باید بررسی شود: `grep -rE 'AIza[0-9A-Za-z_-]{20,}' app/` خالی باشد؛
  دروازه L4 این را برقرار می‌کند (چک «no Firebase API key literals»).
- ریپو عمومی است: هر فایل شامل ترنسکریپت/کلید باید فوراً از درخت + تاریخچه git پاک و کلید چرخش شود.
