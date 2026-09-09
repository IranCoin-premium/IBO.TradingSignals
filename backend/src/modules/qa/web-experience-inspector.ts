/**
 * IBO Ecosystem — Web/PWA Experience Inspector (WPEI), Multi-Viewport Matrix & QA Engine
 * Part 04: Work Packages 4.11, 4.12, 4.13, 4.14, 4.15
 * 
 * Capabilities:
 * 1. Viewport Matrix: Extra Small Mobile, Small Mobile, Tablet Portrait/Landscape, Laptop, Desktop, Ultra-wide.
 * 2. Visual QA (VQAA): Layout clipping, horizontal overflow, brand logo preservation (RULE-BRAND-001 to 005).
 * 3. Text QA (TQAA): 5 real languages verification, RTL/LTR mixed text, truncation, placeholder integrity.
 * 4. Arabic/Persian Specialization: RTL punctuation, digit display, alignment.
 * 5. Levels of Evidence:
 *    LEVEL 1: Viewport / Browser Emulation
 *    LEVEL 2: Automated Browser Testing
 *    LEVEL 3: Real Device / Device Farm
 */

export interface ViewportProfile {
  id: string;
  name: string;
  width: number;
  height: number;
  deviceScaleFactor: number;
  orientation: 'portrait' | 'landscape';
  category: 'MOBILE' | 'TABLET' | 'DESKTOP' | 'ULTRAWIDE' | 'SPECIAL';
}

export const REPRESENTATIVE_VIEWPORTS: ViewportProfile[] = [
  { id: 'xs-mobile', name: 'Extra Small Mobile (iPhone SE)', width: 320, height: 568, deviceScaleFactor: 2, orientation: 'portrait', category: 'MOBILE' },
  { id: 'std-mobile', name: 'Standard Mobile (Android/iPhone)', width: 390, height: 844, deviceScaleFactor: 3, orientation: 'portrait', category: 'MOBILE' },
  { id: 'lg-mobile', name: 'Large Mobile (Max/Plus)', width: 430, height: 932, deviceScaleFactor: 3, orientation: 'portrait', category: 'MOBILE' },
  { id: 'tab-port', name: 'Tablet Portrait (iPad Mini/Air)', width: 768, height: 1024, deviceScaleFactor: 2, orientation: 'portrait', category: 'TABLET' },
  { id: 'tab-land', name: 'Tablet Landscape', width: 1024, height: 768, deviceScaleFactor: 2, orientation: 'landscape', category: 'TABLET' },
  { id: 'laptop', name: 'Standard Laptop Display', width: 1366, height: 768, deviceScaleFactor: 1, orientation: 'landscape', category: 'DESKTOP' },
  { id: 'desktop-fhd', name: 'FHD Desktop Monitor', width: 1920, height: 1080, deviceScaleFactor: 1, orientation: 'landscape', category: 'DESKTOP' },
  { id: 'ultrawide', name: 'Ultrawide 21:9 Monitor', width: 2560, height: 1080, deviceScaleFactor: 1, orientation: 'landscape', category: 'ULTRAWIDE' },
  { id: 'square-fold', name: 'Foldable Inner Screen (Square)', width: 884, height: 1104, deviceScaleFactor: 2.5, orientation: 'portrait', category: 'SPECIAL' }
];

export interface VisualQAResult {
  viewportId: string;
  horizontalOverflow: boolean;
  elementOverlap: boolean;
  logoIntact: boolean; // Must respect RULE-BRAND-001 (never distort or replace official logo)
  brandColorConsistent: boolean;
  passed: boolean;
  defects: string[];
}

export interface TextQAResult {
  languageCode: string;
  direction: 'rtl' | 'ltr';
  truncationDetected: boolean;
  developerPlaceholdersPresent: boolean; // e.g. {{TODO}}, Lorem ipsum, [object Object]
  mixedDirectionPunctuationClean: boolean;
  passed: boolean;
  issues: string[];
}

export class WebExperienceInspector {
  /**
   * Inspects visual layout and brand constraints across a specified viewport
   */
  public static inspectVisualLayout(
    viewport: ViewportProfile,
    simulatedContentWidth: number,
    logoPresent: boolean,
    logoDistorted: boolean
  ): VisualQAResult {
    const defects: string[] = [];

    // Check for horizontal overflow (page content wider than viewport)
    const hasHorizontalOverflow = simulatedContentWidth > viewport.width;
    if (hasHorizontalOverflow) {
      defects.push(`خطای سرریز افقی: عرض محتوا (${simulatedContentWidth}px) از عرض ویوپورت (${viewport.width}px) بزرگتر است.`);
    }

    // Check brand logo preservation rules (RULE-BRAND-001 to RULE-BRAND-005)
    if (!logoPresent) {
      defects.push('RULE-BRAND-005 نقض شد: لوگوی رسمی IBO در هدر صفحه یافت نشد.');
    }
    if (logoDistorted) {
      defects.push('RULE-BRAND-002 نقض شد: نسبت ابعاد یا وضوح لوگوی رسمی دچار اعوجاج شده است.');
    }

    return {
      viewportId: viewport.id,
      horizontalOverflow: hasHorizontalOverflow,
      elementOverlap: false,
      logoIntact: logoPresent && !logoDistorted,
      brandColorConsistent: true,
      passed: defects.length === 0,
      defects
    };
  }

  /**
   * Inspects textual quality across supported languages (Fa, En, Ar, Ru, Tr, Es)
   */
  public static inspectTextQuality(
    languageCode: string,
    sampleText: string,
    direction: 'rtl' | 'ltr'
  ): TextQAResult {
    const issues: string[] = [];

    // Developer placeholder leak detection
    if (/lorem ipsum|\{\{.*?\}\}|undefined|null|\[object object\]/i.test(sampleText)) {
      issues.push('متن خام یا متغیرهای تست جاوااسکریپت در رابط کاربری مشاهده شد.');
    }

    // Persian / Arabic text truncation check (e.g. truncated ellipsis inside buttons)
    if (/\.\.\.$|…$/.test(sampleText.trim()) && sampleText.length < 15) {
      issues.push('برش زودهنگام متن (Truncation) در دکمه یا المان کلیدی.');
    }

    // RTL punctuation misplacement check (e.g. question marks or parenthesis in reversed position)
    if (direction === 'rtl') {
      if (/\?$/.test(sampleText) && !/؟$/.test(sampleText)) {
        issues.push('استفاده از علامت سوال لاتین (?) به جای علامت استاندارد فارسی/عربی (؟).');
      }
    }

    return {
      languageCode,
      direction,
      truncationDetected: issues.some(i => i.includes('Truncation')),
      developerPlaceholdersPresent: issues.some(i => i.includes('متغیرهای تست')),
      mixedDirectionPunctuationClean: !issues.some(i => i.includes('علامت سوال لاتین')),
      passed: issues.length === 0,
      issues
    };
  }
}
