# 📜 Contract Rules – Employment Domain

This file captures all the business rules I defined before starting the Contract feature. These help ensure data consistency and reflect real-world employment logic.

---

## 🧍 Example Case: Peter

Peter joins the company with no prior contract. His situation:

| Contract # | Start Date | End Date   | Type      | Status     | Hours/Week |
|------------|------------|------------|-----------|------------|-------------|
| 1          | 2025-08-01 | 2026-08-01 | Contract  | Part-time  | 20          |

Once Peter has an existing contract, I must **check for overlaps** before adding a new one.

---

## ❓ Key Questions & Clarified Answers

### ❓ Can an employee have multiple contracts?

Yes — but **not overlapping**. Over time, an employee might switch roles or types, resulting in multiple contracts. Only **one contract can be active at a time**.

### ❓ Can contract end dates be updated?

Yes — end dates can be updated. Initially, I will allow any update. In future, I may restrict this action to higher roles (e.g. manager-only).

### ❓ Can someone be part-time and permanent?

Yes — part-time refers to hours, not permanence. A person can be part-time permanent staff.

### ❓ Can permanent contracts have end dates?

Usually no. They are open-ended. If someone leaves, I will **add an end date later** to mark that. Permanent contracts should **start without** an end date.

---

## ✅ Contract Validation Rules

### 🔹 Date Rules

| Rule                                                                 | Applies To              |
|----------------------------------------------------------------------|--------------------------|
| `start_date` must be **before** `end_date` (if end exists)          | All contracts            |
| `start_date` cannot equal `end_date`                                | All contracts            |
| `start_date` or `end_date` may be in the past (to backdate)         | Allowed for now          |
| Permanent contracts must have `end_date = NULL`                     | Permanent only           |
| No overlap with existing contracts                                  | All contracts            |

> Overlap logic: if any `existing.start <= new.end && existing.end >= new.start`, it's invalid.

---

### 🔹 Hours/Week Rules

| Rule                                      | Applies To   |
|-------------------------------------------|--------------|
| Must be > 0                               | All          |
| Upper bound (e.g. ≤ 40) optional          | Optional     |
| < 35 hours allowed for part-time          | Part-time    |
| ≥ 35 hours expected for full-time         | Full-time    |

---

### 🔹 Type & Combination Validity

| Field             | Rule                                           |
|-------------------|------------------------------------------------|
| `contract_type`   | Must be `"permanent"` or `"contract"`          |
| `employment_type` | Must be `"full-time"` or `"part-time"`         |
| Combination       | All combinations are valid                     |

---

## 🧾 Special Case: When Someone Leaves

If a permanent employee leaves:
- I can **edit** the contract to add an `end_date`.
- This **closes** their active contract.
- It allows new contracts to be added in future.

---

## ✅ Summary

| Rule Type    | Description                                                               |
|--------------|---------------------------------------------------------------------------|
| Dates        | Must be in valid sequence, no overlaps, permanent has no end date         |
| Hours        | Must be > 0, full-time/part-time optional enforcement                     |
| Validations  | All fields must be trimmed and checked for emptiness where applicable     |

This gives me a strong base for implementing my Contract domain in a real-world-proof way.