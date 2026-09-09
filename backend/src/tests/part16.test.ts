/**
 * IBO Ecosystem — Product Analytics, Behavior Intelligence & Experimentation Test Suite
 * Master Prompt — Part 16: Work Packages 16.01 - 16.20
 */

import { ProductAnalyticsEngine } from '../modules/analytics/product-analytics.service';

describe('Part 16 — Product Analytics, Behavior Intelligence, Experimentation & KPI Governance', () => {

  describe('Work Package 16.01, 16.02, 16.05: Analytics Ingestion, KPI Dictionary & Privacy/Consent Guard', () => {
    it('should seed a canonical KPI dictionary with defined metrics and target thresholds', () => {
      const kpis = ProductAnalyticsEngine.listKPIs();
      expect(kpis.length).toBeGreaterThanOrEqual(4);

      const retentionKpi = kpis.find((k) => k.kpiId === 'KPI-RETENTION-D30');
      expect(retentionKpi).toBeDefined();
      expect(retentionKpi?.targetThreshold).toBe(40.0);
    });

    it('should successfully track events when user consent is granted', () => {
      const trackResult = ProductAnalyticsEngine.trackEvent({
        eventName: 'signal_clicked',
        userId: 'user-123',
        tenantId: 'tenant-456',
        userConsentGranted: true,
        properties: { signalId: 'sig-789', asset: 'BTC/USD' }
      });

      expect(trackResult.accepted).toBe(true);
      expect(trackResult.eventId).toBeDefined();
      expect(trackResult.reason).toBe('Analytics event recorded');
    });

    it('should strictly skip and reject tracking when user consent is not granted', () => {
      const trackResult = ProductAnalyticsEngine.trackEvent({
        eventName: 'signal_clicked',
        userId: 'user-123',
        tenantId: 'tenant-456',
        userConsentGranted: false,
        properties: { signalId: 'sig-789', asset: 'BTC/USD' }
      });

      expect(trackResult.accepted).toBe(false);
      expect(trackResult.eventId).toBeUndefined();
      expect(trackResult.reason).toBe('Consent denied by user privacy setting');
    });
  });

  describe('Work Package 16.10, 16.11: Controlled Experimentation & Guardrail Breach Rollbacks', () => {
    it('should initialize experiments and record allocations correctly', () => {
      const exp = ProductAnalyticsEngine.createExperiment({
        name: 'Persian RTL Disclosure Layout Optimizations',
        primaryKpiId: 'KPI-CONVERSION-PRO',
        guardrailMaxErrorRate: 0.05, // 5% max allowed error rate on variant rendering
        variants: [
          { variantId: 'A_baseline', name: 'Standard Disclosure', allocationPercentage: 50, conversionCount: 0, sampleCount: 0 },
          { variantId: 'B_expanded_rtl', name: 'RTL Legal Risk Line Highlighted', allocationPercentage: 50, conversionCount: 0, sampleCount: 0 }
        ]
      });

      expect(exp.experimentId).toBeDefined();
      expect(exp.status).toBe('RUNNING');

      // Record a normal conversion sample
      const recorded = ProductAnalyticsEngine.recordExperimentSample(exp.experimentId, 'B_expanded_rtl', true, false);
      expect(recorded).toBe(true);

      const updatedExp = ProductAnalyticsEngine.getExperiment(exp.experimentId);
      const varB = updatedExp?.variants.find((v) => v.variantId === 'B_expanded_rtl');
      expect(varB?.sampleCount).toBe(1);
      expect(varB?.conversionCount).toBe(1);
    });

    it('should trigger automated ROLLED_BACK state if variant error rate breaches guardrail threshold', () => {
      const exp = ProductAnalyticsEngine.createExperiment({
        name: 'Experimental Trading Terminal Header',
        primaryKpiId: 'KPI-SIGNAL-ACCURACY',
        guardrailMaxErrorRate: 0.10, // 10% max allowed error rate
        variants: [
          { variantId: 'A_baseline', name: 'Baseline Header', allocationPercentage: 50, conversionCount: 0, sampleCount: 0 },
          { variantId: 'B_header_fancy', name: 'Interactive Graph Header', allocationPercentage: 50, conversionCount: 0, sampleCount: 0 }
        ]
      });

      // Record normal sample
      ProductAnalyticsEngine.recordExperimentSample(exp.experimentId, 'B_header_fancy', false, false);
      expect(exp.status).toBe('RUNNING');

      // Record sample with failure/error
      ProductAnalyticsEngine.recordExperimentSample(exp.experimentId, 'B_header_fancy', false, true);
      expect(exp.status).toBe('ROLLED_BACK');
      expect(exp.concludedAt).toBeDefined();
    });
  });

  describe('Work Package 16.14: Product Insight Intelligence Generator', () => {
    it('should generate product insight reports containing evidence, hypotheses, and uncertainties', () => {
      const insights = ProductAnalyticsEngine.generateInsights();
      expect(insights.length).toBeGreaterThanOrEqual(1);

      const primaryInsight = insights[0];
      expect(primaryInsight.title).toBe('Persian RTL Mobile Conversion Efficiency Advantage');
      expect(primaryInsight.evidenceData).toContain('M5 Binary signal page conversion');
      expect(primaryInsight.hypothesis).toContain('RTL Persian legal risk disclaimer transparency');
      expect(primaryInsight.uncertainties.length).toBeGreaterThanOrEqual(1);
      expect(primaryInsight.confidenceScore).toBe(0.89);
    });
  });
});
