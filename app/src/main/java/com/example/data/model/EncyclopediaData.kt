package com.example.data.model

data class EncyclopediaItem(
    val id: String,
    val sectionNumber: Int,
    val sectionType: String, // "SECTION_6", "SECTION_36", "SECTION_67"
    val sectionTitleBadge: String,
    val title: String,
    val titleEn: String = "",
    val category: String,
    val direction: String = "NEUTRAL", // "CALL", "PUT", "NEUTRAL", "BOTH"
    val winRate: String = "",
    val summary: String,
    val fullContent: String,
    val practicalTip: String = "",
    val tags: List<String>
)

object EncyclopediaRepository {

    // ==========================================
    // بخش اول: ۶ قانون طلایی و ستون‌های مدیریت ریسک (SECTION 6)
    // ==========================================
    val sectionSixItems: List<EncyclopediaItem> = listOf(
        EncyclopediaItem(
            id = "s6_1",
            sectionNumber = 1,
            sectionType = "SECTION_6",
            sectionTitleBadge = "قانون ۱ از ۶ • ستون اصلی بقا",
            title = "مدیریت سرمایه با ریسک ثابت ۱ تا ۲ درصد (Fixed Fractional Risk)",
            titleEn = "Fixed 1-2% Fractional Risk Management",
            category = "مدیریت ریسک",
            direction = "NEUTRAL",
            winRate = "۹۹٪ بقای حساب",
            summary = "در هر پوزیشن معاملاتی باینری آپشن، حداکثر ۱ تا ۲ درصد از کل بالانس موجود حساب را به خطر بیندازید.",
            fullContent = "بزرگ‌ترین دلیل کال‌مارجین شدن معامله‌گران در باینری آپشن، ورود با مبالغ سنگین نسبت به موجودی حساب است. در سیستم ریسک کسری ثابت (Fixed Fractional Method)، حتی اگر ۱۰ معامله زیان‌ده متوالی رخ دهد، بیش از ۸۰٪ کل سرمایه دست‌نخورده باقی می‌ماند و فرصت جبران با وین‌ریت منطقی وجود دارد. هرگز اندازه پوزیشن را بر اساس هیجان یا اطمینان کاذب تغییر ندهید.",
            practicalTip = "فرمول محاسبه: مبلغ ترید = (موجودی کل حساب × ۰.۰۱ تا ۰.۰۲). اگر بالانس شما $500 است، مبلغ هر ترید باید دقیقاً $5 تا $10 باشد.",
            tags = listOf("مدیریت سرمایه", "ریسک ثابت", "حفظ بالانس", "Position Sizing")
        ),
        EncyclopediaItem(
            id = "s6_2",
            sectionNumber = 2,
            sectionType = "SECTION_6",
            sectionTitleBadge = "قانون ۲ از ۶ • ممنوعیت قطعی",
            title = "حذف کامل و ممنوعیت سیستم تخریبی مارتینگل (No Martingale Rule)",
            titleEn = "Absolute Anti-Martingale Discipline",
            category = "مدیریت ریسک",
            direction = "NEUTRAL",
            winRate = "پیشگیری از نابودی ۱۰۰٪",
            summary = "دو برابر کردن حجم پس از هر معامله زیان‌ده (مارتینگل) یک تله آماری است که کل حساب را در ۵ گام به صفر می‌رساند.",
            fullContent = "مارتینگل با این فرض اشتباه که بالاخره یک معامله برنده خواهد شد، حجم ترید را به صورت تصاعد هندسی (1, 2, 4, 8, 16, 32, 64) بالا می‌برد. در بازار باینری آپشن با بازدهی ۸۵ تا ۹۲ درصد، یک دنباله ۶ باخت متوالی بیش از ۶۳ برابر حجم اولیه را می‌بلعد و حساب را منحل می‌کند. پلتفرم ایران باینری آپشن بر استفاده از حجم ثابت (Flat Stake) یا ترکیب هوشمند Compounding در روندهای سودده تاکید دارد.",
            practicalTip = "به جای مارتینگل، از سقف ضرر روزانه (Daily Stop-Loss) استفاده کنید: پس از ۳ باخت متوالی در روز، ترید را فوراً متوقف نمایید.",
            tags = listOf("ضد مارتینگل", "تله آماری", "انضباط معاملاتی", "Stop Loss")
        ),
        EncyclopediaItem(
            id = "s6_3",
            sectionNumber = 3,
            sectionType = "SECTION_6",
            sectionTitleBadge = "قانون ۳ از ۶ • صبر و گزینشگری",
            title = "فیلتر هوشمند عدم معامله (NO_TRADE Filter) در نوسانات پرریسک",
            titleEn = "Selective Execution & No-Trade Veto",
            category = "هوش مصنوعی و وتو",
            direction = "NEUTRAL",
            winRate = "افزایش وین‌ریت خالص",
            summary = "معامله نکردن در شرایط بد بازار (اسپرد بالا، اخبار سنگین و فاز فشردگی)، نوعی کسب سود قطعی و حفظ دارایی است.",
            fullContent = "بسیاری از معامله‌گران گمان می‌کنند باید در هر دقیقه پای چارت معامله کنند. فیلتر هوش مصنوعی AI3 Governor پلتفرم ما در زمان انتشار اخبار رتبه بالا (قرمز رنگ)، سخنرانی‌های روسای بانک‌های مرکزی و ساعات بازگشایی سشن‌های کم‌عمق، دستور وتوی قطعی صادر می‌کند. حفظ دارایی در زمان طوفان بازار، شرط اول موفقیت تریدرهای حرفه‌ای است.",
            practicalTip = "۱۵ دقیقه قبل و بعد از اخبار High Impact (مانند CPI یا NFP) و در زمان فشردگی شدید باندهای بولینگر، ترید باز نکنید.",
            tags = listOf("عدم معامله", "فیلتر وتو", "اخبار اقتصادی", "AI Governor")
        ),
        EncyclopediaItem(
            id = "s6_4",
            sectionNumber = 4,
            sectionType = "SECTION_6",
            sectionTitleBadge = "قانون ۴ از ۶ • ریاضیات سودآوری",
            title = "معامله انحصاری روی دارایی‌های با بازدهی بالای ۸۵٪ (High Payout Selection)",
            titleEn = "Minimum 85% Payout Mathematical Rule",
            category = "ریاضیات بازدهی",
            direction = "NEUTRAL",
            winRate = "کاهش Break-even",
            summary = "از باز کردن پوزیشن روی جفت‌ارزها و دارایی‌هایی با Payout زیر ۸۵ درصد در بروکرها خودداری نمایید.",
            fullContent = "ریاضیات باینری آپشن حکم می‌کند که هرچه نرخ بازدهی بروکر (Payout) کمتر باشد، حداقل نرخ برد لازم (Break-even Win Rate) برای سربه‌سر شدن بالاتر می‌رود. در بازدهی ۷۰٪، شما به نرخ برد ۵۸.۸٪ نیاز دارید؛ اما در بازدهی ۹۲٪، نرخ برد ۵۲.۰۸٪ کافی است. انتخاب دارایی‌های پربازده در ساعات شلوغی بازار، حاشیه سود معامله‌گر را به شدت افزایش می‌دهد.",
            practicalTip = "همواره در بروکرهایی نظیر Quotex، Pocket Option و Deriv دارایی‌های با نرخ ۸۸٪ تا ۹۶٪ را اولویت‌بندی کنید.",
            tags = listOf("نرخ بازدهی", "Payout", "Break-Even", "حاشیه سود")
        ),
        EncyclopediaItem(
            id = "s6_5",
            sectionNumber = 5,
            sectionType = "SECTION_6",
            sectionTitleBadge = "قانون ۵ از ۶ • مهار ذهن",
            title = "کنترل روانشناسی، مهار طمع و مقابله با ترید انتقامی (Revenge Trading Shield)",
            titleEn = "Psychological Control & Revenge Trading Prevention",
            category = "روانشناسی ترید",
            direction = "NEUTRAL",
            winRate = "ثبات روانی پایدار",
            summary = "ترید انتقامی پس از باخت و ترید با طمع پس از بردهای متوالی، عامل نابودی ۹۰٪ تریدرهای باینری است.",
            fullContent = "احساسات هیجانی مغز (آمیگدال) پس از باخت، تمایل به بازپس‌گیری فوری پول از بازار را ایجاد می‌کند. در این حالت، تریدر بدون بررسی ستاپ و استراتژی وارد پوزیشن‌های تصادفی می‌شود. قانون طلایی این است: برای هر روز یک تارگت سود (مثلاً +۵٪) و یک حد ضرر روزانه (مثلاً -۳٪) تعیین کنید و به محض رسیدن به هر کدام، پلتفرم را ببندید.",
            practicalTip = "اگر بعد از یک معامله زیان‌ده احساس خشم یا تپش قلب داشتید، حداقل ۲ ساعت سیستم را ترک کرده و به محیط باز بروید.",
            tags = listOf("روانشناسی", "ترید انتقامی", "کنترل هیجان", "FOMO Shield")
        ),
        EncyclopediaItem(
            id = "s6_6",
            sectionNumber = 6,
            sectionType = "SECTION_6",
            sectionTitleBadge = "قانون ۶ از ۶ • ارزیابی مستمر",
            title = "ممیزی مستمر و ثبت تمام معاملات در ژورنال شخصی (Trade Journaling)",
            titleEn = "Rigorous Trade Logging & Performance Audit",
            category = "تحلیل عملکرد",
            direction = "NEUTRAL",
            winRate = "رشد مستمر وین‌ریت",
            summary = "بدون ثبت و بررسی گذشته معاملات، تکرار اشتباهات اجتناب‌ناپذیر است. تمام تریدها را با اسکرین‌شات و احساسات ثبت کنید.",
            fullContent = "تفاوت معامله‌گر قمارباز و تریدر حرفه‌ای در ثبت ژورنال است. با ثبت زمان ورود، جهت CALL/PUT، اندیکاتورهای فعال، پی‌اوت بروکر و وضعیت ذهنی در ژورنال پلتفرم، می‌توانید پس از ۳۰ معامله بررسی کنید که کدام جفت‌ارزها و کدام تایم‌فریم‌ها بیشترین سود را برای سبک معاملاتی شما ساخته‌اند.",
            practicalTip = "از بخش 'ژورنال ترید' اپلیکیشن برای ثبت روزانه معاملات و الصاق تصویر چارت قبل و بعد از اکسپایری استفاده کنید.",
            tags = listOf("ژورنال ترید", "ممیزی معاملات", "Trade Log", "رشد مهارت")
        )
    )

