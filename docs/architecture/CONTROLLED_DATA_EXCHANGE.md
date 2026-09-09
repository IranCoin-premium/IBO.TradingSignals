# IBO Project — Controlled Data Exchange Architecture
**Document Version:** 1.0.0 — Phase 2  
**Scope:** Master Prompt Part 2 — Work Package 2.6  

---

## 1. The Principle of Controlled Exchange

In the IBO ecosystem, agents, sub-agents, workflows, and tools must collaborate without behaving as isolated islands. However:
> **"Interaction does not mean unrestricted access."**

### Forbidden Patterns:
- ❌ **Direct Memory Mutation:** Agent A directly altering the internal state or database tables of Agent B.
- ❌ **Uncontrolled Dialogues:** Open-ended, unstructured LLM-to-LLM chatter loops without terminal criteria.
- ❌ **Secret Backchannels:** Undocumented instructions or hidden context exchanges that bypass the audit log.
- ❌ **Permission Transitivity:** Agent A delegating its elevated credentials to a lower-privileged worker.

### Required Pattern:
```
+-----------------------------------------------------------------------------+
|                                    EVENT                                    |
| (Structured, auditable fact: e.g. visual_issue_detected)                    |
+-----------------------------------------------------------------------------+
                                      |
                                      v
+-----------------------------------------------------------------------------+
|                                    TASK                                     |
| (Bounded unit of work: e.g. review_rtl_direction assigned to VTQI)         |
+-----------------------------------------------------------------------------+
                                      |
                                      v
+-----------------------------------------------------------------------------+
|                               ASSIGNED WORKER                               |
| (Executes with least-privilege tools: read source, compute diff, test)     |
+-----------------------------------------------------------------------------+
                                      |
                                      v
+-----------------------------------------------------------------------------+
|                              STRUCTURED RESULT                              |
| (JSON record containing findings, tests_run, evidence, confidence, next_step)|
+-----------------------------------------------------------------------------+
                                      |
                                      v
+-----------------------------------------------------------------------------+
|                         VERIFICATION & NEXT ACTION                          |
| (Automated gate validates result -> spawns follow-up task or closes event)  |
+-----------------------------------------------------------------------------+
```

---

## 2. Structured Result Format Standard

All workers must emit results adhering to the `StructuredWorkerResult` specification (`backend/src/events/task-schema.ts`):

```json
{
  "taskId": "task-8a9b1c-4421-b0e1",
  "workerId": "VTQI-RTL-Inspector",
  "resultStatus": "RETRY_NEEDED",
  "summary": "Persian numeric label clipping detected in SubscriptionScreen card header",
  "findings": [
    {
      "ruleId": "RULE-VTQI-004",
      "severity": "MEDIUM",
      "description": "Text component width constrained to 120dp causes Persian numeral truncation",
      "targetComponent": "SubscriptionScreen.CardHeader",
      "targetFile": "app/src/main/java/com/example/ui/screens/SubscriptionScreen.kt",
      "suggestedFix": "Replace fixed width with wrap_content / Modifier.widthIn(min = 140.dp)"
    }
  ],
  "changedResources": [],
  "evidence": {
    "detectedWidth": 120,
    "requiredWidth": 138,
    "locale": "fa-IR"
  },
  "testsRun": ["bash scripts/verify-ui-uniformity.sh"],
  "testResults": {
    "passed": false,
    "passedTests": 3,
    "failedTests": 1,
    "outputLogSummary": "UI Uniformity check: truncated text block detected"
  },
  "confidence": 0.95,
  "recommendedNextAction": "REQUEST_DEV_FIX"
}
```

---

## 3. Data Minimization & Security Invariants

1. **Payload Sanitization:** No credentials, access tokens, or sensitive user PII may appear in event payloads or task records.
2. **Auditability:** Every handoff is persisted with a UUID correlation ID connecting the initiating event, the executed task, and the structured result.
3. **Fail-Closed on Unknown Format:** Results that fail JSON schema validation are automatically rejected and marked `BLOCKED`.
