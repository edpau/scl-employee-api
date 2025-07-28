## Initialized Backend Project
- [Start project with starter template](https://github.com/nology-tech/getting-started-guides/tree/main/play/starter-template)
- 📘 Full writeup: [Starter Template – Folder & File Guide](./learning/starter-template-notes.md)

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

## 🔐 Application Secret (Play Security)
Instead of hardcoding secrets (like play.http.secret.key) directly in application.conf, I followed Play’s recommendation to inject secrets through environment variables.
This improves:
- Security (no secrets in Git)
- Flexibility (per-env configs: dev vs prod)
Example in application.conf:
```hocon
play.http.secret.key = "changeme"
play.http.secret.key = ${?APPLICATION_SECRET}
```
Then in terminal:
```bash
APPLICATION_SECRET="mysupersecret" sbt run
```
📘 Full writeup: [Managing the Application Secret with Environment Variables](./learning/secrets-env-setup.md)

## Evolutions
-- Evolution 1: Create `employees` table
Summary: [Evolutions – What I Explored So Far](./learning/evolutions.md)

## Create Employee Model + EmployeeTable.scala

- [Why Use `Option[LocalDateTime] = None` Instead of `Timestamp` in Scala Domain Models](./learning/why-localdatetime-over-timestamp.md)
- [Understanding the Three `Option[...] = None` Fields in Domain Model](./learning/option-fields-explained.md)

## 