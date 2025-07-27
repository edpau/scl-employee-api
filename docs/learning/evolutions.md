# 📘 Evolutions

## ✅ What is Play Evolutions?
- A **lightweight migration tool** built into Play.
- Lets you write raw SQL in `conf/evolutions/default/1.sql`.
- Runs SQL on app startup to **create or update database schema**.

## ⚙️ How Play Uses It
- **First Time**: Runs all `.sql` scripts to build schema (e.g. `employees` table).
- **Second Time**: Skips re-running if nothing changes.
- If file changes: Play **detects it**, shows a web warning before applying.
- But if schema is changed **outside Play** (e.g. directly in DB), Play can't detect it → use **Flyway or Liquibase** for complex cases.

## 🔍 What Play Can Detect
1. New SQL File Added (e.g., 2.sql)
   - ✅ If not yet applied (based on play_evolutions table), Play applies it.
   - ✅ If already applied, Play skips it.
2. Existing SQL File Is Modified (e.g., change content in 1.sql)
   - ⚠️ Play detects the change by checking the hash stored in play_evolutions.
   - It shows a browser warning page asking you to manually apply or force it (⚠️ use with caution).
   
 ## 🔒 What Play Cannot Detect
- ❌ If someone manually changes the database schema directly (e.g., runs ALTER TABLE in MySQL Workbench), Play has no way to detect this.
- That’s why:
  - ✅ You should only apply schema changes through evolutions.
  - ✅ Use Flyway or Liquibase for teams or production apps that need to track external changes.

## 🔁 `!Ups` and `!Downs`
- `!Ups`: SQL to **migrate forward** (e.g. create table).
- `!Downs`: SQL to **rollback** (e.g. drop table).
- Use rollback during:
  - Manual testing
  - Migration errors

🧠 **Migrate** = move from one DB version to another (forward or backward).

---

## 🪜 Step-by-Step: Run Evolution

### 1. ✅ Enable Evolutions in `application.conf`

```hocon
play.evolutions.enabled = true
play.evolutions.db.default.autoApply = true
```

> Ensures SQL runs automatically.

### 2. ✅ Start App

```bash
sbt run
```

Then trigger with browser or:

```bash
curl http://localhost:9000/
```

### 3. ✅ Check DB

```sql
SHOW TABLES;
```

You should see:

```
employees
play_evolutions
```

Now confirm your schema:

```sql
DESCRIBE employees;
```
