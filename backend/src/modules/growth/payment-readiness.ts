/**
 * IBO Ecosystem — Payment Readiness Intelligence Agent (PRIA) & 90th Minute Handoff
 * Part 03: Work Packages 3.10 & 3.11
 * 
 * Rules:
 * 1. PRIA NEVER automatically opens merchant accounts or submits false information.
 * 2. PRIA distinguishes Merchant Eligibility from Customer Payment Acceptance.
 * 3. PRIA prioritizes official payment provider documentation.
 * 4. 90th Minute Rule: Automation stops at KYC / Identity / Banking / Legal boundary.
 * 5. Prepares Human Action Package in Persian for the human owner.
 */

export type PaymentReadinessStatus =
  | 'RESEARCH'
  | 'READY'
  | 'HUMAN_ACTION_REQUIRED'
  | 'NOT_AVAILABLE'
  | 'UNKNOWN';

export interface PaymentAssessment {
  market: string;
  provider: string;
  officialSourceDoc: string;
  customerPaymentMethods: string[];
  merchantEligibility: 'CONFIRMED' | 'REQUIRES_KYC_VERIFICATION' | 'RESTRICTED';
  technicalIntegrationStatus: 'READY' | 'UNDER_DEVELOPMENT' | 'BLOCKED';
  status: PaymentReadinessStatus;
  blockerDescription?: string;
  nextAction: string;
}

export interface HumanActionPackage {
  packageId: string;
  market: string;
  providerName: string;
  officialPortalUrl: string;
  whyHumanActionRequired: string;
  completedBySystem: string[];
  remainingTasksForOwner: string[];
  persianInstructions: {
    stepTitle: string;
    description: string;
    requiredDocuments: string[];
    securityWarning: string;
    forbiddenToShareWithAi: string[];
    howToConfirmCompletion: string;
  };
  notificationMetadata: {
    notificationCount: number;
    lastNotificationTimestamp: string;
    readyForDispatch: boolean;
  };
}

export class PaymentReadinessAgent {
  /**
   * Evaluates a payment integration opportunity and triggers the 90th minute handoff if KYC/Legal is reached
   */
  public static assessPaymentGateway(
    market: string,
    provider: string,
    officialDocUrl: string,
    requiresKycOrLegalSign: boolean
  ): { assessment: PaymentAssessment; humanActionPackage?: HumanActionPackage } {
    // Check if official documentation is supplied
    if (!officialDocUrl || !officialDocUrl.startsWith('https://')) {
      return {
        assessment: {
          market,
          provider,
          officialSourceDoc: officialDocUrl || 'NONE',
          customerPaymentMethods: [],
          merchantEligibility: 'RESTRICTED',
          technicalIntegrationStatus: 'BLOCKED',
          status: 'RESEARCH',
          blockerDescription: 'RULE-PAYMENT-001 VIOLATION: Payment readiness requires verified official HTTPS documentation.',
          nextAction: 'Obtain official provider integration API and merchant compliance guidelines.'
        }
      };
    }

    if (requiresKycOrLegalSign) {
      const pkgId = `hap-${market.toLowerCase()}-${provider.toLowerCase()}-${Date.now()}`;
      const humanPkg: HumanActionPackage = {
        packageId: pkgId,
        market,
        providerName: provider,
        officialPortalUrl: officialDocUrl,
        whyHumanActionRequired: 'احراز هویت بیومتریک و بارگذاری مدارک شرکتی/هویتی شخص مالک طبق قوانین بین‌المللی مبارزه با پولشویی (KYC/AML) صرفاً باید توسط انسان انجام پذیرد.',
        completedBySystem: [
          'بررسی فنی و تطبیق وب‌هوک‌های کریپتو و امضای دیجیتال (HMAC)',
          'پیاده‌سازی متدهای ایجاد Intent پرداخت در بک‌اند',
          'بررسی تطبیق ارزهای پایه (USDT-TRC20, TON, Web3)'
        ],
        remainingTasksForOwner: [
          'ورود مستقیم به پورتال رسمی درگاه با مرورگر امن',
          'تکمیل فرآیند احراز هویت شرکتی/شخصی',
          'دریافت Merchant API Key و ثبت امن آن در Secrets Panel'
        ],
        persianInstructions: {
          stepTitle: `تکمیل فعال‌سازی درگاه ${provider} برای بازار ${market}`,
          description: `کلیه تنظیمات نرم‌افزاری و شبیه‌سازی وب‌هوک‌های امنیتی انجام شده است. اکنون نیاز است مالک پروژه از طریق لینک رسمی اقدام به احراز هویت نماید.`,
          requiredDocuments: [
            'پاسپورت معتبر یا کارت شناسایی بین‌المللی',
            'گواهی نشانی تجاری یا سکونت (Proof of Address)',
            'شماره حساب یا ولت سازمانی تسویه‌حساب'
          ],
          securityWarning: 'هشدار امنیتی: رمز عبور، کلمات بازیابی ولت (Seed Phrase) و کدهای تایید دو مرحله‌ای (MFA/2FA) تحت هیچ شرایطی نباید در چت با هوش مصنوعی یا فایل‌های ریپازیتوری وارد شوند.',
          forbiddenToShareWithAi: [
            'رمز عبور پنل درگاه',
            'کلمات بازیابی کیف پول (12/24 Secret Words)',
            'کدهای پیامک یا Google Authenticator',
            'عکس مدارک شناسایی خصوصی'
          ],
          howToConfirmCompletion: 'پس از دریافت کلید API رسمی، آن را مستقیماً در پنل متغیرهای محیطی یا سکرت‌های سرور وارد کرده و پیام «درگاه فعال شد» را اعلام فرمایید.'
        },
        notificationMetadata: {
          notificationCount: 1,
          lastNotificationTimestamp: new Date().toISOString(),
          readyForDispatch: true
        }
      };

      return {
        assessment: {
          market,
          provider,
          officialSourceDoc: officialDocUrl,
          customerPaymentMethods: ['USDT_TRC20', 'USDT_TON', 'CRYPTO'],
          merchantEligibility: 'REQUIRES_KYC_VERIFICATION',
          technicalIntegrationStatus: 'READY',
          status: 'HUMAN_ACTION_REQUIRED',
          blockerDescription: 'Merchant KYC & Identity verification boundary reached.',
          nextAction: 'Handoff to human owner with Persian instructions package.'
        },
        humanActionPackage: humanPkg
      };
    }

    return {
      assessment: {
        market,
        provider,
        officialSourceDoc: officialDocUrl,
        customerPaymentMethods: ['USDT_TRC20'],
        merchantEligibility: 'CONFIRMED',
        technicalIntegrationStatus: 'READY',
        status: 'READY',
        nextAction: 'Proceed with operational testing in sandbox.'
      }
    };
  }
}
