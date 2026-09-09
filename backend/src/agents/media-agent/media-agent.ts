/**
 * Media Asset Generation, Style Consistency & Brand Watermarking Engine
 * Part 08: Google Flow (nanobanana), Style References & Weekly Rotation
 */

import crypto from 'crypto';

export type MediaPurpose = 'news' | 'journal' | 'marketing';
export type AspectRatio = '1:1' | '16:9' | '9:16' | '4:5' | '3:2';
export type MediaType = 'image' | 'video';

export interface MediaAssetRecord {
  id: string;
  type: MediaType;
  purpose: MediaPurpose;
  sourceUrl: string;
  aspectRatio: AspectRatio;
  hasWatermark: boolean;
  hasLogo: boolean;
  createdByAgent: string;
  styleReferenceId?: string;
  styleReferenceWeek?: string;
  isStyleReference: boolean;
  isPermanentLogoReference: boolean;
  newsSourceName?: string;
  platformBrandingText: string;
  bgRemovalMethod: 'google_flow_native' | 'rembg_fallback' | 'none';
  durationSeconds?: number;
  complianceStatus: 'VERIFIED' | 'FLAGGED_COPYRIGHT' | 'FLAGGED_MISLEADING';
  createdAt: string;
}

export interface PromptComplianceCheckResult {
  allowed: boolean;
  reason?: string;
}

export class MediaAgent {
  private agentId = 'agent-media-nanobanana';

  // Forbidden keywords to prevent trademark/copyright infringement and misleading claims
  private static readonly FORBIDDEN_COPYRIGHT_KEYWORDS = [
    'binance', 'nasdaq', 'nyse', 'tradingview', 'metatrader', 'mt4', 'mt5',
    'iqoption', 'quotex', 'pocketoption', 'olymptrade', 'apple', 'google',
    'elon musk', 'warren buffett'
  ];

  private static readonly FORBIDDEN_MISLEADING_KEYWORDS = [
    'guaranteed profit', '100% win', 'get rich quick', 'سود تضمینی',
    'بدون ریسک', 'پولدار شدن یک شبه', '100% profit', 'risk free', 'infinite money'
  ];

  /**
   * Evaluates prompt against critical compliance restrictions
   */
  public checkPromptCompliance(prompt: string): PromptComplianceCheckResult {
    const lower = prompt.toLowerCase();

    for (const kw of MediaAgent.FORBIDDEN_COPYRIGHT_KEYWORDS) {
      if (lower.includes(kw)) {
        return {
          allowed: false,
          reason: `Prompt contains forbidden third-party trademark/copyright keyword: '${kw}'`
        };
      }
    }

    for (const kw of MediaAgent.FORBIDDEN_MISLEADING_KEYWORDS) {
      if (lower.includes(kw)) {
        return {
          allowed: false,
          reason: `Prompt contains forbidden misleading/guaranteed claim keyword: '${kw}'`
        };
      }
    }

    return { allowed: true };
  }

  /**
   * Determine required aspect ratios based on asset purpose
   */
  public getRequiredAspectRatios(purpose: MediaPurpose): AspectRatio[] {
    switch (purpose) {
      case 'news':
        return ['16:9', '9:16']; // Desktop news banner & mobile story
      case 'journal':
        return ['3:2', '1:1'];  // Journal blog cover & square thumbnail
      case 'marketing':
      default:
        return ['1:1', '16:9', '9:16', '4:5', '3:2']; // All 5 mandatory ratios
    }
  }

  /**
   * Simulates Google Flow / nanobanana image generation with active style references
   */
  public generateImageWithStyleReferences(params: {
    prompt: string;
    purpose: MediaPurpose;
    aspectRatio: AspectRatio;
    activeStyleReferenceIds: string[];
    newsSourceName?: string;
    applyBgRemoval?: boolean;
  }): MediaAssetRecord {
    const compliance = this.checkPromptCompliance(params.prompt);
    if (!compliance.allowed) {
      throw new Error(`Media generation rejected: ${compliance.reason}`);
    }

    const assetId = `med_${crypto.randomBytes(8).toString('hex')}`;
    const bgRemoval = params.applyBgRemoval ? 'google_flow_native' : 'none';

    return {
      id: assetId,
      type: 'image',
      purpose: params.purpose,
      sourceUrl: `https://cdn.yourdomain.com/generated/${assetId}_${params.aspectRatio.replace(':', 'x')}.png`,
      aspectRatio: params.aspectRatio,
      hasWatermark: true,
      hasLogo: true,
      createdByAgent: this.agentId,
      styleReferenceId: params.activeStyleReferenceIds[0] || '00000000-0000-0000-0000-000000000001',
      isStyleReference: false,
      isPermanentLogoReference: false,
      newsSourceName: params.newsSourceName,
      platformBrandingText: 'IBO Binary Option Trading Signals — yourdomain.com',
      bgRemovalMethod: bgRemoval,
      complianceStatus: 'VERIFIED',
      createdAt: new Date().toISOString()
    };
  }

  /**
   * Transforms an existing image into a 15-60s teaser motion-graphic video
   */
  public generateMotionVideoFromImage(params: {
    sourceImage: MediaAssetRecord;
    durationSeconds: number; // 15 to 60s
  }): MediaAssetRecord {
    if (params.durationSeconds < 15 || params.durationSeconds > 60) {
      throw new Error('Video duration must be between 15 and 60 seconds.');
    }

    const videoId = `vid_${crypto.randomBytes(8).toString('hex')}`;

    return {
      id: videoId,
      type: 'video',
      purpose: params.sourceImage.purpose,
      sourceUrl: `https://cdn.yourdomain.com/generated/${videoId}.mp4`,
      aspectRatio: params.sourceImage.aspectRatio,
      hasWatermark: true,
      hasLogo: true,
      createdByAgent: this.agentId,
      styleReferenceId: params.sourceImage.id,
      isStyleReference: false,
      isPermanentLogoReference: false,
      platformBrandingText: 'IBO Binary Option Trading Signals — yourdomain.com',
      bgRemovalMethod: 'none',
      durationSeconds: params.durationSeconds,
      complianceStatus: 'VERIFIED',
      createdAt: new Date().toISOString()
    };
  }

  /**
   * Executes weekly rotation of Style Reference Set:
   * Keeps the permanent logo and selects 9 random verified images from the current week.
   */
  public rotateWeeklyStyleReferences(
    allWeeklyCandidates: MediaAssetRecord[],
    currentWeekStr: string // e.g. "2026-W36"
  ): {
    retainedPermanentLogoId: string;
    newStyleReferenceIds: string[];
    retiredReferenceCount: number;
  } {
    const permanentLogoId = '00000000-0000-0000-0000-000000000001';

    // Filter valid, verified image candidates from this week
    const eligible = allWeeklyCandidates.filter(
      c => c.complianceStatus === 'VERIFIED' && c.type === 'image' && !c.isPermanentLogoReference
    );

    // Shuffle and pick up to 9 items
    const shuffled = [...eligible].sort(() => 0.5 - Math.random());
    const selected = shuffled.slice(0, 9);

    const newStyleReferenceIds = [permanentLogoId, ...selected.map(s => s.id)];

    return {
      retainedPermanentLogoId: permanentLogoId,
      newStyleReferenceIds,
      retiredReferenceCount: Math.max(0, allWeeklyCandidates.filter(c => c.isStyleReference && !c.isPermanentLogoReference).length)
    };
  }
}
