/**
 * IBO Ecosystem — Data Architecture, Governance, Backup & Restore Test Suite
 * Master Prompt — Part 15: Work Packages 15.01 - 15.20
 */

import { DataArchitectureGovernanceService } from '../modules/data/data-architecture.service';

describe('Part 15 — Data Architecture, Data Governance, Quality, Backup, Restore & DR', () => {

  describe('Work Package 15.01 & 15.02: Canonical Data Inventory & PII/Financial Isolation Invariants', () => {
    it('should maintain inventory with strict classification and zero AI exposure for restricted PII/financial data', () => {
      const inventory = DataArchitectureGovernanceService.listInventory();

      expect(inventory.length).toBeGreaterThanOrEqual(4);

      const piiDomain = inventory.find((d) => d.dataClassification === 'RESTRICTED_PII_FINANCIAL');
      expect(piiDomain).toBeDefined();
      expect(piiDomain?.piiFinancialExposedToAi).toBe(false); // Invariant: AI systems never see raw PII/financial data
    });
  });

  describe('Work Package 15.12 & 15.13: Actual Backup Restoration Verification Test', () => {
    it('should prove recoverability through actual restoration test with integrity verification', () => {
      const restoreTest = DataArchitectureGovernanceService.executeDataRestoreVerification({
        targetDomainId: 'DATA-TASK-ORCHESTRATION',
        snapshotTimestamp: '2026-09-07T12:00:00Z',
        recordCountToVerify: 1250
      });

      expect(restoreTest.testId).toBeDefined();
      expect(restoreTest.status).toBe('PASSED');
      expect(restoreTest.dataIntegrityCheck).toBe('INTACT');
      expect(restoreTest.recordCountVerified).toBe(1250);
      expect(restoreTest.timeToRestoreMs).toBeGreaterThan(0);
    });
  });
});
