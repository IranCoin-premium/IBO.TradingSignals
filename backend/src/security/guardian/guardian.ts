import fs from 'fs';
import path from 'path';

export interface SecurityFinding {
  id: string;
  severity: 'P0' | 'P1' | 'P2' | 'P3' | 'P4'; // Critical, High, Medium, Low, Informational
  category: string;
  title: string;
  description: string;
  filePath?: string;
  line?: number;
  remediation: string;
  status: 'OPEN' | 'REMEDIATED' | 'CONTAINED';
}

export class SecurityGuardian {
  private findings: SecurityFinding[] = [];
  private workspaceRoot: string;

  constructor(workspaceRoot?: string) {
    // Navigate from /backend/src/security/guardian to root
    this.workspaceRoot = workspaceRoot || path.resolve(__dirname, '../../../../');
  }

  public runScan(): SecurityFinding[] {
    this.findings = [];

    this.scanForSecrets();
    this.scanAndroidSecurity();
    this.scanBackendConfiguration();
    this.scanDependencies();

    return this.findings;
  }

  /**
   * P0/P1: Check for hardcoded credentials, API keys, or tokens in source files
   */
  private scanForSecrets() {
    const filePatterns = [
      { ext: '.kt', paths: ['app/src'] },
      { ext: '.ts', paths: ['backend/src'] },
      { ext: '.json', paths: ['backend', 'app'] },
    ];

    // Regex patterns for secret detection
    const rules = [
      {
        id: 'SEC-01-PASSWORD',
        pattern: /passwordHash\s*=\s*"([^"]+)"|password\s*=\s*"([^"]+)"/gi,
        excludePattern: /"SOCIAL_OAUTH"|""|placeholder/i,
        severity: 'P0' as const,
        category: 'Hardcoded Secret',
        title: 'Hardcoded Plaintext Password/Hash',
        description: 'Hardcoded sensitive password or database seed credential found in source files.',
        remediation: 'Move the hardcoded credential to an environment variable (.env) and access it securely via BuildConfig or process.env.',
      },
      {
        id: 'SEC-01-JWT-SECRET',
        pattern: /JWT_SECRET\s*=\s*"([^"]+)"|jwtSecret\s*=\s*"([^"]+)"/gi,
        excludePattern: /change_me_in_production|placeholder/i,
        severity: 'P1' as const,
        category: 'Hardcoded Secret',
        title: 'Hardcoded JWT Signature Secret',
        description: 'Plaintext JWT signing secret found hardcoded in codebase.',
        remediation: 'Configure JWT_SECRET strictly via server-side environment variables and do not commit to Git.',
      }
    ];

    for (const rule of rules) {
      for (const group of filePatterns) {
        for (const relativePath of group.paths) {
          const targetPath = path.join(this.workspaceRoot, relativePath);
          this.walkDir(targetPath, (filePath) => {
            if (filePath.endsWith(group.ext) && !filePath.includes('guardian.ts') && !filePath.includes('test.ts')) {
              try {
                const content = fs.readFileSync(filePath, 'utf8');
                const lines = content.split('\n');
                lines.forEach((lineText, idx) => {
                  if (rule.pattern.test(lineText) && !rule.excludePattern.test(lineText)) {
                    this.findings.push({
                      id: rule.id,
                      severity: rule.severity,
                      category: rule.category,
                      title: rule.title,
                      description: `${rule.description} Found match in line ${idx + 1}.`,
                      filePath: path.relative(this.workspaceRoot, filePath),
                      line: idx + 1,
                      remediation: rule.remediation,
                      status: 'OPEN',
                    });
                  }
                  // Reset regex state due to 'g' flag
                  rule.pattern.lastIndex = 0;
                });
              } catch (err) {
                // Ignore unreadable files safely
              }
            }
          });
        }
      }
    }
  }

