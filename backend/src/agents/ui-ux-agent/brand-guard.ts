/**
 * Brand Protection & Policy Guard
 * Enforces Hardcoded Inviolable Constraints 1 & 2 for Part 06
 */
import fs from 'fs';
import path from 'path';
import { BrandAssetConfig, UIChangeProposal } from './types';

export class BrandProtectionGuard {
  private brandConfig: BrandAssetConfig;

  constructor(customConfigPath?: string) {
    const configPath = customConfigPath || path.resolve(process.cwd(), '../brand-assets.json');
    if (fs.existsSync(configPath)) {
      this.brandConfig = JSON.parse(fs.readFileSync(configPath, 'utf8'));
    } else {
      // Fallback in-memory config if running in test environment
      this.brandConfig = {
        version: '1.0.0',
        protected_brand_assets: [
          'app/src/main/res/mipmap-*/ic_launcher*',
          'app/src/main/res/drawable/ic_ibo_logo.xml',
          'app/src/main/res/drawable/ibo_brand_watermark.xml',
          'public/logo.png',
          'public/logo.svg',
          'public/favicon.ico',
          'public/assets/branding/*',
          'backend/src/assets/logo.png',
          'brand-assets.json'
        ],
        brand_colors: {
          primary: '#0A2540',
          accent_gold: '#FFB800',
          bull_green: '#00C087',
          bear_red: '#FF3B30'
        },
        mandatory_disclosures: {
          trading_risk_fa: 'این سیگنال‌ها توصیه مالی نیستند؛ معاملات باینری آپشن ریسک بالای از دست دادن سرمایه دارد.',
          trading_risk_en: 'These signals do not constitute financial advice; binary options trading carries a high risk of capital loss.'
        }
      };
    }
  }

  /**
   * Validate that none of the target files match protected brand assets
   */
  public validateProposal(proposal: UIChangeProposal): { allowed: boolean; violation?: string } {
    // 1. Inviolable Rule 1: Never touch brand assets or logo files
    for (const file of proposal.targetFiles) {
      const normalized = file.replace(/\\/g, '/');
      for (const pattern of this.brandConfig.protected_brand_assets) {
        const regexPattern = new RegExp('^' + pattern.replace(/\*/g, '.*') + '$');
        if (regexPattern.test(normalized) || normalized.includes('logo') || normalized.includes('ic_launcher')) {
          return {
            allowed: false,
            violation: `VETO: Modification of protected brand asset '${file}' is strictly prohibited by System Constraint 1.`
          };
        }
      }
    }

    // 2. Inviolable Rule 2: Never remove mandatory risk disclosures
    const mandatoryRiskText = this.brandConfig.mandatory_disclosures.trading_risk_fa;
    for (const [file, oldContent] of Object.entries(proposal.beforeState)) {
      const newContent = proposal.afterState[file];
      if (oldContent && oldContent.includes(mandatoryRiskText)) {
        if (!newContent || !newContent.includes(mandatoryRiskText)) {
          return {
            allowed: false,
            violation: `VETO: Proposed modification removes mandatory risk disclosure from '${file}'. Rejected by System Constraint 2.`
          };
        }
      }
    }

    // 3. Inviolable Rule 4: Drift containment ceiling
    if (proposal.styleDriftPercentage > 8.0) {
      return {
        allowed: false,
        violation: `VETO: Style drift (${proposal.styleDriftPercentage}%) exceeds the maximum allowed threshold (8.0%). Large visual leaps are strictly prohibited.`
      };
    }

    return { allowed: true };
  }

  public getMandatoryRiskDisclosure(): string {
    return this.brandConfig.mandatory_disclosures.trading_risk_fa;
  }
}
