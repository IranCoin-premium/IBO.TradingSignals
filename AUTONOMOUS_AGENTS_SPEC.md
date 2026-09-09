# مستند جامع استقلال کامل ایجنت‌ها، سیستم خودتنظیم‌گر (Self-Tuning) و پنل ادمین اختصاصی (Part 11)

> **افشای ریسک قانونی (الزامی و دائمی در تمام مستندات و ارتباطات پلتفرم):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»

---

## ۱. وایرفریم متنی تب توسعه‌یافته "مدیریت دستیاران و ایجنت‌ها" (UI Wireframe)

این تب جایگزین یا موازی جدید نیست، بلکه گسترش مستقیم تب طراحی‌شده در Part 09 است. با کلیک بر روی هر ردیف ایجنت، پنل ۴ بخشی اختصاصی با جزئیات کامل باز می‌شود:

```text
+-----------------------------------------------------------------------------------------------------------------------+
| IBO Admin Panel > Assistants & Autonomous AI Agents                                        [Admin: ali.khani | Logout] |
+-----------------------------------------------------------------------------------------------------------------------+
| [Dashboard]  [Signals]  [Subscriptions]  [*AI Agents & Assistants*]  [Secrets & Gateways]  [Audit Logs]               |
+-----------------------------------------------------------------------------------------------------------------------+
|                                                                                                                       |
|  لیست ایجنت‌های مستقل پلتفرم:                                                                                         |
|  +-----------------------------------------------------------------------------------------------------------------+  |
|  | نام ایجنت         | وضعیت استقلال | نسخه فعال | لول آخرین تست | شکست ۲۴ساعت | فیوز محافظ (Circuit) | عملیات       |  |
|  +-------------------+---------------+-----------+---------------+-------------+----------------------+--------------+  |
|  | UIUXAgent         | [✓ فعال]      | v3 (self) | Level 10 OK   | 0 / 5       | [عادی - متصل]        | [مدیریت پنل] |  |
|  | TranslatorAgent   | [✓ فعال]      | v2 (self) | Level 10 OK   | 1 / 5       | [عادی - متصل]        | [مدیریت پنل] |  |
|  | ImageVideoAgent   | [✓ فعال]      | v1 (admin)| Level 10 OK   | 0 / 5       | [عادی - متصل]        | [مدیریت پنل] |  |
|  | SupportAgent      | [✓ فعال]      | v1 (admin)| Level 10 OK   | 0 / 5       | [عادی - متصل]        | [مدیریت پنل] |  |
|  +-----------------------------------------------------------------------------------------------------------------+  |
|                                                                                                                       |
+-----------------------------------------------------------------------------------------------------------------------+

=========================================================================================================================
[نمای تفصیلی پنل اختصاصی ۴ بخشی پس از کلیک روی دکمه "مدیریت پنل" برای: UIUXAgent]
=========================================================================================================================

+-----------------------------------------------------------------------------------------------------------------------+
| پنل اختصاصی ایجنت مستقل: UIUXAgent (Master UI/UX Optimization Engine)                              [ بستن / بازگشت X ] |
+-----------------------------------------------------------------------------------------------------------------------+
|                                                                                                                       |
|  [بخش الف: API & Secrets — کلیدهای امن ارجاعی]                                                                        |
|  - کلید محرمانه اتصال: GITHUB_SECRET:GOOGLE_FLOW_API_KEY                                                              |
|  - ماسک امنیتی نمایشی: ****bb90                                                                                       |
|  - مقصد ذخیره ابری: GitHub Actions Secrets                                                                            |
|  - دسترسی کلید: فقط خواندنی سیستمی (بدون نمایش مقدار خام در فرانت یا ارسال به کلاینت)                                 |
|                                                                                                                       |
|-----------------------------------------------------------------------------------------------------------------------|
|                                                                                                                       |
|  [بخش ب: System Instruction فعلی و تاریخچه نسخه‌ها (Self-Tuned & Admin)]                                              |
|  نسخه فعال کنونی: v3 [تولید خودکار توسط ایجنت (self) | ارتقا یافته در: 2026-09-05 11:20]                              |
|  +-----------------------------------------------------------------------------------------------------------------+  |
|  | متن دستورالعمل فعال:                                                                                            |  |
|  | "Master UI/UX Agent. Preserves brand assets, maintains 8dp grid, passes 10-level quality gate before            |  |
|  | production deployment. Risk disclosure is permanent and non-negotiable..."                                      |  |
|  +-----------------------------------------------------------------------------------------------------------------+  |
|  تاریخچه نسخه‌ها:                                                                                                     |
|  - v3 (فعال) | توسط: self  | تست Staging: ۱۰/۱۰ پاس شد | خلاصه: بهینه‌سازی کامپوننت پاپ‌آپ خرید طبق فیدبک سطح ۷        |  |
|  - v2        | توسط: self  | تست Staging: ۱۰/۱۰ پاس شد | خلاصه: تنظیم کانتراست دکمه‌های تم تاریک                       |  |
|  - v1        | توسط: admin | تست Staging: ۱۰/۱۰ پاس شد | خلاصه: دستورالعمل پایه اولیه                                  |  |
|  [مشاهده دیف (Diff) بین v2 و v3]             [دکمه بازگردانی اضطراری دستی (Rollback to v2)]                           |
|                                                                                                                       |
|-----------------------------------------------------------------------------------------------------------------------|
|                                                                                                                       |
|  [بخش ج: Fine-tune & Behavior Parameters — پارامترهای رفتار خودکار]                                                   |
|  - وضعیت اجرای مستقل (Autonomous Mode):    (•) فعال      ( ) غیرفعال (نیازمند تایید دستی ادمین)                       |
|  - سطح محافظه‌کاری تغییرات:                 [ محافظه‌کارانه (Conservative) ▼ ]                                          |
|  - حداکثر دفعات تلاش مجدد (Max Retry):     [ 3 بار ] (در صورت شکست، بازگشت به نسخه قبلی)                              |
|  - آستانه فیوز محافظ (Circuit Breaker):     [ 5 شکست در ۲۴ ساعت ] (در صورت عبور، استقلال قطع می‌شود)                  |
|  - دامنه دسترسی به فایل‌ها (Allowed Files): frontend/src/components/**, frontend/src/styles/**                        |
|  - جداول مجاز پایگاه داده:                 translations                                                               |
|  [ذخیره تغییرات پارامترها]                                                                                            |
|                                                                                                                       |
|-----------------------------------------------------------------------------------------------------------------------|
|                                                                                                                       |
|  [بخش د: Autonomy Log — کارنامه تغییرات کاملاً خودکار و تست‌های Staging/Production]                                   |
|  +-----------------------------------------------------------------------------------------------------------------+  |
|  | شناسه لاگ      | نوع عملیات               | محیط       | وضعیت نهایی       | مرحله رد شده | تلاش | لینک ممیزی   |  |
|  +----------------+--------------------------+------------+-------------------+--------------+------+--------------+  |
|  | log-98214      | SELF_TUNE_INSTRUCTION    | production | passed_all_levels | - (10/10 OK) | ۱    | [audit_#442] |  |
|  | log-98190      | PROPOSE_UI_MODAL_CHANGE  | staging    | passed_all_levels | - (10/10 OK) | ۲    | [audit_#440] |  |
|  | log-98012      | PROPOSE_GRID_TWEAK       | staging    | rolled_back_at_lvl| Level 7 (CLS)| ۳    | [audit_#435] |  |
|  +-----------------------------------------------------------------------------------------------------------------+  |
|                                                                                                                       |
+-----------------------------------------------------------------------------------------------------------------------+
```

