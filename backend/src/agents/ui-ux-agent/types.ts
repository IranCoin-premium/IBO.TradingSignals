/**
 * UI/UX Agent Type Definitions
 * Part 06: UI/UX Continuous Evolution & 10-Level Quality Gate
 */

export interface BrandAssetConfig {
  version: string;
  protected_brand_assets: string[];
  brand_colors: Record<string, string>;
  mandatory_disclosures: {
    trading_risk_fa: string;
    trading_risk_en: string;
  };
}

export type UIChangeType =
  | 'STYLE_DRIFT'
  | 'MODAL_CONVERSION'
  | 'SPACING_ADJUSTMENT'
  | 'ANIMATION_REFINEMENT'
  | 'ACCESSIBILITY_FIX'
  | 'TYPOGRAPHY_ALIGNMENT';

export type LevelTestStatus =
  | 'PENDING'
  | 'TESTING_LEVEL_1_3'
  | 'TESTING_LEVEL_4_6'
  | 'TESTING_LEVEL_7_9'
  | 'TESTING_LEVEL_10'
  | 'PASSED_LEVEL_10'
  | 'FAILED_ROLLED_BACK'
  | 'CANCELLED_MAX_RETRIES_EXCEEDED';

export interface UIChangeProposal {
  componentTarget: string;
  changeType: UIChangeType;
  targetFiles: string[];
  diffSummary: string;
  styleDriftPercentage: number; // Max allowed e.g. 8.0%
  beforeState: Record<string, string>; // filePath -> content
  afterState: Record<string, string>;  // filePath -> content
}

export interface LevelTestResult {
  level: number;
  levelName: string;
  passed: boolean;
  details: string;
  metricValue?: number;
  error?: string;
}

export interface TenLevelRunResult {
  overallPassed: boolean;
  failedLevel?: number;
  failedReason?: string;
  levelResults: LevelTestResult[];
}

export interface UIChangeRecord {
  id: string;
  auditLogId: string;
  agentId: string;
  componentTarget: string;
  changeType: UIChangeType;
  diffSummary: string;
  styleDriftPercentage: number;
  levelTestStatus: LevelTestStatus;
  failedLevel?: number;
  retryCount: number;
  maxRetries: number;
  rolledBack: boolean;
  rollbackReason?: string;
  beforeState: Record<string, string>;
  afterState: Record<string, string>;
  testRunDetails: Record<string, any>;
  humanReviewRequired: boolean;
  humanReviewStatus: 'UNREVIEWED' | 'APPROVED' | 'REJECTED';
  createdAt: Date;
  updatedAt: Date;
}