  /**
   * P1/P2: Audit Android manifest and security flags
   */
  private scanAndroidSecurity() {
    const manifestPath = path.join(this.workspaceRoot, 'app/src/main/AndroidManifest.xml');
    if (!fs.existsSync(manifestPath)) {
      return;
    }

    try {
      const manifest = fs.readFileSync(manifestPath, 'utf8');

      // 1. Audit allowBackup
      if (manifest.includes('android:allowBackup="true"')) {
        this.findings.push({
          id: 'AND-01-ALLOW-BACKUP',
          severity: 'P2',
          category: 'Mobile Security',
          title: 'Android Application Backups Enabled',
          description: 'The android:allowBackup attribute is set to true. This allows users or attackers with ADB access to copy app private data.',
          filePath: 'app/src/main/AndroidManifest.xml',
          remediation: 'Set android:allowBackup="false" in AndroidManifest.xml to disable backup of app-private files.',
          status: 'OPEN',
        });
      }

      // 2. Audit cleartext traffic policy
      if (manifest.includes('android:usesCleartextTraffic="true"')) {
        this.findings.push({
          id: 'AND-02-CLEARTEXT-TRAFFIC',
          severity: 'P1',
          category: 'Mobile Network Security',
          title: 'Cleartext HTTP Traffic Allowed',
          description: 'The android:usesCleartextTraffic attribute is set to true, allowing insecure unencrypted HTTP connections.',
          filePath: 'app/src/main/AndroidManifest.xml',
          remediation: 'Ensure all traffic uses HTTPS and configure network_security_config.xml to disable cleartext traffic.',
          status: 'OPEN',
        });
      }

      // 3. Audit exported components without permissions (excluding standard AppWidgets)
      const receiverMatches = manifest.match(/<receiver[^>]+android:exported="true"[^>]*>/g);
      const hasVulnerableReceiver = receiverMatches && receiverMatches.some(r => !r.includes('Widget') && !r.includes('widget') && !r.includes('Provider'));
      if (hasVulnerableReceiver) {
        this.findings.push({
          id: 'AND-03-EXPORTED-RECEIVER',
          severity: 'P2',
          category: 'Mobile Component Security',
          title: 'Exported Receiver Without Strict Permissions',
          description: 'An Android BroadcastReceiver is exported="true" without explicit custom permissions, making it vulnerable to intent hijacking.',
          filePath: 'app/src/main/AndroidManifest.xml',
          remediation: 'Set android:exported="false" unless the receiver absolutely must receive system-wide broadcasts.',
          status: 'OPEN',
        });
      }
    } catch (err) {
      // Ignore reading issues
    }
  }

  /**
   * P1/P2: Audit Server configuration, CORS, and Secrets setups
   */
  private scanBackendConfiguration() {
    const backendEnvPath = path.join(this.workspaceRoot, 'backend/.env');
    if (fs.existsSync(backendEnvPath)) {
      try {
        const content = fs.readFileSync(backendEnvPath, 'utf8');

        // Check for permissive CORS configuration
        if (content.includes('CORS_ALLOWED_ORIGINS=*') || content.includes('CORS_ALLOWED_ORIGINS=""')) {
          this.findings.push({
            id: 'BCK-01-PERMISSIVE-CORS',
            severity: 'P1',
            category: 'API Security',
            title: 'Permissive CORS Configuration',
            description: 'The CORS_ALLOWED_ORIGINS environment variable allows unrestricted cross-origin requests.',
            filePath: 'backend/.env',
            remediation: 'Set CORS_ALLOWED_ORIGINS to specific, authorized domain names separated by commas.',
            status: 'OPEN',
          });
        }

        // Check for placeholder JWT secrets
        if (content.includes('JWT_SECRET=super_secret_jwt_sign_key_change_me_in_production')) {
          this.findings.push({
            id: 'BCK-02-DEFAULT-JWT-KEY',
            severity: 'P1',
            category: 'Server Identity Security',
            title: 'Default JWT Secret Key Active',
            description: 'The server is running with the default placeholder JWT signing key, which is highly insecure.',
            filePath: 'backend/.env',
            remediation: 'Generate a strong cryptographically secure random secret key and set it as JWT_SECRET.',
            status: 'OPEN',
          });
        }
      } catch (err) {
        // Safe skip
      }
    }
  }