---

## ۲. شبه‌کد (Pseudocode) مکانیزم خودتنظیم‌گر (Self-Tuning) و رانر تست Staging/Shadow

```typescript
/**
 * PSEUDOCODE: Autonomous Self-Tuning & Shadow Staging Test Runner
 */
async function runAutonomousSelfTuning(agentName: string, performanceInsights: PerformanceReport) {
  // ۱. بررسی فیوز محافظ و فعال بودن حالت خودکار
  const settings = await db.getAutonomySettings(agentName);
  if (!settings.autonomy_enabled || settings.circuit_broken) {
    logger.warn(`Autonomy halted for ${agentName}. Requires manual admin review.`);
    return;
  }

  // ۲. تولید متن پیشنهادی Draft Instruction بر اساس تجربیات اخیر
  const currentInstruction = await db.getActiveInstruction(agentName);
  const draftInstruction = await generateRefinedInstruction({
    currentInstruction,
    recentFailures: performanceInsights.failedExecutions,
    lessonsLearned: performanceInsights.insights
  });

  // ۳. بررسی محدودیت‌های سخت و ابدی (Immutable Guardrails) قبل از هر تستی
  if (!verifyImmutableGuardrails(draftInstruction)) {
    await logSecurityRejection(agentName, "Draft instruction attempted to breach immutable guardrails");
    return;
  }

  // ۴. اجرای زنجیره ۱۰ لولی تست تشدیدی در محیط ایزوله Staging/Shadow
  let attempt = 0;
  let stagingPassed = false;
  let lastFailedLevel = null;

  while (attempt < settings.max_retry && !stagingPassed) {
    attempt++;
    const testSuiteResult = await runEscalating10LevelGateOnStaging(agentName, draftInstruction);
    
    if (testSuiteResult.all_passed) {
      stagingPassed = true;
    } else {
      lastFailedLevel = testSuiteResult.failed_at_level;
      // اصلاح تدریجی درفت در صورت شکست و تکرار از لول ۱
      draftInstruction = await autoPatchDraftInstruction(draftInstruction, testSuiteResult.errorDetails);
    }
  }

  // ۵. نتیجه‌گیری و ثبت تصمیم
  if (stagingPassed) {
    // ارتقای خودکار بدون دخالت انسان به Production
    const newVersionNumber = currentInstruction.version_number + 1;
    await db.transaction(async (trx) => {
      await trx.deactivateAllVersions(agentName);
      await trx.insertInstructionVersion({
        agent_name: agentName,
        version_number: newVersionNumber,
        instruction_text: draftInstruction,
        created_by: 'self',
        test_result: { all_passed: true, env: 'staging', levels: 10 },
        is_active: true
      });
      await trx.insertAutonomyLog({
        agent_name: agentName,
        action_type: 'SELF_TUNE_INSTRUCTION_PROMOTED',
        environment: 'production',
        status: 'passed_all_levels',
        attempt_count: attempt
      });
    });
    logger.info(`Agent ${agentName} successfully self-promoted to instruction version v${newVersionNumber}`);
  } else {
    // رول‌بک کامل به نسخه فعال فعلی و بررسی آستانه فیوز محافظ
    settings.failures_last_24h += 1;
    let status = 'rolled_back_at_level';
    if (settings.failures_last_24h >= settings.circuit_breaker_threshold) {
      settings.circuit_broken = true;
      settings.autonomy_enabled = false;
      status = 'circuit_breaker_tripped';
    } else {
      status = 'pending_human_review';
    }
    await db.updateAutonomySettings(agentName, settings);
    await db.insertAutonomyLog({
      agent_name: agentName,
      action_type: 'SELF_TUNE_INSTRUCTION_REJECTED',
      environment: 'staging',
      status,
      failed_level: lastFailedLevel,
      attempt_count: attempt
    });
  }
}
```

