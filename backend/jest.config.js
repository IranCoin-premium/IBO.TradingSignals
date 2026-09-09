module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'node',
  testMatch: ['**/*.test.ts'],
  verbose: true,
  forceExit: true,
  clearMocks: true,
  resetMocks: true,
  restoreMocks: true,
  // Test-only webhook signing secret (injected to tests via env — never hardcoded
  // in application code; production value comes from the deployment environment).
  setupFiles: ['<rootDir>/src/tests/test-env.ts']
};