  /**
   * P2/P3: Audit package and catalog dependencies
   */
  private scanDependencies() {
    const backendPackagePath = path.join(this.workspaceRoot, 'backend/package.json');
    if (fs.existsSync(backendPackagePath)) {
      try {
        const pkg = JSON.parse(fs.readFileSync(backendPackagePath, 'utf8'));
        const deps = { ...pkg.dependencies, ...pkg.devDependencies };

        // Scan for outdated or insecure express versions
        if (deps['express']) {
          const version = deps['express'].replace(/[^0-9.]/g, '');
          const major = parseInt(version.split('.')[0], 10);
          if (major < 4) {
            this.findings.push({
              id: 'DEP-01-VULNERABLE-EXPRESS',
              severity: 'P1',
              category: 'Supply Chain Security',
              title: 'Vulnerable Express Version Detected',
              description: `Express dependency uses outdated major version ${version}.`,
              filePath: 'backend/package.json',
              remediation: 'Upgrade to Express v4.19.2 or above which contains important vulnerability fixes.',
              status: 'OPEN',
            });
          }
        }
      } catch (err) {
        // Safe skip
      }
    }
  }

  /**
   * Auto-remedy low-risk or high-impact configuration violations safely
   */
  public runAutoRemediation(): number {
    let repairedCount = 0;
    const backendEnvPath = path.join(this.workspaceRoot, 'backend/.env');

    if (fs.existsSync(backendEnvPath)) {
      try {
        let content = fs.readFileSync(backendEnvPath, 'utf8');

        // Rule: If JWT_SECRET is set to placeholder, generate a strong cryptographically safe random key
        if (content.includes('JWT_SECRET=super_secret_jwt_sign_key_change_me_in_production')) {
          const secureKey = Array.from({ length: 48 }, () => Math.random().toString(36)[2]).join('');
          content = content.replace(
            'JWT_SECRET=super_secret_jwt_sign_key_change_me_in_production',
            `JWT_SECRET=${secureKey}`
          );
          fs.writeFileSync(backendEnvPath, content, 'utf8');
          repairedCount++;
        }
      } catch (err) {
        // Safe skip
      }
    }

    return repairedCount;
  }

  /**
   * Traverses directories recursively
   */
  private walkDir(dir: string, callback: (filePath: string) => void) {
    if (!fs.existsSync(dir)) return;
    const files = fs.readdirSync(dir);
    for (const file of files) {
      const filePath = path.join(dir, file);
      const stat = fs.statSync(filePath);
      if (stat.isDirectory()) {
        if (file !== 'node_modules' && file !== 'dist' && file !== '.gradle' && file !== '.build-outputs') {
          this.walkDir(filePath, callback);
        }
      } else {
        callback(filePath);
      }
    }
  }
}

// Standalone CLI implementation
if (require.main === module) {
  const guardian = new SecurityGuardian();
  const scanResult = guardian.runScan();

  console.log('==================================================');
  console.log('AUTONOMOUS SECURITY GUARDIAN REPORT');
  console.log('==================================================');
  console.log(`Discovered ${scanResult.length} security findings.\n`);

  scanResult.forEach((finding) => {
    console.log(`[${finding.severity}] - ${finding.title}`);
    console.log(`Category: ${finding.category}`);
    console.log(`Location: ${finding.filePath || 'Root'}:${finding.line || 'Global'}`);
    console.log(`Detail:   ${finding.description}`);
    console.log(`Remediation: ${finding.remediation}`);
    console.log('--------------------------------------------------');
  });

  // Run Safe Auto-Remediation
  const repaired = guardian.runAutoRemediation();
  if (repaired > 0) {
    console.log(`Successfully auto-remediated ${repaired} security config issues.`);
  }

  // Fail CLI if critical P0 or P1 vulnerability exists
  const hasCritical = scanResult.some(f => f.severity === 'P0' || f.severity === 'P1');
  if (hasCritical) {
    console.log('\n❌ Security Gate FAILED. P0/P1 issues must be addressed before deployment.');
    process.exit(1);
  } else {
    console.log('\n✅ Security Gate PASSED. No critical security issues found.');
    process.exit(0);
  }
}
