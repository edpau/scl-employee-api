## Initialized Backend Project
[Start project with starter template](https://github.com/nology-tech/getting-started-guides/tree/main/play/starter-template)

## Database Configuration
- Renamed default playdb to scl_employee_db
- Created local MySQL database with modern UTF-8 support:
```sql
CREATE DATABASE scl_employee_db
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```
> Using `utf8mb4` ensures full Unicode support, including emojis and multi-language characters — a good baseline for production-quality APIs.

```bash
curl http://localhost:9000/
```
If everything’s working
```json
{"message":"Hello, Play + Slick + MySQL (Scala 2.13)!"}
```

[//]: # (TODO)
curl for POST, PATCH, or DELETE

## ✅ Git Ignore Setup

- Cleaned up `.vscode/`, `.metals/`, and `.bloop/`
- Updated `.gitignore` to reflect IntelliJ + Scala workflow
- Ignoring unnecessary editor-specific and build artifacts