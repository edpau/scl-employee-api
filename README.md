## 🛠️ Database Setup

Make sure MySQL is running, then create the database:

```sql
CREATE DATABASE scl_employee_db
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```
This ensures support for all Unicode characters (e.g. emojis, multilingual text).

[//]: # (TODO)
talk about how to link up with Mysql, local and production

---

### 🧠 Why `id` Uses `Int` in This Project

For this learning project, `id` fields are defined as `Int` for simplicity and readability.  
In production systems, `Long` (`BIGINT`) is typically used to support very large datasets and prevent overflow.  
This decision was intentional to keep the code lightweight while focusing on architecture and clean layering.

---

## 🛠️ Post-MVP Improvements (Planned)
- [ ] Implement archiving (soft delete) for employees instead of hard deletion