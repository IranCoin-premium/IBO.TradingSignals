// Test environment defaults — loaded before the test modules via jest setupFiles.
// Provides a deterministic, non-production webhook signing secret for tests only.
process.env.PAYMENTS_WEBHOOK_SECRET = process.env.PAYMENTS_WEBHOOK_SECRET || 'TEST_ONLY_WEBHOOK_SECRET';

export {};