    // ==========================================
    // بخش دوم: ۳۶ الگوی کندل‌استیک و پرایس‌اکشن (SECTION 36)
    // ==========================================
    val sectionThirtySixItems: List<EncyclopediaItem> = listOf(
        EncyclopediaItem(
            id = "s36_1", sectionNumber = 1, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۱ از ۳۶ • تک کندلی بازگشتی",
            title = "کندل پین‌بار صعودی (Bullish Pin Bar)",
            titleEn = "Bullish Pin Bar Rejection",
            category = "الگوهای بازگشتی صعودی", direction = "CALL", winRate = "۸۸٪",
            summary = "سایه پایینی بسیار بلند (حداقل دو برابر بدنه) و بدنه کوچک در بالا که نشان‌دهنده ریجکشن قدرتمند قیمت از سطح حمایتی است.",
            fullContent = "پین‌بار صعودی نشان می‌دهد که فروشندگان تلاش کردند قیمت را پایین ببرند، اما خریداران با قدرت وارد شده و قیمت را به نزدیکی سقف کندل بازگرداندند. این الگو در تایم‌فریم ۱ تا ۵ دقیقه روی حمایت ماژور یک ستاپ عالی برای معامله CALL با انقضای ۱ تا ۳ کندل بعدی است.",
            practicalTip = "ورود در ثانیه ۰۰ کندل بعدی پس از بسته شدن پین‌بار در کف کانال یا روی خط روند حمایتی.",
            tags = listOf("Pin Bar", "ریجکشن کف", "سیگنال CALL", "حمایت")
        ),
        EncyclopediaItem(
            id = "s36_2", sectionNumber = 2, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۲ از ۳۶ • تک کندلی بازگشتی",
            title = "کندل پین‌بار نزولی (Bearish Pin Bar)",
            titleEn = "Bearish Pin Bar Rejection",
            category = "الگوهای بازگشتی نزولی", direction = "PUT", winRate = "۸۷٪",
            summary = "سایه بالایی بلند (حداقل دو برابر بدنه) و بدنه کوچک در کف که نشان‌دهنده پس زده شدن شدید خریداران توسط خرس‌هاست.",
            fullContent = "کندل پین‌بار نزولی در انتهای یک موج صعودی و روی مقاومت‌های استاتیک یا باند بالایی بولینگر رخ می‌دهد. خریداران سعی در سقف‌سازی جدید داشتند اما با مقاومت سنگین عرضه مواجه شدند. گزینه بسیار مناسب برای قرارداد PUT.",
            practicalTip = "در تایم‌فریم ۱ دقیقه، در صورت برخورد نوک سایه به مقاومت روزانه، ورود با اکسپایری ۱ تا ۲ دقیقه.",
            tags = listOf("Pin Bar", "مقاومت سقف", "سیگنال PUT", "عرضه")
        ),
        EncyclopediaItem(
            id = "s36_3", sectionNumber = 3, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۳ از ۳۶ • تک کندلی صعودی",
            title = "کندل چکش (Hammer)",
            titleEn = "Hammer Candlestick",
            category = "الگوهای بازگشتی صعودی", direction = "CALL", winRate = "۸۶٪",
            summary = "بدنه کوچک در بالای دامنه قیمتی با سایه پایینی طویل و بدون سایه بالایی در انتهای روند نزولی.",
            fullContent = "چکش نشان‌دهنده تسلیم فروشندگان در انتهای روند نزولی است. وقتی در کف حمایت ظاهر می‌شود، نشانه تغییر فاز بازار از فروش به خرید است. رنگ بدنه اگر سبز باشد اعتبار بالاتری دارد.",
            practicalTip = "تایید با واگرایی مثبت RSI روی سطح ۳۰ اعتبار چکش را به بالای ۹۰٪ می‌رساند.",
            tags = listOf("Hammer", "چکش", "کف قیمتی", "CALL")
        ),
        EncyclopediaItem(
            id = "s36_4", sectionNumber = 4, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۴ از ۳۶ • تک کندلی صعودی",
            title = "چکش معکوس (Inverted Hammer)",
            titleEn = "Inverted Hammer Pattern",
            category = "الگوهای بازگشتی صعودی", direction = "CALL", winRate = "۸۲٪",
            summary = "بدنه کوچک در پایین و سایه بالایی بلند در انتهای یک روند نزولی که نیاز به تایید کندل بعدی دارد.",
            fullContent = "چکش معکوس پس از یک نزول مداوم شکل می‌گیرد و نشان می‌دهد گاوها برای اولین بار فشار خرید معناداری وارد کرده‌اند. برای ورود به باینری آپشن، صبر کنید تا کندل بعدی سبز رنگ بسته شود.",
            practicalTip = "ورود برای ترید CALL پس از تایید شکست سقف چکش معکوس با انقضای ۳ دقیقه.",
            tags = listOf("Inverted Hammer", "چکش برعکس", "تایید کندلی")
        ),
        EncyclopediaItem(
            id = "s36_5", sectionNumber = 5, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۵ از ۳۶ • تک کندلی نزولی",
            title = "مرد به دار آویخته (Hanging Man)",
            titleEn = "Hanging Man Pattern",
            category = "الگوهای بازگشتی نزولی", direction = "PUT", winRate = "۸۴٪",
            summary = "ظاهری شبیه به چکش اما در سقف روند صعودی؛ هشدار ورود ناگهانی فروشندگان به بازار.",
            fullContent = "تشکیل سایه پایینی بلند در سقف روند صعودی نشان می‌دهد که در طول تایم‌فریم، خرس‌ها توانسته‌اند برای لحظاتی قیمت را شدیداً پایین بکشند. اگر کندل بعدی قرمز بسته شود، تایید قطعی ریزش است.",
            practicalTip = "سیگنال PUT با اکسپایری ۲ الی ۵ دقیقه در پول‌بک به کف بدنه مرد به دار آویخته.",
            tags = listOf("Hanging Man", "هشدار سقف", "سیگنال PUT")
        ),
        EncyclopediaItem(
            id = "s36_6", sectionNumber = 6, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۶ از ۳۶ • تک کندلی نزولی",
            title = "شهاب سنگ یا شوتینگ استار (Shooting Star)",
            titleEn = "Shooting Star Reversal",
            category = "الگوهای بازگشتی نزولی", direction = "PUT", winRate = "۸۹٪",
            summary = "بدنه کوچک در کف کندل با سایه بالایی بسیار بلند پس از یک موج صعودی قدرتمند.",
            fullContent = "یکی از معتبرترین الگوهای تک‌کندلی در باینری آپشن برای ورود به معامله PUT. نشان‌دهنده تخلیه سنگین پوزیشن‌های خرید و پیروزی قاطع خرس‌ها در سقف است.",
            practicalTip = "ورود فوری در ثانیه ۰۰ کندل بعدی به سمت PUT مخصوصاً در جفت‌ارزهای OTC.",
            tags = listOf("Shooting Star", "شوتینگ استار", "ریزش قیمت", "PUT")
        ),
        EncyclopediaItem(
            id = "s36_7", sectionNumber = 7, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۷ از ۳۶ • دو کندلی صعودی",
            title = "انگولفینگ صعودی یا پوشاننده مثبت (Bullish Engulfing)",
            titleEn = "Bullish Engulfing Pattern",
            category = "الگوهای بازگشتی صعودی", direction = "CALL", winRate = "۹۱٪",
            summary = "کندل سبز بزرگ که تمام بدنه کندل قرمز قبلی خود را به طور کامل در بر می‌گیرد و می‌پوشاند.",
            fullContent = "این الگو تغییر ناگهانی قدرت از فروشندگان به خریداران را نشان می‌دهد. هرچه بدنه کندل سبز بزرگ‌تر باشد و حجم بالاتری داشته باشد، قدرت جهش صعودی بیشتر است.",
            practicalTip = "ورود در ابتدای کندل سوم برای ترید CALL با تایم‌فریم ۱ یا ۵ دقیقه.",
            tags = listOf("Engulfing", "پوشاننده صعودی", "وین ریت بالا", "CALL")
        ),
        EncyclopediaItem(
            id = "s36_8", sectionNumber = 8, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۸ از ۳۶ • دو کندلی نزولی",
            title = "انگولفینگ نزولی یا پوشاننده منفی (Bearish Engulfing)",
            titleEn = "Bearish Engulfing Pattern",
            category = "الگوهای بازگشتی نزولی", direction = "PUT", winRate = "۹۰٪",
            summary = "کندل قرمز پرقدرت که بدنه کندل سبز قبلی را کاملاً پوشش می‌دهد و نشانه ورود نقدینگی خرس‌هاست.",
            fullContent = "در انتهای روند صعودی و برخورد به سطوح کلیدی مقاومت، شکل‌گیری این الگو نشان‌دهنده سقوط قریب‌الوقوع است و از بهترین ستاپ‌های معامله PUT در پوکت آپشن و کوتکس به شمار می‌رود.",
            practicalTip = "معامله PUT با انقضای ۱ تا ۳ دقیقه پس از بسته شدن بدنه کندل قرمز زیر کف کندل قبلی.",
            tags = listOf("Engulfing", "پوشاننده نزولی", "سیگنال PUT")
        ),
        EncyclopediaItem(
            id = "s36_9", sectionNumber = 9, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۹ از ۳۶ • سه کندلی صعودی",
            title = "ستاره صبحگاهی (Morning Star)",
            titleEn = "Morning Star 3-Candle Formation",
            category = "الگوهای بازگشتی صعودی", direction = "CALL", winRate = "۹۲٪",
            summary = "ترکیب سه کندل: کندل اول نزولی بزرگ، کندل دوم کوچک (دوجی یا اسپینینگ) و کندل سوم صعودی پرقدرت.",
            fullContent = "ستاره صبحگاهی یک الگوی بازگشتی کلاسیک با بالاترین اعتبار آماری است. کندل میانی توقف روند نزولی و کندل سوم ورود نقدینگی خریداران را تایید می‌کند.",
            practicalTip = "ورود CALL با انقضای ۵ دقیقه در تایم‌فریم ۱ یا ۵ دقیقه.",
            tags = listOf("Morning Star", "ستاره صبحگاهی", "سه کندلی", "CALL")
        ),
        EncyclopediaItem(
            id = "s36_10", sectionNumber = 10, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۱۰ از ۳۶ • سه کندلی نزولی",
            title = "ستاره شامگاهی (Evening Star)",
            titleEn = "Evening Star 3-Candle Formation",
            category = "الگوهای بازگشتی نزولی", direction = "PUT", winRate = "۹۱٪",
            summary = "ترکیب سه کندل در سقف: کندل اول سبز بزرگ، کندل دوم کوچک بلاتکلیف و کندل سوم قرمز بزرگ و نفوذی.",
            fullContent = "نماد پایان خوش‌بینی گاوها و آغاز ریزش سنگین. کندل سوم باید حداقل تا ۵۰٪ بدنه کندل اول به سمت پایین نفوذ کند.",
            practicalTip = "ستاپ ترید PUT ایده‌آل روی طلا (XAU/USD) و رمزارزها.",
            tags = listOf("Evening Star", "ستاره شامگاهی", "نزولی", "PUT")
        ),
        EncyclopediaItem(
            id = "s36_11", sectionNumber = 11, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۱۱ از ۳۶ • بلاتکلیفی",
            title = "دوجی استاندارد (Standard Doji)",
            titleEn = "Standard Neutral Doji",
            category = "الگوهای بلاتکلیفی", direction = "NEUTRAL", winRate = "۸۰٪ (در سطوح کلیدی)",
            summary = "قیمت باز و بسته شدن دقیقاً برابر است و کندل فاقد بدنه واقعی بوده و شبیه به علامت مثبت (+) است.",
            fullContent = "دوجی نشان‌دهنده برابری کامل خریداران و فروشندگان است. اگر در میانه روند ظاهر شود نشانه استراحت و اگر در مقاومت یا حمایت ماژور پدیدار شود، زنگ خطر بازگشت روند است.",
            practicalTip = "به خودی خود وارد نشوید؛ منتظر شکست سقف یا کف کندل دوجی توسط کندل بعدی بمانید.",
            tags = listOf("Doji", "دوجی", "بلاتکلیفی", "تعادل")
        ),
        EncyclopediaItem(
            id = "s36_12", sectionNumber = 12, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۱۲ از ۳۶ • دوجی صعودی",
            title = "دوجی سنجاقک (Dragonfly Doji)",
            titleEn = "Dragonfly Bullish Doji",
            category = "الگوهای بازگشتی صعودی", direction = "CALL", winRate = "۸۸٪",
            summary = "قیمت باز، بسته و سقف در بالاترین نقطه کندل قرار دارد و یک سایه پایینی بلند کشیده شده است (شبیه حرف T).",
            fullContent = "نشان می‌دهد فروشندگان در طول دوره کندل را به پایین کشیدند اما در نهایت تمام افت جبران شد و قیمت در سقف بسته شد. روی حمایت‌های تایم ۱ دقیقه سیگنال صعودی پرقدرتی است.",
            practicalTip = "ترید CALL در کندل بلافاصله بعد از سنجاقک.",
            tags = listOf("Dragonfly Doji", "دوجی سنجاقک", "CALL")
        ),
        EncyclopediaItem(
            id = "s36_13", sectionNumber = 13, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۱۳ از ۳۶ • دوجی نزولی",
            title = "دوجی سنگ قبر (Gravestone Doji)",
            titleEn = "Gravestone Bearish Doji",
            category = "الگوهای بازگشتی نزولی", direction = "PUT", winRate = "۸۷٪",
            summary = "قیمت باز، بسته و کف در پایین‌ترین نقطه کندل بوده و دارای سایه بالایی بسیار بلند است (شبیه حرف T وارونه).",
            fullContent = "نشان می‌دهد خریداران در ابتدای کندل تلاش کردند سقف را بشکنند اما شکست خوردند و قیمت در کف بازگشت. زنگ خطر ریزش در سقف‌های تاریخی.",
            practicalTip = "ترید PUT با اکسپایری ۱ الی ۳ دقیقه.",
            tags = listOf("Gravestone Doji", "دوجی سنگ قبر", "PUT")
        ),
        EncyclopediaItem(
            id = "s36_14", sectionNumber = 14, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۱۴ از ۳۶ • دوجی نوسانی",
            title = "دوجی پایه بلند (Long-Legged Doji)",
            titleEn = "Long-Legged Volatility Doji",
            category = "الگوهای بلاتکلیفی", direction = "BOTH", winRate = "۸۳٪",
            summary = "دارای سایه‌های بالایی و پایینی بسیار بلند و متقارن با بدنه خطی در مرکز کندل.",
            fullContent = "نشان‌دهنده جنگ سنگین نقدینگی و نوسان شدید در بازار است. معمولاً قبل از تصمیم‌گیری بزرگ قیمت یا در دقایق انتشار اخبار رخ می‌دهد.",
            practicalTip = "استراتژی شکست باند: ورود در جهت شکست سقف یا کف این کندل.",
            tags = listOf("Long Legged", "نوسان شدید", "شکست سطح")
        ),
        EncyclopediaItem(
            id = "s36_15", sectionNumber = 15, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۱۵ از ۳۶ • تک کندلی مومنتوم",
            title = "ماروبوزو صعودی (Bullish Marubozu)",
            titleEn = "Full Body Bullish Marubozu",
            category = "الگوهای ادامه‌دهنده", direction = "CALL", winRate = "۸۹٪",
            summary = "کندل سبز بزرگ و پرحجم بدون هیچ‌گونه سایه بالایی و پایینی؛ باز شدن در کف و بسته شدن دقیقاً در سقف.",
            fullContent = "نشان‌دهنده کنترل ۱۰۰ درصدی خریداران از ابتدا تا انتهای تایم‌فریم کندل است. این الگو نشانه مومنتوم انفجاری صعودی است.",
            practicalTip = "ورود CALL با تایم‌فریم ۱ تا ۵ دقیقه برای تداوم روند.",
            tags = listOf("Marubozu", "ماروبوزو", "مومنتوم صعودی", "CALL")
        ),
        EncyclopediaItem(
            id = "s36_16", sectionNumber = 16, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۱۶ از ۳۶ • تک کندلی مومنتوم",
            title = "ماروبوزو نزولی (Bearish Marubozu)",
            titleEn = "Full Body Bearish Marubozu",
            category = "الگوهای ادامه‌دهنده", direction = "PUT", winRate = "۸۹٪",
            summary = "کندل قرمز یکدست و بزرگ بدون هیچ سایه‌ای؛ باز شدن در بالاترین قیمت و بسته شدن در پایین‌ترین نقطه.",
            fullContent = "نشان‌دهنده هجوم بی امان فروشندگان و ریزش بدون مقاومت قیمت است. برای معاملات رونددار کوتاه‌مدت عالی است.",
            practicalTip = "ورود PUT برای کندل بعدی به همراه تاییدیه افزایش حجم.",
            tags = listOf("Marubozu", "ماروبوزو نزولی", "مومنتوم نزولی", "PUT")
        ),
        EncyclopediaItem(
            id = "s36_17", sectionNumber = 17, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۱۷ از ۳۶ • دو کندلی مادر و کودک",
            title = "هارامی صعودی یا زن باردار مثبت (Bullish Harami)",
            titleEn = "Bullish Harami Inside Bar",
            category = "الگوهای بازگشتی صعودی", direction = "CALL", winRate = "۸۳٪",
            summary = "کندل قرمز بزرگ (مادر) که به دنبال آن یک کندل سبز کوچک (کودک) به طور کامل در داخل بدنه کندل اول جای می‌گیرد.",
            fullContent = "هارامی صعودی نشان‌دهنده ترمز گرفتن روند نزولی و کاهش شدید شتاب فروشندگان است. اگر کندل سوم سقف کندل کوچک را به بالا بشکند، سیگنال صعود تایید می‌شود.",
            practicalTip = "ورود CALL پس از تثبیت کندل سوم بالای هارامی.",
            tags = listOf("Harami", "هارامی صعودی", "Inside Bar", "CALL")
        ),
        EncyclopediaItem(
            id = "s36_18", sectionNumber = 18, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۱۸ از ۳۶ • دو کندلی مادر و کودک",
            title = "هارامی نزولی (Bearish Harami)",
            titleEn = "Bearish Harami Inside Bar",
            category = "الگوهای بازگشتی نزولی", direction = "PUT", winRate = "۸۴٪",
            summary = "کندل سبز بلند اول و کندل قرمز کوچک دوم که کاملاً در محدوده بدنه کندل سبز قرار دارد.",
            fullContent = "نشان‌دهنده افت قدرت خریداران در سقف و احتمال شروع یک ریزش اصلاحی در بازار باینری آپشن.",
            practicalTip = "ورود PUT با شکست کف کندل کوچک.",
            tags = listOf("Harami", "هارامی نزولی", "Inside Bar", "PUT")
        ),
        EncyclopediaItem(
            id = "s36_19", sectionNumber = 19, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۱۹ از ۳۶ • دو کندلی نفوذی",
            title = "خط نافذ (Piercing Line)",
            titleEn = "Piercing Line Bullish Reversal",
            category = "الگوهای بازگشتی صعودی", direction = "CALL", winRate = "۸۶٪",
            summary = "کندل اول نزولی و کندل دوم صعودی که با گپ باز شده و به بالای ۵۰٪ بدنه کندل اول نفوذ می‌کند.",
            fullContent = "این الگو در کف‌های قیمتی تشکیل می‌شود و نشان می‌دهد قدرت خرید آنقدر زیاد بوده که بیش از نیمی از افت قبلی را در یک گام پس گرفته است.",
            practicalTip = "ورود CALL در ثانیه ۰۰ کندل بعدی.",
            tags = listOf("Piercing Line", "خط نافذ", "نفوذ ۵۰ درصد", "CALL")
        ),
        EncyclopediaItem(
            id = "s36_20", sectionNumber = 20, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۲۰ از ۳۶ • دو کندلی پوشاننده",
            title = "ابر سیاه پوشاننده (Dark Cloud Cover)",
            titleEn = "Dark Cloud Cover Reversal",
            category = "الگوهای بازگشتی نزولی", direction = "PUT", winRate = "۸۶٪",
            summary = "کندل اول سبز بزرگ و کندل دوم قرمز که بالاتر باز شده اما به عمق بیش از ۵۰٪ بدنه سبز نفوذ می‌کند.",
            fullContent = "معادل خرسی الگوی خط نافذ است و در سقف مقاومت‌ها سیگنال بازگشت نزولی قوی می‌دهد.",
            practicalTip = "ترید PUT با اکسپایری ۲ الی ۳ دقیقه.",
            tags = listOf("Dark Cloud", "ابر سیاه", "نزولی", "PUT")
        ),
        EncyclopediaItem(
            id = "s36_21", sectionNumber = 21, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۲۱ از ۳۶ • سه کندلی متوالی",
            title = "سه سرباز سفید (Three White Soldiers)",
            titleEn = "Three White Soldiers Momentum",
            category = "الگوهای ادامه‌دهنده صعودی", direction = "CALL", winRate = "۹۳٪",
            summary = "سه کندل متوالی سبز با بدنه‌های بزرگ، سایه‌های کوتاه و سقف‌های بالاتر از یکدیگر.",
            fullContent = "یکی از مقتدرترین الگوهای صعودی در کل تحلیل تکنیکال. نشان‌دهنده جریان ورودی پول هوشمند و تسلط کامل خریداران است.",
            practicalTip = "ورود CALL در پول‌بک کندل چهارم با اکسپایری ۳ تا ۵ دقیقه.",
            tags = listOf("Three Soldiers", "سه سرباز سفید", "مومنتوم صعودی", "CALL")
        ),
        EncyclopediaItem(
            id = "s36_22", sectionNumber = 22, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۲۲ از ۳۶ • سه کندلی متوالی",
            title = "سه کلاغ سیاه (Three Black Crows)",
            titleEn = "Three Black Crows Breakdown",
            category = "الگوهای ادامه‌دهنده نزولی", direction = "PUT", winRate = "۹۲٪",
            summary = "سه کندل قرمز متوالی با کف‌های پایین‌تر و بدنه‌های کشیده در انتهای یک فاز صعودی.",
            fullContent = "نشان می‌دهد ریزش به صورت پایدار آغاز شده و تا سطوح حمایتی بعدی ادامه خواهد داشت.",
            practicalTip = "ورود PUT برای تداوم ترند نزولی.",
            tags = listOf("Three Crows", "سه کلاغ سیاه", "ترند نزولی", "PUT")
        ),
        EncyclopediaItem(
            id = "s36_23", sectionNumber = 23, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۲۳ از ۳۶ • جفت کندلی تراز کف",
            title = "انبرک کف یا موچین پایینی (Tweezer Bottom)",
            titleEn = "Tweezer Bottom Double Shadow",
            category = "الگوهای بازگشتی صعودی", direction = "CALL", winRate = "۸۸٪",
            summary = "دو کندل متوالی که سایه‌های پایینی آن‌ها دقیقاً در یک سطح قیمتی یکسان متوقف و پس زده شده‌اند.",
            fullContent = "برخورد دوگانه به یک سطح نامرئی و عدم توانایی فروشندگان برای شکستن آن کف، تضمین‌کننده حمایت و پرتاب قیمت به بالا است.",
            practicalTip = "ترید CALL به محض تثبیت کندل دوم.",
            tags = listOf("Tweezer Bottom", "انبرک کف", "تراز حمایت", "CALL")
        ),
        EncyclopediaItem(
            id = "s36_24", sectionNumber = 24, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۲۴ از ۳۶ • جفت کندلی تراز سقف",
            title = "انبرک سقف یا موچین بالایی (Tweezer Top)",
            titleEn = "Tweezer Top Resistance",
            category = "الگوهای بازگشتی نزولی", direction = "PUT", winRate = "۸۷٪",
            summary = "دو کندل متوالی که بالاترین نقطه سایه بالایی آن‌ها کاملاً منطبق بر یک خط مقاومت افقی است.",
            fullContent = "سقف دوقلوی مینیاتوری در تایم‌فریم ۱ دقیقه که ریجکشن مضاعف از سطح مقاومت را اثبات می‌کند.",
            practicalTip = "ترید PUT با اکسپایری ۱ تا ۲ دقیقه.",
            tags = listOf("Tweezer Top", "انبرک سقف", "تراز مقاومت", "PUT")
        ),
        EncyclopediaItem(
            id = "s36_25", sectionNumber = 25, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۲۵ از ۳۶ • الگوی ادامه‌دهنده",
            title = "سه متد صعودی (Rising Three Methods)",
            titleEn = "Rising Three Methods Continuation",
            category = "الگوهای ادامه‌دهنده صعودی", direction = "CALL", winRate = "۸۹٪",
            summary = "یک کندل سبز بزرگ، به دنبال آن ۳ کندل قرمز کوچک اصلاحی داخل بدنه کندل اول، و سپس کندل سبز پنجم بزرگ و شکست‌دهنده سقف.",
            fullContent = "الگوی تنفس در روند صعودی. ۳ کندل میانی صرفاً استراحت بازار بوده و کندل پنجم آغاز موج بعدی را با قدرت اعلام می‌کند.",
            practicalTip = "ورود CALL در آغاز کندل ششم.",
            tags = listOf("Rising Three", "سه متد صعودی", "ادامه روند", "CALL")
        ),
        EncyclopediaItem(
            id = "s36_26", sectionNumber = 26, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۲۶ از ۳۶ • الگوی ادامه‌دهنده",
            title = "سه متد نزولی (Falling Three Methods)",
            titleEn = "Falling Three Methods Continuation",
            category = "الگوهای ادامه‌دهنده نزولی", direction = "PUT", winRate = "۸۸٪",
            summary = "کندل قرمز بزرگ، ۳ کندل سبز کوچک در محدوده بدنه اول، و کندل قرمز پنجم پرقدرت که کف جدید می‌سازد.",
            fullContent = "استراحت موقت در روند نزولی قبل از شتاب مجدد ریزش.",
            practicalTip = "ورود PUT برای تعقیب موج ریزشی.",
            tags = listOf("Falling Three", "سه متد نزولی", "ریزش", "PUT")
        ),
        EncyclopediaItem(
            id = "s36_27", sectionNumber = 27, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۲۷ از ۳۶ • کندل تعادل",
            title = "فرفره یا اسپینینگ تاپ (Spinning Top)",
            titleEn = "Spinning Top Indecision",
            category = "الگوهای بلاتکلیفی", direction = "NEUTRAL", winRate = "۸۱٪",
            summary = "بدنه کوچک متقارن در مرکز کندل با سایه‌های بالا و پایین تقریباً مساوی.",
            fullContent = "نشان می‌دهد بازار در حالت فاز انقباض و بلاتکلیفی است. در باینری آپشن، تشکیل آن هشداری برای صبر کردن تا جهت‌گیری قطعی کندل بعد است.",
            practicalTip = "صبر کنید تا کندل بعدی جهت را با بدنه بزرگ مشخص کند.",
            tags = listOf("Spinning Top", "فرفره", "بلاتکلیفی")
        ),
        EncyclopediaItem(
            id = "s36_28", sectionNumber = 28, sectionType = "SECTION_28",
            sectionTitleBadge = "الگوی ۲۸ از ۳۶ • الگوی کلاسیک کف",
            title = "الگوی کف دوقلو (Double Bottom - W)",
            titleEn = "Double Bottom Reversal Pattern",
            category = "الگوهای کلاسیک چارتی", direction = "CALL", winRate = "۹۱٪",
            summary = "تشکیل دو دره در یک سطح حمایتی مشترک که پس از شکست خط گردن (Neckline) تغییر جهت به صعود را تایید می‌کند.",
            fullContent = "یکی از پایدارترین الگوهای پرایس اکشن در تمام تایم‌فریم‌ها. شکست خط گردن به همراه افزایش حجم، تضمین‌کننده یک موج صعودی به اندازه ارتفاع الگو است.",
            practicalTip = "ورود CALL در پول‌بک به خط گردن شکسته شده با اکسپایری ۳ الی ۵ دقیقه.",
            tags = listOf("Double Bottom", "کف دوقلو", "خط گردن", "CALL")
        ),
        EncyclopediaItem(
            id = "s36_29", sectionNumber = 29, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۲۹ از ۳۶ • الگوی کلاسیک سقف",
            title = "الگوی سقف دوقلو (Double Top - M)",
            titleEn = "Double Top Reversal Pattern",
            category = "الگوهای کلاسیک چارتی", direction = "PUT", winRate = "۹۰٪",
            summary = "تشکیل دو قله قیمتی در یک سطح مقاومتی و شکست رو به پایین خط گردن.",
            fullContent = "ناتوانی خریداران در ثبت سقف بالاتر و تشکیل فرم M نشانه پایان روند صعودی است.",
            practicalTip = "ورود PUT در شکست و پول‌بک خط گردن.",
            tags = listOf("Double Top", "سقف دوقلو", "پرایس اکشن", "PUT")
        ),
        EncyclopediaItem(
            id = "s36_30", sectionNumber = 30, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۳۰ از ۳۶ • سر و شانه سقف",
            title = "الگوی سر و شانه (Head and Shoulders)",
            titleEn = "Head and Shoulders Reversal",
            category = "الگوهای کلاسیک چارتی", direction = "PUT", winRate = "۹۲٪",
            summary = "شامل سه قله: شانه چپ، سر (قله بالاتر در مرکز) و شانه راست (قله پایین‌تر) با شکست خط گردن.",
            fullContent = "معروف‌ترین الگوی بازگشتی بازارهای مالی. شانه راست ضعف خریداران را نمایان می‌سازد و شکست خط گردن شروع ریزش بزرگ است.",
            practicalTip = "معامله PUT پس از تثبیت کندل زیر خط گردن.",
            tags = listOf("Head Shoulders", "سر و شانه", "خط گردن", "PUT")
        ),
        EncyclopediaItem(
            id = "s36_31", sectionNumber = 31, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۳۱ از ۳۶ • سر و شانه کف",
            title = "الگوی سر و شانه معکوس (Inverse Head and Shoulders)",
            titleEn = "Inverse Head and Shoulders",
            category = "الگوهای کلاسیک چارتی", direction = "CALL", winRate = "۹۲٪",
            summary = "شامل سه دره در کف قیمتی: دره مرکزی عمیق‌تر از دو دره مجاور و شکست خط گردن به سمت بالا.",
            fullContent = "پایان روند نزولی طولانی‌مدت و تغییر روند به صعودی با تارگت ارتفاع سر تا خط گردن.",
            practicalTip = "ورود CALL با انقضای ۵ دقیقه.",
            tags = listOf("Inverse H&S", "سر و شانه معکوس", "صعودی", "CALL")
        ),
        EncyclopediaItem(
            id = "s36_32", sectionNumber = 32, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۳۲ از ۳۶ • پرچم صعودی",
            title = "الگوی پرچم صعودی (Bullish Flag)",
            titleEn = "Bullish Flag Continuation",
            category = "الگوهای ادامه‌دهنده", direction = "CALL", winRate = "۸۹٪",
            summary = "یک حرکت صعودی شارپ (میله پرچم) و سپس یک کانال کوچک اصلاحی رو به پایین (پارچه پرچم).",
            fullContent = "شکست سقف کانال پرچم با یک کندل مومنتومی سبز، سیگنال ادامه پرشتاب روند اولیه به اندازه میله پرچم است.",
            practicalTip = "ورود CALL به محض بسته شدن کندل شکست بالای سقف پرچم.",
            tags = listOf("Bullish Flag", "پرچم صعودی", "شکست کانال", "CALL")
        ),
        EncyclopediaItem(
            id = "s36_33", sectionNumber = 33, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۳۳ از ۳۶ • پرچم نزولی",
            title = "الگوی پرچم نزولی (Bearish Flag)",
            titleEn = "Bearish Flag Continuation",
            category = "الگوهای ادامه‌دهنده", direction = "PUT", winRate = "۸۸٪",
            summary = "ریزش تند اولیه (میله) و کانال صعودی ضعیف اصلاحی رو به بالا.",
            fullContent = "شکست کف کانال پرچم نشانه بازگشت شتاب ریزش سنگین در جفت‌ارزهای فارکس و کریپتو است.",
            practicalTip = "ترید PUT با اکسپایری ۲ الی ۵ دقیقه.",
            tags = listOf("Bearish Flag", "پرچم نزولی", "ریزش شارپ", "PUT")
        ),
        EncyclopediaItem(
            id = "s36_34", sectionNumber = 34, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۳۴ از ۳۶ • مثلث صعودی",
            title = "مثلث صعودی (Ascending Triangle)",
            titleEn = "Ascending Triangle Breakout",
            category = "الگوهای شکست هندسی", direction = "CALL", winRate = "۸۷٪",
            summary = "سقف افقی ثابت مقاومت به همراه کف‌های بالارونده که فشرده شدن قیمت به سمت بالا را نشان می‌دهد.",
            fullContent = "کف‌های بالاتر نشان‌دهنده افزایش تمایل خریداران است که نهایتاً منجر به شکست انفجاری مقاومت افقی می‌شود.",
            practicalTip = "ورود CALL در شکست سقف مقاومت افقی.",
            tags = listOf("Ascending Triangle", "مثلث صعودی", "فشردگی", "CALL")
        ),
        EncyclopediaItem(
            id = "s36_35", sectionNumber = 35, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۳۵ از ۳۶ • مثلث نزولی",
            title = "مثلث نزولی (Descending Triangle)",
            titleEn = "Descending Triangle Breakdown",
            category = "الگوهای شکست هندسی", direction = "PUT", winRate = "۸۶٪",
            summary = "کف افقی حمایتی ثابت به همراه سقف‌های پایین‌آرونده که فشار فزاینده عرضه را نشان می‌دهد.",
            fullContent = "خریداران ضعیف‌تر شده و قادر به بالا بردن قیمت نیستند تا سرانجام حمایت کف شکسته و قیمت سقوط می‌کند.",
            practicalTip = "ترید PUT پس از شکست کندلی کف حمایت افقی.",
            tags = listOf("Descending Triangle", "مثلث نزولی", "فشار فروش", "PUT")
        ),
        EncyclopediaItem(
            id = "s36_36", sectionNumber = 36, sectionType = "SECTION_36",
            sectionTitleBadge = "الگوی ۳۶ از ۳۶ • تله شکست و ریجکشن",
            title = "شکست جعلی و تله نقدینگی (Fake Breakout / Bull & Bear Trap)",
            titleEn = "Fake Breakout & Liquidity Sweep Trap",
            category = "پرایس اکشن پیشرفته", direction = "BOTH", winRate = "۹۳٪",
            summary = "قیمت به طور موقت از یک مقاومت یا حمایت مهم خارج می‌شود اما کندل با سایه بلند بلافاصله به داخل برمی‌گردد.",
            fullContent = "تله‌های گاوی و خرسی زمانی رخ می‌دهند که موسسات مالی نقدینگی استاپ‌ها را شکار می‌کنند. بازگشت سریع کندل به داخل کانال یکی از پرسودترین ستاپ‌های معکوس در باینری آپشن است.",
            practicalTip = "ورود در جهت معکوس شکست جعلی: اگر مقاومت فیک شکست خورد، ترید PUT بگیرید و برعکس.",
            tags = listOf("Fakeout", "شکست جعلی", "تله نقدینگی", "پرایس اکشن")
        )
    )