---

## ۳. بازتعریف زنجیره ۱۰ لولی تست تشدیدی برای هر ۴ ایجنت

| شماره لول | UIUXAgent (Part 06) | TranslatorAgent (Part 07) | ImageVideoAgent (Part 08) | SupportAgent (Part 07) |
| :---: | :--- | :--- | :--- | :--- |
| **Level 1** | بررسی عدم دستکاری دارایی‌های محافظت‌شده برند | بررسی عدم دسترسی به جداول مالی/محرمانه | بررسی فرمت خروجی (PNG/WebP/MP4) | بررسی عدم دسترسی نوشتن به دیتابیس (صرفاً Read-Only) |
| **Level 2** | حفظ قطعی خط افشای ریسک قانونی | حفظ بدون تغییر متن افشای ریسک به زبان مقصد | رعایت ابعاد دقیق (16:9, 9:16, 1:1, ...) | بررسی نبود اطلاعات گمراه‌کننده درباره کاربر |
| **Level 3** | پایبندی به گرید 8dp و فاصله‌گذاری | بررسی ساختار، علائم نگارشی و طول رشته | سقف حجم فایل (عکس زیر ۲MB، ویدیو زیر ۲۵MB) | افشای صریح هویت AI در صورت پرسش مستقیم |
| **Level 4** | تطابق کنتراست رنگ‌ها با استاندارد WCAG AAA | تطابق اصطلاحات با گلاسوری مالی تاییدشده | درج لوگوی برند و متن مرجع منبع خبر | نبود هرگونه وعده سود تضمینی یا بازگشت پول |
| **Level 5** | بررسی استقلال المان‌ها بدون تغییر پکیج | ممانعت از جا ماندن Placeholderهای انگلیسی | بررسی شباهت با Style Reference Set | الصاق پیوسته هشدار ریسک معاملات باینری |
| **Level 6** | سنجش عملکرد و شاخص‌های Core Web Vitals | بررسی نحوی متون راست‌به‌چپ و چپ‌به‌راست | بررسی نمره Drift سبکی (زیر آستانه ۰.۲۵) | سنجش تاخیر پاسخگویی (زیر ۵۰۰ میلی‌ثانیه) |
| **Level 7** | رندر در تمام اندازه‌های ویوپورت و موبایل | تست رندر در ساختار UI بدون شکستن Layout | تایید مدت زمان ویدیو (بین ۱۵ تا ۶۰ ثانیه) | بررسی سلامت پاسخ در قالب Markdown مناسب |
| **Level 8** | انطباق با تست‌های رگرسیون تصویری Roborazzi | تست همخوانی با متغیرهای دینامیک کلاینت | بررسی عدم نقض علائم تجاری ثالث (تریدماک) | آزمون رگرسیون بر روی ۱۰۰ سوال پرتکرار پشتیبانی |
| **Level 9** | اجرای بدون خطای تغییر در محیط Staging | ارزیابی ترجمه در محیط ایزوله Shadow | رندر کامل تیزر و موشن در محیط تستی موقت | شبیه‌سازی مکالمه در کانتینر ایزوله تست |
| **Level 10** | دریافت مجوز تایید نهایی کیفیت تولید | سنجش پایداری لحن نسبت به متون پیشین | ممیزی نهایی عدم ادعای تبلیغاتی گمراه‌کننده | مجوز نهایی استقلال با تضمین رعایت قوانین |

---

## ۴. محدودیت‌های سخت و ابدی (Immutable Guardrails)

این قوانین در لایه‌ای خارج از دسترس و ویرایش ایجنت‌ها قرار دارند:
1. **خط افشای ریسک قانونی:** هیچ متنی بدون خط افشای ریسک («این سیگنال‌ها توصیه مالی نیستند...») تایید نمی‌شود.
2. **عدم دسترسی نوشتن به جداول حساس:** جداول `orders`، `subscriptions`، `payment_receipts`، `secrets` و `admin_users` تحت هیچ شرایطی برای ایجنت‌ها قابل ویرایش نیستند.
3. **عدم دسترسی به مقادیر خام Secret:** ایجنت‌ها صرفاً به نام متغیر یا `secret_ref` دسترسی دارند.
4. **فیوز محافظ خودکار (Circuit Breaker):** وقوع ۵ شکست متوالی در ۲۴ ساعت، استقلال ایجنت را متوقف کرده و به تایید انسانی بازمی‌گرداند.
