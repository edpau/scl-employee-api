## 🛠️ Database Setup

Make sure MySQL is running, then create the database:

```sql
CREATE DATABASE scl_employee_db
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

This ensures support for all Unicode characters (e.g. emojis, multilingual text).