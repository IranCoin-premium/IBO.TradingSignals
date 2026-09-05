# IBO Binary Option Trading Signals Platform

> **افشای ریسک قانونی (الزامی و دائمی):**  
> «این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.»

سیستم جامع اشتراک و ارائه سیگنال‌های معاملاتی باینری آپشن بر پایه تایید هم‌زمان تحلیل‌گر انسانی و مدل‌های هوش مصنوعی.

---

## ساختار ماژول‌ها و اسناد معماری

- **معماری جریان سفارش و سیگنال:** [PURCHASE_AND_SIGNAL_FLOW.md](./PURCHASE_AND_SIGNAL_FLOW.md)
- **طراحی پایگاه داده 3NF و ERD:** [DATABASE_SCHEMA.md](./DATABASE_SCHEMA.md)
- **یکپارچه‌سازی درگاه‌های پرداخت:** [PAYMENT_GATEWAYS.md](./PAYMENT_GATEWAYS.md) و [PAYMENT_GATEWAYS_MAPPING.md](./PAYMENT_GATEWAYS_MAPPING.md)
- **استراتژی اتوماسیون و n8n بدون VPS:** [N8N_AND_AUTOMATION_STRATEGY.md](./N8N_AND_AUTOMATION_STRATEGY.md)
- **امنیت و کنترل دسترسی:** [SECURITY.md](./SECURITY.md)

---

## استراتژی اتوماسیون و اجرای n8n (Part 05)

### جمع‌بندی راهکار استقرار بدون VPS:
> **«n8n دائمی نیاز به یک سرویس Always-On دارد (حتی رایگان‌ترین PaaS)؛ GitHub Actions/Codespace جایگزین کامل آن نیست اما برای بخش قابل‌توجهی از اتوماسیون‌های زمان‌بندی‌شده (نه Webhook زنده) می‌تواند رایگان و بدون n8n جایگزین شود.»**

### تفکیک وظایف:
1. **وب‌هووک‌های حساس پرداخت (NowPayments, استورها، اقساط):** مستقیماً و بدون واسطه روی مونوست بک‌اند اصلی پردازش می‌شوند.
2. **اتوماسیون زمان‌بندی‌شده دوره‌ای (GitHub Actions):**
   - ممیزی انطباق UI/UX و ریسک (هر ۶ ساعت): `.github/workflows/ui-ux-check.yml`
   - تولید رسانه و تصویر با لوگو و واترمارک (روزانه): `.github/workflows/image-generation.yml`
   - واکشی فیدهای اقتصادی و RSS (ساعتی): `.github/workflows/rss-news-fetcher.yml`
3. **ارکستریشن رویدادی سبک (n8n روی PaaS):**
   - دیپلوی روی سرویس‌های رایگان/ارزان PaaS نظیر Render.com یا Railway.app از طریق فایل کانتینر اختصاصی `docker-compose.n8n.yml`.