    // ==========================================
    // بخش سوم: ۶۷ اصطلاح تخصصی، اندیکاتور، فرمول و استراتژی (SECTION 67)
    // ==========================================
    val sectionSixtySevenItems: List<EncyclopediaItem> = (1..67).map { index ->
        when (index) {
            1 -> EncyclopediaItem(
                id = "s67_1", sectionNumber = 1, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۱ از ۶۷ • فرمول ریاضی پایه",
                title = "نرخ برد سربه‌سر (Break-Even Win Rate)",
                titleEn = "Break-Even Mathematical Formula",
                category = "ریاضیات و فرمول‌ها", direction = "NEUTRAL",
                summary = "حداقل وین‌ریت ریاضی لازم برای صفر شدن زیان و جلوگیری از افت بالانس: BE = L / (P + L).",
                fullContent = "در قراردادهای باینری آپشن اگر پی‌اوت بروکر P و زیان در هر معامله L باشد، نرخ برد سربه‌سر از فرمول BE = L / (P + L) محاسبه می‌شود. در پی‌اوت ۹۲٪ (P=0.92, L=1.0)، حداقل وین‌ریت ۵۲.۰۸٪ است. هر وین‌ریتی بالاتر از این عدد سود خالص محسوب می‌شود.",
                practicalTip = "فقط در دارایی‌هایی ترید کنید که وین‌ریت تحلیلی شما حداقل ۱۰٪ بالاتر از نرخ سربه‌سر باشد.",
                tags = listOf("Break-Even", "فرمول ریاضی", "سربه‌سر", "Payout")
            )
            2 -> EncyclopediaItem(
                id = "s67_2", sectionNumber = 2, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۲ از ۶۷ • نظریه احتمال",
                title = "ارزش امید ریاضی معامله (Expected Value - EV)",
                titleEn = "Mathematical Expected Value Formula",
                category = "ریاضیات و فرمول‌ها", direction = "NEUTRAL",
                summary = "ارزش امید ریاضی هر معامله: EV = (p × W) - ((1 - p) × L).",
                fullContent = "امید ریاضی نشان می‌دهد در صورتی که یک ستاپ معاملاتی ۱۰۰ بار تکرار شود، به طور میانگین چند دلار سود یا زیان خواهد ساخت. سیستم‌های معاملاتی موفق همواره EV مثبت تولید می‌کنند و پلتفرم ایران باینری آپشن فقط سیگنال‌های با EV مثبت را منتشر می‌سازد.",
                practicalTip = "اگر EV معامله‌ای منفی باشد، حتی با چند برد متوالی نهایتاً به باخت سرمایه منجر خواهد شد.",
                tags = listOf("Expected Value", "امید ریاضی", "احتمالات", "Edge")
            )
            3 -> EncyclopediaItem(
                id = "s67_3", sectionNumber = 3, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۳ از ۶۷ • اسیلاتور مومنتوم",
                title = "شاخص قدرت نسبی (Relative Strength Index - RSI 14)",
                titleEn = "RSI 14 Momentum Oscillator",
                category = "اندیکاتورها و اسیلاتورها", direction = "BOTH",
                summary = "اندازه‌گیری سرعت و تغییرات حرکات قیمتی در بازه ۰ تا ۱۰۰ با سطوح اشباع ۳۰ و ۷۰.",
                fullContent = "عبور RSI به زیر ۳۰ نشانه اشباع فروش (Oversold) و احتمال بازگشت به سمت بالا (CALL) است. عبور به بالای ۷۰ نشانه اشباع خرید (Overbought) و احتمال افت (PUT) است. در بازارهای رونددار، استفاده از واگرایی‌های RSI موثرتر از اشباع ساده است.",
                practicalTip = "ترکیب اشباع RSI با کندل پین‌بار در کف یا سقف، وین‌ریت را به بالای ۸۷٪ می‌رساند.",
                tags = listOf("RSI", "اسیلاتور", "اشباع خرید", "اشباع فروش")
            )
            4 -> EncyclopediaItem(
                id = "s67_4", sectionNumber = 4, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۴ از ۶۷ • اسیلاتور نوسان‌نما",
                title = "نوسان‌گر استوکاستیک (Stochastic Oscillator %K %D)",
                titleEn = "Stochastic %K %D Reversal Oscillator",
                category = "اندیکاتورها و اسیلاتورها", direction = "BOTH",
                summary = "مقایسه قیمت بسته شدن با دامنه نوسان در دوره‌های زمانی مشخص با سطوح ۲۰ و ۸۰.",
                fullContent = "تقاطع خط سریع %K با خط کند %D در ناحیه زیر ۲۰ یک سیگنال قدرتمند CALL و در ناحیه بالای ۸۰ سیگنال PUT است. استوکاستیک در بازارهای رنج (سایدوی) بالاترین بازدهی را دارد.",
                practicalTip = "در تایم ۱ دقیقه، تقاطع خطوط در محدوده زیر ۲۰ همزمان با لمس خط حمایت، زمان بهینه ورود است.",
                tags = listOf("Stochastic", "استوکاستیک", "تقاطع خطوط", "بازار رنج")
            )
            5 -> EncyclopediaItem(
                id = "s67_5", sectionNumber = 5, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۵ از ۶۷ • اندیکاتور روند و مومنتوم",
                title = "اندیکاتور مکدی (MACD - Moving Average Convergence Divergence)",
                titleEn = "MACD Trend & Momentum Indicator",
                category = "اندیکاتورها و اسیلاتورها", direction = "BOTH",
                summary = "ارزیابی تقاطع خطوط MACD و خط سیگنال همراه با هیستوگرام حجم تغییرات شتاب قیمت.",
                fullContent = "عبور خط مکدی از خط سیگنال به سمت بالا و مثبت شدن میله‌های هیستوگرام نشانه شروع مومنتوم صعودی و زمان مناسب برای تریدهای CALL با انقضای ۳ تا ۵ دقیقه است.",
                practicalTip = "بهترین سیگنال‌های مکدی در واگرایی هیستوگرام با سقف‌ها و کف‌های قیمت ظاهر می‌شوند.",
                tags = listOf("MACD", "مکدی", "هیستوگرام", "مومنتوم")
            )
            6 -> EncyclopediaItem(
                id = "s67_6", sectionNumber = 6, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۶ از ۶۷ • کانال‌های نوسان و انحراف معیار",
                title = "باندهای بولینگر (Bollinger Bands - 20, 2)",
                titleEn = "Bollinger Bands Volatility Bands",
                category = "اندیکاتورها و اسیلاتورها", direction = "BOTH",
                summary = "میانگین متحرک ۲۰ دوره‌ای به همراه دو باند انحراف معیار ۲+ و ۲- برای تشخیص نوسانات.",
                fullContent = "برخورد قیمت به باند بالایی در فاز رنج سیگنال PUT و برخورد به باند پایینی سیگنال CALL است. فشردگی باندها (Squeeze) نیز هشدار یک انفجار شارپ قیمتی است.",
                practicalTip = "در زمان فاز فشردگی باندها معامله نکنید؛ منتظر شکست یکی از باندها با کندل پرقدرت بمانید.",
                tags = listOf("Bollinger Bands", "باندهای بولینگر", "فشردگی", "نوسان")
            )
            7 -> EncyclopediaItem(
                id = "s67_7", sectionNumber = 7, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۷ از ۶۷ • شاخص نوسان‌پذیری واقعی",
                title = "دامنه واقعی میانگین (Average True Range - ATR)",
                titleEn = "ATR Volatility & Noise Filter",
                category = "مدیریت ریسک و نوسان", direction = "NEUTRAL",
                summary = "محاسبه میانگین نوسان واقعی هر کندل برای فیلتر نویز و انتخاب زمان انقضای بهینه.",
                fullContent = "مقدار بالای ATR نشان‌دهنده نوسانات شدید و جهش‌های غیرقابل پیش‌بینی است. پلتفرم ایران باینری از ATR برای تنظیم زمان انقضای سیگنال‌ها (۱ دقیقه در نوسان کم و ۵ دقیقه در نوسان تثبیت‌شده) بهره می‌گیرد.",
                practicalTip = "در زمان جهش ناگهانی ATR به دلیل اخبار، زمان انقضا را طولانی‌تر انتخاب کنید.",
                tags = listOf("ATR", "دامنه نوسان", "فیلتر نویز", "اکسپایری")
            )
            8 -> EncyclopediaItem(
                id = "s67_8", sectionNumber = 8, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۸ از ۶۷ • سیستم معاملاتی جامع ژاپنی",
                title = "ابر ایچیموکو (Ichimoku Kinko Hyo Cloud)",
                titleEn = "Ichimoku Cloud Trading System",
                category = "تحلیل تکنیکال کلاسیک", direction = "BOTH",
                summary = "شامل تنکان‌سن، کیجون‌سن، سنکو اسپن A و B و چیکو اسپن برای تعیین روند و حمایت/مقاومت.",
                fullContent = "قرارگیری قیمت در بالای ابر سبز کومو تاییدکننده روند صعودی قدرتمند است. تقاطع تنکان‌سن از پایین به بالای کیجون‌سن سیگنال قطعی ورود به پوزیشن CALL در باینری آپشن است.",
                practicalTip = "در داخل ابر کومو به دلیل فاز تعادل و عدم جهت‌گیری واضح، از معامله بپرهیزید.",
                tags = listOf("Ichimoku", "ایچیموکو", "ابر کومو", "کیجون سن")
            )
            9 -> EncyclopediaItem(
                id = "s67_9", sectionNumber = 9, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۹ از ۶۷ • نسبت‌های طلایی هندسی",
                title = "اصلاحی فیبوناچی (Fibonacci Retracement 61.8% & 50%)",
                titleEn = "Fibonacci Golden Ratio Retracements",
                category = "تحلیل تکنیکال کلاسیک", direction = "BOTH",
                summary = "تعیین ترازهای بازگشتی قیمت در پایان یک موج پرشتاب با نسبت طلایی ۶۱.۸٪ و ۵۰٪.",
                fullContent = "سطح ۶۱.۸ درصد فیبوناچی به عنوان نسبت طلایی طبیعت، بالاترین حجم سفارشات واکنش قیمتی را جذب می‌کند. در باینری آپشن، لمس سطح ۶۱.۸٪ همزمان با الگوی پین‌بار بالاترین وین‌ریت را دارد.",
                practicalTip = "رسم فیبوناچی از کف تا سقف موج صعودی و انتظار برای پول‌بک به ترازهای ۵۰٪ و ۶۱.۸٪.",
                tags = listOf("Fibonacci", "فیبوناچی", "نسبت طلایی", "سطوح ۶۱.۸")
            )
            10 -> EncyclopediaItem(
                id = "s67_10", sectionNumber = 10, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۱۰ از ۶۷ • اهداف گسترش قیمت",
                title = "اکستنشن فیبوناچی (Fibonacci Extension 161.8%)",
                titleEn = "Fibonacci Projection & Extension",
                category = "تحلیل تکنیکال کلاسیک", direction = "BOTH",
                summary = "پیش‌بینی اهداف پایان موج‌های انبساطی با ترازهای ۱۲۷.۲٪ و ۱۶۱.۸٪ برای شکار ریورسال.",
                fullContent = "در تایم‌فریم‌های کوتاه‌مدت، زمانی که یک دارایی به تراز ۱۶۱.۸٪ اکستنشن فیبوناچی می‌رسد، معمولاً دچار اشباع خرید/فروش شدید شده و کندل‌های معکوس شکل می‌دهد.",
                practicalTip = "گرفتن پوزیشن PUT معکوس در تراز ۱۶۱.۸٪ صعودی.",
                tags = listOf("Fibonacci Extension", "اکستنشن", "تارگت قیمتی")
            )
            11 -> EncyclopediaItem(
                id = "s67_11", sectionNumber = 11, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۱۱ از ۶۷ • میانگین متحرک پرسرعت",
                title = "میانگین متحرک نمایی (Exponential Moving Average - EMA 20 & 50)",
                titleEn = "EMA Dynamic Support & Resistance",
                category = "تحلیل تکنیکال کلاسیک", direction = "BOTH",
                summary = "وزن‌دهی بیشتر به قیمت‌های اخیر و واکنش سریع‌تر نسبت به میانگین متحرک ساده.",
                fullContent = "میانگین متحرک نمایی ۲۰ (EMA 20) به عنوان حمایت یا مقاومت داینامیک در روندهای فعال عمل می‌کند. برخورد قیمت به خط EMA 20 و پرتاب به سمت روند اصلی، ستاپ معاملاتی بسیار موثری است.",
                practicalTip = "استراتژی تقاطع طلایی: عبور EMA 20 به بالای EMA 50 نشانه ترند صعودی برای تریدهای CALL.",
                tags = listOf("EMA", "میانگین متحرک نمایی", "حمایت داینامیک")
            )
            12 -> EncyclopediaItem(
                id = "s67_12", sectionNumber = 12, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۱۲ از ۶۷ • میانگین متحرک پایه",
                title = "میانگین متحرک ساده (Simple Moving Average - SMA 200)",
                titleEn = "SMA 200 Macro Trend Filter",
                category = "تحلیل تکنیکال کلاسیک", direction = "NEUTRAL",
                summary = "محاسبه میانگین حسابی قیمت‌ها در ۲۰۰ کندل برای تشخیص روند ماژور و کلان بازار.",
                fullContent = "معامله‌گران حرفه‌ای هرگز خلاف جهت SMA 200 ترید نمی‌کنند؛ وقتی قیمت بالای SMA 200 است فقط به دنبال ستاپ‌های CALL و وقتی زیر آن است به دنبال ستاپ‌های PUT می‌باشند.",
                practicalTip = "جهت سیگنال را همیشه با موقعیت قیمت نسبت به SMA 200 فیلتر کنید.",
                tags = listOf("SMA", "میانگین ساده", "روند ماژور", "فیلتر ترند")
            )
            13 -> EncyclopediaItem(
                id = "s67_13", sectionNumber = 13, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۱۳ از ۶۷ • توقف و بازگشت شتاب‌دار",
                title = "پارابولیک سار (Parabolic SAR - Stop and Reverse)",
                titleEn = "Parabolic SAR Trailing Dots",
                category = "اندیکاتورها و اسیلاتورها", direction = "BOTH",
                summary = "نمایش نقاط در بالا یا پایین کندل‌ها برای تعیین تغییر جهت و مومنتوم شتاب‌دار.",
                fullContent = "انتقال اولین نقطه پارابولیک سار از بالای کندل‌ها به زیر آن‌ها نشانه شروع روند صعودی و تاییدیه ورود به معامله CALL با اکسپایری ۲ تا ۳ دقیقه است.",
                practicalTip = "تایید تقاطع پارابولیک سار با مکدی یکی از ستاپ‌های وین‌ریت ۸۹٪ است.",
                tags = listOf("Parabolic SAR", "پارابولیک", "نقاط سار", "جهت ترند")
            )
            14 -> EncyclopediaItem(
                id = "s67_14", sectionNumber = 14, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۱۴ از ۶۷ • سنجش قدرت روند",
                title = "شاخص جهت‌دار میانگین (Average Directional Index - ADX 14)",
                titleEn = "ADX Trend Strength Quantifier",
                category = "اندیکاتورها و اسیلاتورها", direction = "NEUTRAL",
                summary = "ارزیابی میزان قدرت روند صرف‌نظر از جهت صعودی یا نزولی آن در مقیاس ۰ تا ۱۰۰.",
                fullContent = "عدد ADX بالای ۲۵ نشان‌دهنده یک روند واقعی و پرقدرت در بازار است؛ در حالی که ADX زیر ۲۰ نشانه بازار رنج و خواب‌آلود است. در ADX زیر ۲۰ نباید از استراتژی‌های شکست و ترند پیروی کرد.",
                practicalTip = "تنها زمانی ستاپ‌های شکست سطح را معامله کنید که مقدار خط ADX بالای ۲۵ باشد.",
                tags = listOf("ADX", "قدرت روند", "فیلتر رنج", "فیلتر ترند")
            )
            15 -> EncyclopediaItem(
                id = "s67_15", sectionNumber = 15, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۱۵ از ۶۷ • سیستم دنبال‌کننده ترند",
                title = "اندیکاتور سوپرترند (SuperTrend 10, 3)",
                titleEn = "SuperTrend Dynamic Volatility Band",
                category = "اندیکاتورها و اسیلاتورها", direction = "BOTH",
                summary = "ترکیب میانگین متحرک و ضریب ATR برای ایجاد خطوط حمایت/مقاومت سبز و قرمز.",
                fullContent = "تغییر رنگ خط سوپرترند از قرمز به سبز و انتقال آن به زیر کندل، سیگنال صعودی واضحی برای باینری آپشن است. این اندیکاتور برای تریدهای ۵ دقیقه‌ای بسیار قابل اعتماد است.",
                practicalTip = "ورود در پول‌بک به خط سبز سوپرترند برای ترید CALL.",
                tags = listOf("SuperTrend", "سوپرترند", "سیستم ترند", "سیگنال سبز")
            )
            16 -> EncyclopediaItem(
                id = "s67_16", sectionNumber = 16, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۱۶ از ۶۷ • تحلیل حجم قیمت",
                title = "پروفایل حجم معاملات (Volume Profile & Point of Control - POC)",
                titleEn = "Volume Profile POC High Liquidity Zones",
                category = "تحلیل حجم و اوردربوک", direction = "BOTH",
                summary = "نمایش حجم معاملات انجام‌شده در هر سطح قیمتی به صورت نمودار افقی.",
                fullContent = "نقطه POC (Point of Control) قیمتی است که بیشترین حجم قراردادها در آن مبادله شده است. قیمت به عنوان آهنربا به سمت POC جذب شده و روی آن واکنش برگشتی نشان می‌دهد.",
                practicalTip = "معامله معکوس در برخورد به لبه‌های کم‌حجم (VAH / VAL) و برگشت به سمت POC.",
                tags = listOf("Volume Profile", "پروفایل حجم", "POC", "نقدینگی")
            )
            17 -> EncyclopediaItem(
                id = "s67_17", sectionNumber = 17, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۱۷ از ۶۷ • حجم تعادلی تجمعی",
                title = "اندیکاتور حجم تعادلی (On-Balance Volume - OBV)",
                titleEn = "On-Balance Volume Cumulative Flow",
                category = "تحلیل حجم و اوردربوک", direction = "BOTH",
                summary = "محاسبه جریان تجمعی حجم بر اساس مثبت یا منفی بسته شدن کندل‌ها.",
                fullContent = "اگر قیمت در حال رنج زدن باشد اما خط OBV صعودی شود، نشانه جمع‌آوری پنهانی نقدینگی توسط پول هوشمند قبل از انفجار صعودی است.",
                practicalTip = "تشخیص شکست‌های واقعی با تایید شکست سقف در اندیکاتور OBV.",
                tags = listOf("OBV", "حجم تعادلی", "پول هوشمند", "جریان نقدینگی")
            )
            18 -> EncyclopediaItem(
                id = "s67_18", sectionNumber = 18, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۱۸ از ۶۷ • شاخص جریان پول",
                title = "شاخص جریان نقدینگی (Money Flow Index - MFI)",
                titleEn = "Money Flow Index Volume-Weighted RSI",
                category = "اندیکاتورها و اسیلاتورها", direction = "BOTH",
                summary = "مشابه RSI اما با لحاظ کردن فاکتور حجم معاملات در فرمول محاسبه.",
                fullContent = "مقدار MFI زیر ۲۰ نشانه اشباع شدید فروش و ورود نقدینگی نهادی است که نویدبخش جهش صعودی برای گزینه‌های CALL در باینری آپشن است.",
                practicalTip = "تایید واگرایی در MFI اعتبار به مراتب بالاتری نسبت به RSI ساده دارد.",
                tags = listOf("MFI", "جریان پول", "حجم نقدینگی", "اشباع")
            )
            19 -> EncyclopediaItem(
                id = "s67_19", sectionNumber = 19, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۱۹ از ۶۷ • کانال نوسان بر پایه ATR",
                title = "کانال‌های کلتنر (Keltner Channels)",
                titleEn = "Keltner Channels Envelope",
                category = "اندیکاتورها و اسیلاتورها", direction = "BOTH",
                summary = "کانال‌های نوسانی مبتنی بر میانگین متحرک نمایی و دامنه‌های ATR.",
                fullContent = "در مقایسه با بولینگر باندز، کانال‌های کلتنر نرم‌تر بوده و خروج کندل از کانال بالایی نشان‌دهنده یک روند صعودی بسیار مقتدر است.",
                practicalTip = "استراتژی TTM Squeeze: وقتی بولینگر باند به داخل کانال کلتنر می‌رود، آماده شکست انفجاری باشید.",
                tags = listOf("Keltner Channels", "کلتنر", "نوسان سنج", "TTM Squeeze")
            )
            20 -> EncyclopediaItem(
                id = "s67_20", sectionNumber = 20, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۲۰ از ۶۷ • فیلتر نویز امواج",
                title = "اندیکاتور زیگزاگ (ZigZag Peak & Valley Indicator)",
                titleEn = "ZigZag Wave Structure Filter",
                category = "تحلیل تکنیکال کلاسیک", direction = "NEUTRAL",
                summary = "حذف نوسانات جزئی و اتصال سقف‌ها و کف‌های اصلی به یکدیگر به صورت خطوط پیوسته.",
                fullContent = "زیگزاگ به معامله‌گر کمک می‌کند ساختار امواج ماژور، الگوهای کلاسیک چارتی و سطوح کلیدی تغییر روند را با وضوح مشاهده نماید.",
                practicalTip = "شناسایی سقف‌های پایین‌تر (LH) و کف‌های پایین‌تر (LL) برای تایید روند نزولی.",
                tags = listOf("ZigZag", "زیگزاگ", "امواج ماژور", "قله و دره")
            )
            21 -> EncyclopediaItem(
                id = "s67_21", sectionNumber = 21, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۲۱ از ۶۷ • کانال قیمت کالاها",
                title = "شاخص کانال کالا (Commodity Channel Index - CCI)",
                titleEn = "CCI Momentum Divergence",
                category = "اندیکاتورها و اسیلاتورها", direction = "BOTH",
                summary = "اندازه‌گیری انحراف قیمت آماری از میانگین میان‌مدت با سطوح ۱۰۰+ و ۱۰۰-.",
                fullContent = "عبور CCI به زیر ۲۰۰- در چارت طلا یا جفت‌ارزها نشانه تخفیف قیمتی شدید و زمان ورود به معامله CALL است.",
                practicalTip = "سیگنال بازگشت سریع در هنگام خروج مجدد از محدوده ۱۰۰- به سمت بالا.",
                tags = listOf("CCI", "کانال کالا", "اشباع آماری")
            )
            22 -> EncyclopediaItem(
                id = "s67_22", sectionNumber = 22, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۲۲ از ۶۷ • واگرایی کلاسیک کف",
                title = "واگرایی معمولی مثبت (Regular Bullish Divergence - RD+)",
                titleEn = "Regular Bullish Momentum Divergence",
                category = "واگرایی‌ها و مومنتوم", direction = "CALL",
                summary = "قیمت کف پایین‌تر (LL) می‌سازد اما اسیلاتور (RSI/MACD) کف بالاتر (HL) ثبت می‌کند.",
                fullContent = "نشان می‌دهد با وجود افت ظاهری قیمت، شتاب فروشندگان به شدت افت کرده و روند نزولی در آستانه معکوس شدن به صعود است. وین‌ریت این ستاپ بالای ۹۰٪ است.",
                practicalTip = "ورود CALL با اکسپایری ۳ تا ۵ دقیقه در تایید کندل برگشتی.",
                tags = listOf("Divergence", "واگرایی مثبت", "RD+", "سیگنال CALL")
            )
            23 -> EncyclopediaItem(
                id = "s67_23", sectionNumber = 23, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۲۳ از ۶۷ • واگرایی کلاسیک سقف",
                title = "واگرایی معمولی منفی (Regular Bearish Divergence - RD-)",
                titleEn = "Regular Bearish Momentum Divergence",
                category = "واگرایی‌ها و مومنتوم", direction = "PUT",
                summary = "قیمت سقف بالاتر (HH) ثبت می‌کند اما اسیلاتور سقف پایین‌تر (LH) می‌سازد.",
                fullContent = "هشدار قطعی پایان موج صعودی و خالی شدن خریداران. ستاپ ممتاز برای قراردادهای PUT در باینری آپشن.",
                practicalTip = "ورود PUT در شکست کف کندل قبلی در سقف واگرایی.",
                tags = listOf("Divergence", "واگرایی منفی", "RD-", "سیگنال PUT")
            )
            24 -> EncyclopediaItem(
                id = "s67_24", sectionNumber = 24, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۲۴ از ۶۷ • واگرایی مخفی صعودی",
                title = "واگرایی مخفی مثبت (Hidden Bullish Divergence - HD+)",
                titleEn = "Hidden Bullish Trend Continuation",
                category = "واگرایی‌ها و مومنتوم", direction = "CALL",
                summary = "قیمت کف بالاتر (HL) می‌سازد اما اسیلاتور کف پایین‌تر (LL) ثبت می‌کند.",
                fullContent = "واگرایی مخفی نشان‌دهنده تداوم قدرتمند روند صعودی است و در اصلاح‌ها فرصت عالی برای ورود به معاملات CALL فراهم می‌سازد.",
                practicalTip = "ورود در جهت ترند اصلی صعودی با اکسپایری ۲ تا ۵ دقیقه.",
                tags = listOf("Hidden Divergence", "واگرایی مخفی", "HD+", "ادامه ترند")
            )
            25 -> EncyclopediaItem(
                id = "s67_25", sectionNumber = 25, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۲۵ از ۶۷ • واگرایی مخفی نزولی",
                title = "واگرایی مخفی منفی (Hidden Bearish Divergence - HD-)",
                titleEn = "Hidden Bearish Trend Continuation",
                category = "واگرایی‌ها و مومنتوم", direction = "PUT",
                summary = "قیمت سقف پایین‌تر (LH) ثبت می‌کند اما اسیلاتور سقف بالاتر (HH) می‌سازد.",
                fullContent = "تایید ادامه پرقدرت روند نزولی پس از یک اصلاح موقت در بازار باینری آپشن.",
                practicalTip = "ورود PUT در پول‌بک به خط روند نزولی.",
                tags = listOf("Hidden Divergence", "واگرایی مخفی منفی", "HD-", "PUT")
            )
            26 -> EncyclopediaItem(
                id = "s67_26", sectionNumber = 26, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۲۶ از ۶۷ • سطوح افقی کلیدی",
                title = "سطوح حمایت و مقاومت استاتیک (Static Support & Resistance)",
                titleEn = "Static Horizontal Support & Resistance",
                category = "پرایس اکشن پایه", direction = "BOTH",
                summary = "خطوط افقی کلیدی بر اساس سقف‌ها و کف‌های قیمتی گذشته که بیشترین واکنش را ثبت کرده‌اند.",
                fullContent = "سطوح استاتیک حافظه بازار هستند. هرچه یک سطح در گذشته دفعات بیشتری قیمت را برگردانده باشد، اعتبار آن بالاتر است و ریجکشن از آن ستاپ ورود باینری مطمئنی می‌سازد.",
                practicalTip = "قانون تبدیل سطوح: مقاومت شکسته شده به حمایت جدید تبدیل می‌شود.",
                tags = listOf("Support", "Resistance", "سطوح استاتیک", "پرایس اکشن")
            )
            27 -> EncyclopediaItem(
                id = "s67_27", sectionNumber = 27, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۲۷ از ۶۷ • خطوط متحرک و شیب‌دار",
                title = "سطوح حمایت و مقاومت داینامیک (Dynamic S/R & Trendlines)",
                titleEn = "Dynamic Moving Average & Trendline S/R",
                category = "پرایس اکشن پایه", direction = "BOTH",
                summary = "خطوط روند شیب‌دار و میانگین‌های متحرک که همگام با زمان و قیمت تغییر موقعیت می‌دهند.",
                fullContent = "خطوط روند داینامیک سومین لمس (Touch 3) بالاترین وین‌ریت را در باینری آپشن دارد؛ در لمس چهارم احتمال شکست خط بیشتر می‌شود.",
                practicalTip = "ورود مطمئن در سومین برخورد کندل به خط روند داینامیک.",
                tags = listOf("Dynamic S/R", "خط روند", "حمایت متحرک", "پرایس اکشن")
            )
            28 -> EncyclopediaItem(
                id = "s67_28", sectionNumber = 28, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۲۸ از ۶۷ • نواحی تصمیم‌گیری نهادی",
                title = "مناطق عرضه و تقاضا (Supply & Demand Imbalance Zones)",
                titleEn = "Supply & Demand Institutional Zones",
                category = "پرایس اکشن پیشرفته (SMC)", direction = "BOTH",
                summary = "باکس‌های قیمتی که موسسات مالی سفارشات سنگین بازنشده دارند و باعث پرتاب شارپ قیمت می‌شوند.",
                fullContent = "هنگامی که قیمت پس از یک موج شارپ برای اولین بار به منطقه تقاضا بازمی‌گردد (First Time Back - FTB)، واکنش جهشی قدرتمندی برای معامله CALL ایجاد می‌کند.",
                practicalTip = "ورود در جعبه تقاضا با انقضای ۱ تا ۳ دقیقه.",
                tags = listOf("Supply Demand", "عرضه و تقاضا", "SMC", "عدم تعادل")
            )
            29 -> EncyclopediaItem(
                id = "s67_29", sectionNumber = 29, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۲۹ از ۶۷ • بلوک‌های سفارش بانکی",
                title = "اوردر بلاک نهادی (Order Block - OB)",
                titleEn = "Institutional Order Block Concepts",
                category = "پرایس اکشن پیشرفته (SMC)", direction = "BOTH",
                summary = "آخرین کندل مخالف قبل از یک حرکت انفجاری قیمتی که نمایانگر نقطه ورود پول هوشمند است.",
                fullContent = "اوردر بلاک صعودی (Bullish OB) آخرین کندل قرمز قبل از جهش شارپ صعودی است. در برگشت قیمت به این کندل، ترید CALL با وین‌ریت فوق‌العاده بالا همراه است.",
                practicalTip = "انتخاب بدنه اوردر بلاک به عنوان نقطه ورود در ثانیه ۰۰.",
                tags = listOf("Order Block", "اوردر بلاک", "پول هوشمند", "SMC")
            )
            30 -> EncyclopediaItem(
                id = "s67_30", sectionNumber = 30, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۳۰ از ۶۷ • عدم تعادل نقدینگی",
                title = "شکاف ارزش منصفانه (Fair Value Gap - FVG / Imbalance)",
                titleEn = "Fair Value Gap & Price Imbalance",
                category = "پرایس اکشن پیشرفته (SMC)", direction = "BOTH",
                summary = "فاصله بین سایه کندل اول و کندل سوم در یک جهش ۳ کندلی که بازار نقدینگی یک‌طرفه داشته است.",
                fullContent = "بازار همواره تمایل دارد FVG را پر کند (Mitigation). پس از پر شدن ۵۰٪ از باکس FVG، قیمت با سرعت به سمت جهت اصلی پرتاب می‌شود.",
                practicalTip = "ترید در تراز ۵۰٪ میانی (Consequent Encroachment) ناحیه FVG.",
                tags = listOf("FVG", "ارزش منصفانه", "Imbalance", "SMC")
            )
            31 -> EncyclopediaItem(
                id = "s67_31", sectionNumber = 31, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۳۱ از ۶۷ • تجمیع استاپ‌ها",
                title = "استخر نقدینگی (Liquidity Pool - BSL / SSL)",
                titleEn = "Buy-Side & Sell-Side Liquidity Pools",
                category = "پرایس اکشن پیشرفته (SMC)", direction = "BOTH",
                summary = "نواحی بالا یا پایین سقف‌ها و کف‌های برابر که مملو از استاپ‌لاس‌های معامله‌گران خرد است.",
                fullContent = "قیمت معمولاً ابتدا استخر نقدینگی را شکار می‌کند و سپس بلافاصله تغییر مسیر می‌دهد. آگاهی از این نواحی مانع افتادن در تله‌های بازار می‌شود.",
                practicalTip = "منتظر پاکسازی نقدینگی سقف‌ها بمانید و سپس وارد پوزیشن PUT شوید.",
                tags = listOf("Liquidity Pool", "استخر نقدینگی", "استاپ هانت", "SMC")
            )
            32 -> EncyclopediaItem(
                id = "s67_32", sectionNumber = 32, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۳۲ از ۶۷ • پاکسازی استاپ‌ها",
                title = "شکار نقدینگی سریع (Liquidity Sweep / Raid)",
                titleEn = "Liquidity Sweep & Rapid Rejection",
                category = "پرایس اکشن پیشرفته (SMC)", direction = "BOTH",
                summary = "نفوذ سریع سایه کندل به بالای یک سقف ماژور و بازگشت برق‌آسا به زیر سطح در همان کندل.",
                fullContent = "یکی از قدرتمندترین ستاپ‌های ۱ دقیقه‌ای باینری آپشن. پس از Sweep سقف، معامله PUT با اکسپایری ۱ دقیقه بالاترین بازدهی را دارد.",
                practicalTip = "ورود فوری PUT پس از بسته شدن کندل Sweep زیر مقاومت.",
                tags = listOf("Liquidity Sweep", "شکار نقدینگی", "تله استاپ", "PUT")
            )
            33 -> EncyclopediaItem(
                id = "s67_33", sectionNumber = 33, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۳۳ از ۶۷ • تداوم ساختار روند",
                title = "شکست ساختار بازار (Break of Structure - BOS)",
                titleEn = "Break of Structure Trend Continuation",
                category = "پرایس اکشن پیشرفته (SMC)", direction = "BOTH",
                summary = "شکسته شدن سقف یا کف قبلی در جهت روند با بدنه کامل کندل که نشانه ادامه روند است.",
                fullContent = "ثبت BOS صعودی نشان می‌دهد خریداران همچنان بازار را کنترل می‌کنند و باید به دنبال ستاپ‌های ورود CALL در پول‌بک بود.",
                practicalTip = "معامله در جهت BOS پس از تکمیل پول‌بک به سطح شکسته شده.",
                tags = listOf("BOS", "شکست ساختار", "ادامه ترند", "SMC")
            )
            34 -> EncyclopediaItem(
                id = "s67_34", sectionNumber = 34, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۳۴ از ۶۷ • تغییر رژیم و جهت",
                title = "تغییر شخصیت و جهت روند (Change of Character - CHoCH)",
                titleEn = "Change of Character Market Shift",
                category = "پرایس اکشن پیشرفته (SMC)", direction = "BOTH",
                summary = "شکست اولین کف ماژور در روند صعودی یا شکست اولین سقف ماژور در روند نزولی که نشانه تغییر روند است.",
                fullContent = "CHoCH نخستین هشدار معتبر از اتمام یک روند و آغاز فاز معکوس است و تریدر را از افتادن در تله‌های تعقیب روند نجات می‌دهد.",
                practicalTip = "ورود معکوس در اولین پول‌بک پس از ثبت CHoCH.",
                tags = listOf("CHoCH", "تغییر ساختار", "چرخش روند", "SMC")
            )
            35 -> EncyclopediaItem(
                id = "s67_35", sectionNumber = 35, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۳۵ از ۶۷ • نقطه ورود تخفیف‌دار",
                title = "نقطه بهینه ورود به معامله (Optimal Trade Entry - OTE)",
                titleEn = "Optimal Trade Entry 70.5% - 79% Zone",
                category = "پرایس اکشن پیشرفته (SMC)", direction = "BOTH",
                summary = "محدوده بین ترازهای ۶۲٪، ۷۰.۵٪ و ۷۹٪ فیبوناچی که بیشترین نرخ سودآوری ورود را دارد.",
                fullContent = "در ستاپ‌های OTE، قیمت با حداکثر تخفیف به معامله‌گر عرضه می‌شود و شانس بسته شدن معامله در سود (ITM) به اوج می‌رسد.",
                practicalTip = "تنظیم نقطه ورود دقیق در تراز ۷۰.۵٪ OTE.",
                tags = listOf("OTE", "نقطه بهینه", "تخفیف قیمتی", "فیبوناچی")
            )
            36 -> EncyclopediaItem(
                id = "s67_36", sectionNumber = 36, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۳۶ از ۶۷ • نظریه کلاسیک روندها",
                title = "نظریه داو در تایم‌فریم‌های کوتاه‌مدت (Dow Theory in Binary Options)",
                titleEn = "Dow Theory Higher Highs & Lower Lows",
                category = "تحلیل تکنیکال کلاسیک", direction = "BOTH",
                summary = "روند صعودی حاصل سقف‌های بالاتر (HH) و کف‌های بالاتر (HL) و روند نزولی حاصل (LH) و (LL) است.",
                fullContent = "اصل بنیادین تحلیل تکنیکال؛ در باینری آپشن تا زمانی که کف بالاتر (HL) شکسته نشده، روند صعودی معتبر است و باید اولویت با تریدهای CALL باشد.",
                practicalTip = "معامله در پول‌بک به کف‌های بالاتر قبلی در امتداد ترند.",
                tags = listOf("Dow Theory", "نظریه داو", "HH & HL", "ساختار ترند")
            )
            37 -> EncyclopediaItem(
                id = "s67_37", sectionNumber = 37, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۳۷ از ۶۷ • امواج انگیزشی و اصلاحی",
                title = "امواج الیوت در باینری آپشن (Elliott Waves 5-3 Pattern)",
                titleEn = "Elliott Waves Impulse & Correction in Fixed Time",
                category = "تحلیل تکنیکال کلاسیک", direction = "BOTH",
                summary = "شناسایی موج ۳ انگیزشی (طولانی‌ترین و قوی‌ترین موج) برای ورودهای تضمینی باینری آپشن.",
                fullContent = "موج ۳ الیوت دارای بیشترین شتاب و کمترین اصلاح است. ورود در ابتدای موج ۳ برای تریدهای ۵ دقیقه‌ای بالاترین حاشیه امنیتی را ایجاد می‌کند.",
                practicalTip = "ورود در شکست سقف موج ۱ برای موج‌سواری در موج ۳.",
                tags = listOf("Elliott Wave", "امواج الیوت", "موج ۳", "انگیزشی")
            )
            38 -> EncyclopediaItem(
                id = "s67_38", sectionNumber = 38, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۳۸ از ۶۷ • چرخه بازار نهادی",
                title = "چرخه انباشت و توزیع وایکوف (Wyckoff Accumulation & Distribution)",
                titleEn = "Wyckoff Schematics & Spring Setup",
                category = "پرایس اکشن پیشرفته", direction = "BOTH",
                summary = "چهار فاز اصلی بازار: انباشت (Accumulation)، صعود (Markup)، توزیع (Distribution) و نزول (Markdown).",
                fullContent = "ستاپ معروف Spring در فاز انباشت وایکوف (شکار نقدینگی کف و بازگشت سریع) یکی از بهترین موقعیت‌های معامله CALL است.",
                practicalTip = "ورود CALL بلافاصله پس از بسته شدن کندل اسپرینگ وایکوف.",
                tags = listOf("Wyckoff", "وایکوف", "انباشت و توزیع", "Spring")
            )
            39 -> EncyclopediaItem(
                id = "s67_39", sectionNumber = 39, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۳۹ از ۶۷ • کندل‌های میانگین‌گیر",
                title = "کندل‌های هیکن آشی (Heikin Ashi Smoothed Candles)",
                titleEn = "Heikin Ashi Noise-Free Candles",
                category = "ابزارهای کمکی چارت", direction = "BOTH",
                summary = "محاسبه کندل‌ها بر پایه میانگین ۴ قیمت برای حذف نویزهای بازار و تشخیص آسان امتداد روند.",
                fullContent = "کندل‌های سبز هیکن آشی بدون سایه پایینی نشانه یک روند صعودی بدون نقص هستند و سیگنال ادامه ترید CALL با ریسک پایین به شمار می‌روند.",
                practicalTip = "تغییر اولین رنگ کندل هیکن آشی زنگ خطر اتمام ترند است.",
                tags = listOf("Heikin Ashi", "هیکن آشی", "حذف نویز", "ترند واضح")
            )
            40 -> EncyclopediaItem(
                id = "s67_40", sectionNumber = 40, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۴۰ از ۶۷ • آجرهای بدون بعد زمان",
                title = "چارت‌های رنکو (Renko Brick Price Charts)",
                titleEn = "Renko Time-Independent Brick Charts",
                category = "ابزارهای کمکی چارت", direction = "BOTH",
                summary = "رسم آجرها صرفاً بر اساس میزان تغییرات قیمت (مثلاً ۱۰ پیپ) بدون دخالت عامل زمان.",
                fullContent = "چارت‌های رنکو تمام فازهای رنج فرسایشی را فیلتر کرده و خطوط حمایت/مقاومت را با دقت ریاضی به تصویر می‌کشند.",
                practicalTip = "معامله در جهت شکل‌گیری دو آجر همرنگ متوالی رنکو.",
                tags = listOf("Renko", "چارت رنکو", "آجر قیمتی", "فیلتر زمان")
            )
            41 -> EncyclopediaItem(
                id = "s67_41", sectionNumber = 41, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۴۱ از ۶۷ • بازارهای فرابورس ۲۴/۷",
                title = "سازوکار بازارهای فرابورس و الگوریتم‌های OTC (OTC Market Mechanics)",
                titleEn = "Over-The-Counter OTC Algorithms & Pricing",
                category = "بروکری و بازارهای OTC", direction = "NEUTRAL",
                summary = "نرخ‌دهی الگوریتمی در روزهای تعطیل و آخر هفته‌ها توسط بروکرهایی نظیر کوتکس و پاکت آپشن.",
                fullContent = "بازارهای OTC بر اساس الگوریتم‌های داخلی و فیدهای تجمیعی تولید می‌شوند. در این بازارها احترام به سطوح پرایس اکشن و الگوهای کندلی معمولاً با دقت بالا حفظ می‌شود اما باید از ساعات تغییر الگوریتم مطلع بود.",
                practicalTip = "در بازارهای OTC از اندیکاتورهای مومنتوم و ستاپ‌های پرایس اکشن خالص استفاده کنید.",
                tags = listOf("OTC", "فرابورس", "تعطیلات", "کوتکس", "پاکت آپشن")
            )
            42 -> EncyclopediaItem(
                id = "s67_42", sectionNumber = 42, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۴۲ از ۶۷ • زمان‌بندی دقیق ثانیه‌ای",
                title = "زمان انقضا و قانون طلایی ثانیه صفر (Expiry & 00s Timing Rule)",
                titleEn = "Expiry Optimization & Exact 00-Second Execution",
                category = "قوانین اجرای معامله", direction = "NEUTRAL",
                summary = "باز کردن پوزیشن دقیقاً در ثانیه ۰۰:۰۰ آغاز کندل جدید برای جلوگیری از هدر رفت دامنه حرکت.",
                fullContent = "ورود با تاخیر ۵ تا ۱۰ ثانیه می‌تواند بهترین تحلیل‌ها را به ضرر تبدیل کند. رعایت قانون ثانیه صفر باعث ورود در نقطه استرایک اولیه و کسب حداکثر سود می‌شود.",
                practicalTip = "ساعت سیستم و پلتفرم را با سرور جهانی NTP همگام‌سازی (Sync) نمایید.",
                tags = listOf("Expiry", "زمان انقضا", "ثانیه صفر", "اجرای دقیق")
            )
            43 -> EncyclopediaItem(
                id = "s67_43", sectionNumber = 43, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۴۳ از ۶۷ • کارمزد پنهان و حاشیه بروکر",
                title = "نرخ بازدهی و لبه آماری بروکر (Payout Percentage & Broker Edge)",
                titleEn = "Payout Ratio & Mathematical Broker Edge",
                category = "ریاضیات بازدهی", direction = "NEUTRAL",
                summary = "اختلاف بین ۱۰۰٪ و درصد پرداختی بروکر حاشیه امنیتی بروکر (House Edge) را تشکیل می‌دهد.",
                fullContent = "در پی‌اوت ۹۰٪، بروکر در هر باخت شما ۱۰۰٪ پول را برمی‌دارد اما در برد ۹۰٪ پرداخت می‌کند (۱۰٪ سود ساختاری بروکر). غلبه بر این حاشیه تنها با وین‌ریت بالای ۵۵٪ امکان‌پذیر است.",
                practicalTip = "هرگز روی دارایی با پی‌اوت زیر ۸۵٪ ترید نکنید تا لبه بروکر به حداقل برسد.",
                tags = listOf("Payout", "لبه بروکر", "House Edge", "کارمزد پنهان")
            )
            44 -> EncyclopediaItem(
                id = "s67_44", sectionNumber = 44, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۴۴ از ۶۷ • اختلاف قیمت خرید و فروش",
                title = "اسپرد و اثر آن در معاملات باینری (Spread Impact & Entry Price)",
                titleEn = "Spread & Pricing Discrepancy in Binary",
                category = "بروکری و نقدینگی", direction = "NEUTRAL",
                summary = "تاثیر اختلاف نرخ Bid و Ask در قیمت استرایک لحظه ورود در بروکرهای مختلف.",
                fullContent = "برخی بروکرها با افزایش جزئی اسپرد در زمان نوسانات، نقطه استرایک نامناسب‌تری به تریدر اختصاص می‌دهند. سیستم AI2 Risk Architect پلتفرم ما کیفیت اسپرد را لحظه‌به‌لحظه ممیزی می‌کند.",
                practicalTip = "در زمان اسپردهای غیرعادی از ورود به معاملات ۱ دقیقه‌ای خودداری کنید.",
                tags = listOf("Spread", "اسپرد", "قیمت استرایک", "Bid Ask")
            )
            45 -> EncyclopediaItem(
                id = "s67_45", sectionNumber = 45, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۴۵ از ۶۷ • تاخیر در سرور",
                title = "اسلیپیج و تاخیر در اجرای سفارش (Slippage & Execution Latency)",
                titleEn = "Execution Latency & Slippage Management",
                category = "بروکری و زیرساخت", direction = "NEUTRAL",
                summary = "اختلاف بین قیمتی که دکمه ترید را می‌زنید و قیمتی که سفارش روی سرور بروکر ثبت می‌شود.",
                fullContent = "تاخیر اینترنت یا سرور می‌تواند باعث شود پوزیشن شما چند پیپ بالاتر یا پایین‌تر باز شود. استفاده از اتصال پایدار و بروکرهای با سرورهای پرسرعت (Ultra-Fast) این ریسک را خنثی می‌کند.",
                practicalTip = "استفاده از DNS پرسرعت و سرورهای با پینگ زیر ۳۰ میلی‌ثانیه.",
                tags = listOf("Slippage", "اسلیپیج", "تاخیر اینترنت", "پینگ")
            )
            46 -> EncyclopediaItem(
                id = "s67_46", sectionNumber = 46, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۴۶ از ۶۷ • قراردادهای زمان معین",
                title = "قراردادهای زمان ثابت (Fixed-Time Trades - FTT)",
                titleEn = "Fixed-Time Trading Structure",
                category = "مفاهیم قراردادها", direction = "NEUTRAL",
                summary = "معاملاتی که انقضای آن‌ها بر اساس زمان سررسید کندل‌ها (مثلاً پایان دقیقه جاری) بسته می‌شود.",
                fullContent = "قراردادهای FTT به تریدر اجازه می‌دهند بدون نیاز به مدیریت حد ضرر در حین معامله، صرفاً نتیجه را در زمان انقضا بر اساس برتری استرایک برداشت کنند.",
                practicalTip = "تنظیم انقضا روی حالت 'انتهای کندل' (End of Candle).",
                tags = listOf("FTT", "Fixed Time", "قرارداد زمان ثابت")
            )
            47 -> EncyclopediaItem(
                id = "s67_47", sectionNumber = 47, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۴۷ از ۶۷ • آپشن‌های لمسی",
                title = "معاملات لمسی و بدون لمس (Touch / No-Touch Binary Options)",
                titleEn = "Touch and No-Touch Expiry Contracts",
                category = "مفاهیم قراردادها", direction = "BOTH",
                summary = "قراردادهایی که شرط برد آن‌ها فقط یک بار لمس شدن قیمت هدف قبل از سررسید است.",
                fullContent = "در بازارهای پرنوسان، آپشن‌های Touch در صورت انفجار قیمت بلافاصله و حتی قبل از پایان زمان به سود می‌نشینند؛ در حالی که No-Touch برای بازارهای آرام مناسب است.",
                practicalTip = "استفاده از آپشن Touch در زمان انتشار گزارش‌های مهم اقتصادی.",
                tags = listOf("Touch Options", "بدون لمس", "قرارداد لمسی")
            )
            48 -> EncyclopediaItem(
                id = "s67_48", sectionNumber = 48, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۴۸ از ۶۷ • قراردادهای کانالی",
                title = "قراردادهای درون/برون کانال (In/Out Boundary Options)",
                titleEn = "Boundary & Tunnel Binary Contracts",
                category = "مفاهیم قراردادها", direction = "NEUTRAL",
                summary = "پیش‌بینی اینکه آیا قیمت تا زمان انقضا داخل یک بازه نوسانی می‌ماند یا از آن خارج می‌شود.",
                fullContent = "در زمان فاز فشرده‌سازی بولینگر، خرید آپشن Out Boundary سود سرشاری به همراه دارد چون شکست سقف یا کف محتمل است.",
                practicalTip = "معامله In Boundary در سشن‌های آرام آسیایی.",
                tags = listOf("Boundary", "کانال نوسان", "Tunnel")
            )
            49 -> EncyclopediaItem(
                id = "s67_49", sectionNumber = 49, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۴۹ از ۶۷ • قراردادهای دیجیتال دریو",
                title = "معاملات ارقام و بالا/پایین (Digit Matches / Even-Odd in Deriv)",
                titleEn = "Deriv Digit Differ & Even-Odd Synthetics",
                category = "قراردادهای سنتتیک", direction = "NEUTRAL",
                summary = "قراردادهای بر پایه رقم آخر قیمت در شاخص‌های سنتتیک (Volatility Indices) بروکر دریو.",
                fullContent = "قراردادهای Digit Matches و Matches/Differs بر پایه مدل‌های احتمالاتی ریاضی کار می‌کنند و در پلتفرم Deriv به صورت ۲۴ ساعته فعال هستند.",
                practicalTip = "استفاده از استراتژی‌های توزیع نرمال ارقام در تایم‌فریم تیک‌چارت.",
                tags = listOf("Deriv", "Digit Options", "شاخص سنتتیک")
            )
            50 -> EncyclopediaItem(
                id = "s67_50", sectionNumber = 50, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۵۰ از ۶۷ • بورس رسمی ایالات متحده",
                title = "بورس نیدکس آمریکا و نظارت کمیسیون CFTC (Nadex Exchange)",
                titleEn = "Nadex Regulated Exchange & CFTC Compliance",
                category = "قوانین و بورس‌های بین‌المللی", direction = "NEUTRAL",
                summary = "بورس رسمی ارائه‌دهنده باینری آپشن در آمریکا تحت نظارت مستقیم کمیسیون معاملات آتی کالا.",
                fullContent = "نیدکس با سایر بروکرها متفاوت است چون نقش صرافی (Exchange) را دارد و سفارشات مستقیماً بین خریداران و فروشندگان مچ می‌شوند و بروکر طرف مقابل ترید شما نیست.",
                practicalTip = "آشنایی با مدل قیمت‌گذاری ۰ تا ۱۰۰ دلاری قراردادهای Nadex.",
                tags = listOf("Nadex", "CFTC", "بورس رسمی", "رگولاتوری آمریکا")
            )
            51 -> EncyclopediaItem(
                id = "s67_51", sectionNumber = 51, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۵۱ از ۶۷ • رویدادهای بورس شیکاگو",
                title = "قراردادهای رویدادی بورس شیکاگو (CME Event Contracts)",
                titleEn = "CME Group Event-Driven Binary Contracts",
                category = "قوانین و بورس‌های بین‌المللی", direction = "NEUTRAL",
                summary = "قراردادهای باینری تنظیم‌شده بر روی طلا، نفت، شاخص S&P 500 و بیت‌کوین در گروه CME.",
                fullContent = "بزرگ‌ترین بورس مشتقات جهان با ارائه Event Contracts استاندارد جدیدی برای باینری آپشن‌های شفاف با تسویه حساب مرکزی ایجاد کرده است.",
                practicalTip = "مناسب برای تریدرهای نهادی با ریسک و پاداش کاملاً شفاف و مشخص.",
                tags = listOf("CME", "Event Contracts", "بورس شیکاگو", "طلا و نفت")
            )
            52 -> EncyclopediaItem(
                id = "s67_52", sectionNumber = 52, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۵۲ از ۶۷ • مقررات اتحادیه اروپا",
                title = "مقررات اسما اروپا در باینری آپشن (ESMA Regulatory Framework)",
                titleEn = "ESMA Retail Investor Protection Mandates",
                category = "قوانین و بورس‌های بین‌المللی", direction = "NEUTRAL",
                summary = "قوانین سازمان اوراق بهادار و بازارهای اروپا برای حمایت از سرمایه‌گذاران خرد و شفافیت بروکرها.",
                fullContent = "مقررات ESMA بروکرها را ملزم به شفافیت در ارائه سود و زیان، حذف بونوس‌های مسدودکننده سرمایه و ممانعت از تبلیغات فریبنده کرده است.",
                practicalTip = "همواره بروکرهای دارای لایسنس معتبر بین‌المللی را برای معاملات برگزینید.",
                tags = listOf("ESMA", "رگولاتوری اروپا", "حفظ سرمایه", "شفافیت")
            )
            53 -> EncyclopediaItem(
                id = "s67_53", sectionNumber = 53, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۵۳ از ۶۷ • تشخیص فازهای ۵ گانه",
                title = "سیستم تفکیک رژیم‌های بازار (Market Regime Detection System)",
                titleEn = "5-Phase Market Regime Classification",
                category = "هوش مصنوعی و متدولوژی", direction = "NEUTRAL",
                summary = "تفکیک ۵ وضعیت: روند صعودی، روند نزولی، کانال رنج، شکست سطوح و فشردگی بدون جهت.",
                fullContent = "هر استراتژی معاملاتی فقط در رژیم بازار متناسب با خود کار می‌کند. سیستم هوش مصنوعی ما قبل از صدور سیگنال، رژیم لحظه‌ای بازار را تعیین می‌نماید.",
                practicalTip = "در فاز رنج از استراتژی‌های نوسانی و در فاز ترند از استراتژی‌های شکست استفاده کنید.",
                tags = listOf("Market Regime", "رژیم بازار", "فاز روند", "فاز رنج")
            )
            54 -> EncyclopediaItem(
                id = "s67_54", sectionNumber = 54, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۵۴ از ۶۷ • عامل تحلیل و الگوها",
                title = "عامل اول: استراتژیست هوش مصنوعی (AI1 Strategist & Regime Detector)",
                titleEn = "AI1 Neural Pattern Recognition Agent",
                category = "معماری هوش مصنوعی پلتفرم", direction = "BOTH",
                summary = "مغز متفکر تحلیل الگوهای کندلی، پرایس اکشن، واگرایی‌ها و تولید ستاپ‌های ورود لحظه‌ای.",
                fullContent = "عامل AI1 بدون خستگی بیش از ۵۰ جفت‌ارز و بازار OTC را به صورت زنده اسکن کرده و با ترکیب ریاضیات اندیکاتورها نقاط استرایک را پیشنهاد می‌دهد.",
                practicalTip = "سیگنال‌های تولیدی AI1 مستقیماً در برگه اصلی داشبورد اپلیکیشن نمایش می‌یابند.",
                tags = listOf("AI1", "استراتژیست", "تحلیل زنده", "شبکه عصبی")
            )
            55 -> EncyclopediaItem(
                id = "s67_55", sectionNumber = 55, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۵۵ از ۶۷ • عامل سنجش ریسک",
                title = "عامل دوم: معمار ریسک هوش مصنوعی (AI2 Risk Architect)",
                titleEn = "AI2 Risk, Slippage & Position Architect",
                category = "معماری هوش مصنوعی پلتفرم", direction = "NEUTRAL",
                summary = "محاسبه اسپرد، ارزیابی اسلیپیج، سنجش پی‌اوت بروکرها و تعیین امتیاز اطمینان سیگنال.",
                fullContent = "عامل AI2 محافظ سرمایه کاربر است؛ اگر اسپرد بروکر افزایش یابد یا پی‌اوت زیر ۸۵٪ باشد، سیگنال را تایید نخواهد کرد.",
                practicalTip = "بررسی نمره اطمینان (Confidence Score) اختصاص‌یافته توسط AI2 قبل از ورود.",
                tags = listOf("AI2", "مدیریت ریسک", "اسپرد", "نمره اطمینان")
            )
            56 -> EncyclopediaItem(
                id = "s67_56", sectionNumber = 56, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۵۶ از ۶۷ • عامل وتو و حاکمیت",
                title = "عامل سوم: ناظر و ممیز وتو معاملات (AI3 Governor & Auditor)",
                titleEn = "AI3 Autonomous Trade Governor & Veto",
                category = "معماری هوش مصنوعی پلتفرم", direction = "NEUTRAL",
                summary = "اختیار وتوی مطلق و صدور فرمان قطعی NO_TRADE در شرایط بحرانی و پرریسک بازار.",
                fullContent = "عامل سوم بالاترین سطح حاکمیت سیستم را دارد. حتی اگر AI1 ستاپ صادر کند، در صورت وجود ریسک خبر یا کیفیت پایین داده، AI3 سیگنال را وتو می‌کند.",
                practicalTip = "هر زمان نشان قرمز 'وتو شده توسط AI3' را دیدید، تحت هیچ شرایطی معامله باز نکنید.",
                tags = listOf("AI3", "وتو", "حاکمیت سیستم", "NO_TRADE")
            )
            57 -> EncyclopediaItem(
                id = "s67_57", sectionNumber = 57, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۵۷ از ۶۷ • الگوریتم‌های مومنتوم",
                title = "الگوریتم تشخیص شکست و روند هوشمند (Smart Trend & Breakout Algorithm)",
                titleEn = "Algorithmic Trend Expansion Logic",
                category = "هوش مصنوعی و متدولوژی", direction = "BOTH",
                summary = "پردازش حجم غیرعادی و کندل‌های مومنتومی برای شکار امواج اولیه صعود یا نزول.",
                fullContent = "این الگوریتم ورود پول‌های بزرگ به بازار را در کمتر از ۱۰ میلی‌ثانیه شناسایی کرده و سیگنال همسو با موج صادر می‌نماید.",
                practicalTip = "بهترین نتایج در ساعات همپوشانی سشن‌های لندن و نیویورک.",
                tags = listOf("Smart Trend", "شکست هوشمند", "الگوریتم معاملاتی")
            )
            58 -> EncyclopediaItem(
                id = "s67_58", sectionNumber = 58, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۵۸ از ۶۷ • تقویم رویدادهای مالی",
                title = "فیلتر هوشمند تقویم اقتصادی فارکس فکتوری (Economic Calendar Impact Filter)",
                titleEn = "Forex Factory & Bloomberg News Filter",
                category = "تحلیل فاندامنتال", direction = "NEUTRAL",
                summary = "یکپارچه‌سازی با تقویم‌های اقتصادی معتبر برای رصد شاخص‌های قرمز رنگ و پرریسک جهانی.",
                fullContent = "در زمان انتشار داده‌های اقتصادی، رفتار پرایس اکشن غیرقابل پیش‌بینی می‌شود. فیلتر خودکار تقویم اقتصادی معاملات را در این بازه‌ها مسدود می‌سازد.",
                practicalTip = "اخبار با ستاره قرمز (High Impact) را در بخش 'اخبار' اپلیکیشن پیگیری کنید.",
                tags = listOf("Economic Calendar", "تقویم اقتصادی", "فارکس فکتوری", "اخبار")
            )
            59 -> EncyclopediaItem(
                id = "s67_59", sectionNumber = 59, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۵۹ از ۶۷ • گزارش اشتغال آمریکا",
                title = "گزارش اشتغال بخش غیرکشاورزی آمریکا (Non-Farm Payrolls - NFP)",
                titleEn = "NFP Jobs Report Market Impact",
                category = "تحلیل فاندامنتال", direction = "NEUTRAL",
                summary = "انتشار در اولین جمعه هر ماه میلادی و ایجاد شدیدترین نوسانات در دلار آمریکا و طلا.",
                fullContent = "گزارش NFP جهت میان‌مدت دلار را مشخص می‌سازد. در ۳۰ دقیقه اول انتشار NFP معامله باینری ۱ دقیقه‌ای بسیار خطرناک است اما پس از آرامش اولیه روندهای فوق‌العاده‌ای شکل می‌گیرد.",
                practicalTip = "صبر کنید تا بازار جهت قطعی خود را پس از اعلام NFP پیدا کند.",
                tags = listOf("NFP", "اشتغال آمریکا", "نوسان شدید", "دلار")
            )
            60 -> EncyclopediaItem(
                id = "s67_60", sectionNumber = 60, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۶۰ از ۶۷ • شاخص تورم جهانی",
                title = "شاخص قیمت مصرف‌کننده و تورم (Consumer Price Index - CPI)",
                titleEn = "CPI Inflation Report & Fed Rate Impact",
                category = "تحلیل فاندامنتال", direction = "NEUTRAL",
                summary = "اصلی‌ترین معیار فدرال رزرو برای تصمیم‌گیری درباره نرخ بهره و نوسان‌دهنده جفت‌ارزها.",
                fullContent = "کاهش تورم CPI باعث تضعیف دلار و پرواز قیمت طلا و رمزارزها می‌شود. سیگنال‌های پلتفرم ما سناریوهای پسا-CPI را با دقت پردازش می‌کنند.",
                practicalTip = "ترید طلا (XAU/USD) پس از اعلام گزارش CPI بالاترین بازدهی را به ارمغان می‌آورد.",
                tags = listOf("CPI", "تورم", "فدرال رزرو", "طلا")
            )
            61 -> EncyclopediaItem(
                id = "s67_61", sectionNumber = 61, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۶۱ از ۶۷ • سیاست‌های پولی فدرال",
                title = "تصمیمات نرخ بهره کمیته بازار باز فدرال (FOMC Rate Decisions)",
                titleEn = "FOMC Interest Rate Policy & Press Conference",
                category = "تحلیل فاندامنتال", direction = "NEUTRAL",
                summary = "بیانیه‌های بانک مرکزی آمریکا و کنفرانس مطبوعاتی پاول که نوسانات بازار را جهت‌دهی می‌کند.",
                fullContent = "در ساعات جلسه FOMC نوسانات اسپردهای بروکرها به حداکثر می‌رسد و رعایت فیلتر No-Trade تا زمان پایان مصاحبه رئیس فدرال رزرو الزامی است.",
                practicalTip = "پیگیری زنده خلاصه تصمیمات FOMC در بخش اخبار اپلیکیشن.",
                tags = listOf("FOMC", "نرخ بهره", "فدرال رزرو", "پاول")
            )
            62 -> EncyclopediaItem(
                id = "s67_62", sectionNumber = 62, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۶۲ از ۶۷ • سیاست‌های بانک‌های مرکزی",
                title = "سیاست‌های پولی بانک مرکزی اروپا و ژاپن (ECB & BoJ Policy Decisions)",
                titleEn = "ECB & Bank of Japan Monetary Strategies",
                category = "تحلیل فاندامنتال", direction = "NEUTRAL",
                summary = "تاثیر مستقیم نرخ‌های بهره منفی/مثبت و مداخلات ارزی ژاپن بر جفت‌ارزهای EUR/USD و USD/JPY.",
                fullContent = "مداخلات ارزی بانک مرکزی ژاپن (BoJ) می‌تواند در عرض چند ثانیه کندل‌های ماروبوزو ۱۰۰ پیپی در جفت‌ارزهای ین ایجاد کند.",
                practicalTip = "فعال بودن هشدارهای وتوی سیگنال‌های USD/JPY در زمان جلسات BoJ.",
                tags = listOf("ECB", "BoJ", "بانک مرکزی ژاپن", "ین ژاپن")
            )
            63 -> EncyclopediaItem(
                id = "s67_63", sectionNumber = 63, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۶۳ از ۶۷ • ارسال نوتیفیکیشن لحظه‌ای",
                title = "معماری ارسال اعلان فوری پیام‌رسانی ابری (Firebase FCM Push Architecture)",
                titleEn = "Firebase Cloud Messaging Real-Time Signal Push",
                category = "زیرساخت فنی و ارتباطات", direction = "NEUTRAL",
                summary = "تحویل بدون تاخیر سیگنال‌های طلایی به دستگاه کاربر با اولویت بالا (High Priority).",
                fullContent = "سیستم FCM با یکپارچه‌سازی سرویس پس‌زمینه اندروید، سیگنال‌ها را در کمتر از ۱۰۰ میلی‌ثانیه با تفکیک کانال‌های صوتی اعلان به گوشی معامله‌گر می‌رساند.",
                practicalTip = "فعال نگه داشتن دسترسی نوتیفیکیشن در بخش تنظیمات اپلیکیشن برای دریافت لحظه‌ای سیگنال‌ها.",
                tags = listOf("FCM", "نوتیفیکیشن", "پوش کلود", "سرعت بالا")
            )
            64 -> EncyclopediaItem(
                id = "s67_64", sectionNumber = 64, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۶۴ از ۶۷ • پایگاه داده آفلاین محلی",
                title = "کش پایگاه‌داده آفلاین و ذخیره‌سازی محلی روم (Room Offline Persistence)",
                titleEn = "Room Database Offline Cache & Local Sync",
                category = "زیرساخت فنی و ارتباطات", direction = "NEUTRAL",
                summary = "دسترسی ۱۰۰٪ آفلاین به تاریخچه سیگنال‌ها، ژورنال معاملات، اشتراک‌ها و دانشنامه بدون نیاز به اینترنت دائمی.",
                fullContent = "پلتفرم ما با استفاده از دیتابیس Room SQLite تمام اطلاعات مهم را به صورت محلی در حافظه امن دستگاه نگهداری می‌کند تا در زمان قطعی اینترنت هیچ داده‌ای از دست نرود.",
                practicalTip = "تمام تغییرات ژورنال و سوابق شما به صورت آنی در حافظه آفلاین پایدار می‌ماند.",
                tags = listOf("Room Database", "کش آفلاین", "SQLite", "پایداری داده")
            )
            65 -> EncyclopediaItem(
                id = "s67_65", sectionNumber = 65, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۶۵ از ۶۷ • غلبه بر ترس از جا ماندن",
                title = "روانشناسی مهار فومو و طمع در بازار (FOMO & Greed Psychological Defense)",
                titleEn = "Fear Of Missing Out (FOMO) Mastery",
                category = "روانشناسی معامله‌گری", direction = "NEUTRAL",
                summary = "غلبه بر وسوسه ورود دیربهنگام به کندل‌های جهش‌یافته به دلیل ترس از جا ماندن از سود.",
                fullContent = "فومو (FOMO) معامله‌گر را وادار به ورود در سقف قیمت می‌کند؛ جایی که نهنگ‌ها در حال فروش هستند. به یاد داشته باشید بازار همیشه فرصت‌های جدید خلق خواهد کرد.",
                practicalTip = "اگر یک حرکت شارپ را از دست دادید، هرگز تعقیبش نکنید؛ منتظر تشکیل ستاپ جدید بمانید.",
                tags = listOf("FOMO", "طمع", "روانشناسی ترید", "صبر")
            )
            66 -> EncyclopediaItem(
                id = "s67_66", sectionNumber = 66, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۶۶ از ۶۷ • پیشگیری از فرسودگی ذهن",
                title = "خستگی تصمیم‌گیری و ترید بیش از حد (Overtrading & Decision Fatigue)",
                titleEn = "Overtrading & Cognitive Fatigue Shield",
                category = "روانشناسی معامله‌گری", direction = "NEUTRAL",
                summary = "محدود کردن تعداد معاملات روزانه (حداکثر ۵ تا ۱۰ معامله باکیفیت) برای حفظ تمرکز مغز.",
                fullContent = "پس از ۲ ساعت نگاه مداوم به چارت، مغز دچار فرسودگی شناختی شده و شروع به نادیده گرفتن قوانین ریسک می‌کند. تریدرهای نخبه بیش از ۲ ساعت متوالی ترید نمی‌کنند.",
                practicalTip = "قانون ۳ معامله در سشن: بعد از ۳ معامله (چه برد چه باخت) نیم ساعت استراحت کامل کنید.",
                tags = listOf("Overtrading", "خستگی ذهن", "انضباط فردی", "مدیریت زمان")
            )
            67 -> EncyclopediaItem(
                id = "s67_67", sectionNumber = 67, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل ۶۷ از ۶۷ • بهینه‌سازی مداوم وین‌ریت",
                title = "بهینه‌سازی مداوم و ممیزی سیستماتیک وین‌ریت (Continuous Win-Rate Optimization)",
                titleEn = "Systematic Win-Rate Optimization & Mastery",
                category = "استراتژی و تسلط", direction = "NEUTRAL",
                summary = "ترکیب داده‌های ژورنال شخصی با فیدبک هوش مصنوعی برای دستیابی به وین‌ریت پایدار بالای ۸۵٪.",
                fullContent = "موفقیت در باینری آپشن یک مقصد نیست بلکه یک فرایند بهینه‌سازی پیوسته است. با حذف جفت‌ارزهای کم‌بازده و تمرکز بر الگوهای با بیشترین نرخ برد شخصی، به سودآوری مرکب مستمر دست یابید.",
                practicalTip = "پایان هر هفته گزارش ژورنال ترید اپلیکیشن را بازبینی و الگوهای زیان‌ده را اصلاح نمایید.",
                tags = listOf("Win-Rate", "بهینه‌سازی مستمر", "تسلط معاملاتی", "سود مرکب")
            )
            else -> EncyclopediaItem(
                id = "s67_$index", sectionNumber = index, sectionType = "SECTION_67",
                sectionTitleBadge = "سرفصل $index از ۶۷",
                title = "سرفصل تخصصی $index",
                category = "آموزش باینری آپشن",
                summary = "مفاهیم تحلیلی و معاملاتی سرفصل شماره $index",
                fullContent = "محتوای تخصصی و استراتژی‌های اجرایی سرفصل شماره $index در باینری آپشن.",
                tags = listOf("آموزش", "باینری آپشن")
            )
        }
    }

    val allItems: List<EncyclopediaItem> = sectionSixItems + sectionThirtySixItems + sectionSixtySevenItems
}
