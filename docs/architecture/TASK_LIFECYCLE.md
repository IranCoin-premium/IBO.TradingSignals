# IBO Project — Task Lifecycle & State Machine Architecture
**Document Version:** 1.0.0 — Phase 1  
**Scope:** Master Prompt Part 1 — Work Package 8  

---

## 1. Task Lifecycle States

Every task assigned to an automated agent or pipeline worker must progress through a deterministic, auditable finite state machine (FSM).

```
   [ NEW ]
      |
      v
  [ QUEUED ]
      |
      v
  [ RUNNING ] -------------------+
      |                          |
      v                          v
 [ VERIFYING ]              [ BLOCKED ] (Missing credentials/input)
   |       |                     |
   |       +--- Fail ----+       v
   |                     |  [ ESCALATED ] (Human operator required)
   v (Pass)              v
[ SUCCESS ]          [ RETRY ] (Max 3 attempts)
                         |
                         v (Attempts exhausted)
                     [ FAILED ]
                         |
                         v
                    [ ESCALATED ]
```

---

## 2. State Descriptions & Transition Invariants

| State | Invariant & Operational Rules | Next Permitted States |
|---|---|---|
| **NEW** | Task submitted and validated against input schema. No side effects have occurred. | `QUEUED`, `BLOCKED` |
| **QUEUED** | Task assigned a queue priority and awaiting agent worker allocation. | `RUNNING`, `BLOCKED` |
| **RUNNING** | Agent or worker is actively analyzing files or generating patches. | `VERIFYING`, `BLOCKED`, `FAILED` |
| **VERIFYING** | Mandatory automated test gate executing (`jest`, `compile_applet`, etc.). | `SUCCESS`, `RETRY`, `FAILED` |
| **SUCCESS** | All verification tests passed 100%. Patch committed or artifact generated. | Terminal state |
| **RETRY** | Intermediate failure detected; automatic rollback applied, context refreshed. Retry counter incremented. | `RUNNING`, `FAILED` |
| **BLOCKED** | Execution paused due to external dependency, missing permission, or ambiguity. System resources released. | `QUEUED`, `ESCALATED` |
| **FAILED** | Task failed and exceeded maximum automatic retry attempts (max 3). Clean rollback enforced. | `ESCALATED` |
| **ESCALATED** | High-priority notification sent to human admin/CEO with diagnostic bundle. Awaiting manual resolution. | `QUEUED`, Terminal |

---

## 3. Failure Containment & Non-Blocking Isolation

### The Isolation Law:
> **"A failed or blocked agent task must NEVER crash the API service, lock the database, or stop other independent queues."**

1. **State Independence:** Each task has its own isolated context and execution timeout (default: 300 seconds).
2. **Resource Boundaries:** Worker memory and CPU are strictly bounded; hanging processes are terminated via timeout watchdog.
3. **Clean Rollbacks:** If a task enters `FAILED` or `RETRY`, any temporary git branches or staging tables are discarded.
