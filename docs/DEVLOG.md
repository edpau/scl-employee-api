# Devlog
## Resource
- [Slick 3.0.0 manual](https://scala-slick.org/doc/3.0.0/index.html)
- [play-scala-slick-example](https://github.com/playframework/play-samples/tree/3.0.x/play-scala-slick-example)
- [Rock the JVM - Slick Tutorial with Scala](https://www.youtube.com/watch?v=Uwqf_8nwYN4&list=PLmtsMNDRU0BxIFCdPgm77Dx5Ll9SQ76pR)
- [Introduction to Slick](https://www.baeldung.com/scala/slick-intro#:~:text=Slick%20is%20a%20Functional%20Relational,SQL%2C%20thus%20providing%20typesafe%20queries.)
- [play-slick3-steps example](https://github.com/pedrorijo91/play-slick3-steps/tree/master)

-[Backend Domain Layer: Step-by-Step Build Order](./learning/backend-domain-build-order.md)

---
## Converting Between LocalDate and SQL Date
- [Converting Between LocalDate and SQL Date](https://www.baeldung.com/java-convert-localdate-sql-date)

---
## Initialized Backend Project
- [Start project with starter template](https://github.com/nology-tech/getting-started-guides/tree/main/play/starter-template)
- 📘 Full writeup: [Starter Template – Folder & File Guide](./learning/starter-template-notes.md)

---
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

---
## ✅ Git Ignore Setup

- Cleaned up `.vscode/`, `.metals/`, and `.bloop/`
- Updated `.gitignore` to reflect IntelliJ + Scala workflow
- Ignoring unnecessary editor-specific and build artifacts

---
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

--- 
## Evolutions
-- Evolution 1: Create `employees` table
Summary: [Evolutions – What I Explored So Far](./learning/evolutions.md)
- [Why I Use DB-Managed Timestamps (`createdAt` / `updatedAt`)](./learning/db_managed_timestamps_note.md)

---
## Create Employee Model

- [What Is Employee.scala (Domain Model)](./learning/what-is-employee-model.md)

- [Why Use `Option[LocalDateTime] = None` Instead of `Timestamp` in Scala Domain Models](./learning/why-localdatetime-over-timestamp.md)
- [Understanding the Three `Option[...] = None` Fields in Domain Model](./learning/option-fields-explained.md)

## Create EmployeeTable (Slick Table)
- [What Is `EmployeeTable.scala` (Slick Table Mapping)](./learning/what-is-employee-table.md)

- <> is a Slick method called <> (named with angle brackets))
  - It stands for: "Map between a tuple and a case class — both ways"
  - <> (Employee.tupled, Employee.unapply)
  - "Here's how to turn tuples into Employee, and how to turn Employee back into tuples."
- Rep[...] stands for: "Representation" of a database value
  - def firstName: Rep[String] = column[String]("first_name")
  - “This is not just a String in Scala — it’s a reference to a DB column that contains strings.”

### Docs
- [Slick - Schemas](https://scala-slick.org/doc/3.3.3/schemas.html)
- [ProvenShape docs in Slick](https://scala-slick.org/doc/3.3.3/api/index.html#slick.lifted.ProvenShape@mapTo%5BU%5D:slick.lifted.MappedProjection%5BU,T%5D)
- [Slick - Getting Started](https://scala-slick.org/doc/prerelease/gettingstarted.html?utm_source=chatgpt.com)
- [Slick - MySQLProfile.api](https://scala-slick.org/doc/3.5.0-M4/api/slick/jdbc/MySQLProfile.html?utm_source=chatgpt.com)

## Create Repository, EmployeeRepository.scala
- [What Is `EmployeeRepository.scala`](./learning/what-is-employee-repository.md)
- @ annotation syntax
- @Singleton, tells Play's dependency injection system: “Only create one instance of this class for the entire application.”
- @Inject, tells Play’s dependency injection system: “Automatically provide the required dependencies when creating this class.”
- class CategoryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)
  - “When Play starts the app, it should create this class, and automatically give it a working DatabaseConfigProvider.”
- (implicit ec: ExecutionContext)
  - “Also make sure this class has access to a thread pool for running asynchronous code.”
  - Play automatically provides a thread pool (ExecutionContext)
- Slick queries return Future[...], and we run them like:
```scala
db.run(query)  // returns Future[Result]
```
Under the hood, that needs a thread pool to work.
So without implicit ec, db.run(...) calls would not compile.

### Docs
- [Play - DatabaseConfigProvider](https://www.playframework.com/documentation/2.8.x/api/scala/play/api/db/slick/DatabaseConfigProvider.html?utm_source=chatgpt.com)
- [Play Slick Documentation](https://www.playframework.com/documentation/2.8.x/PlaySlick#Getting-the-DatabaseConfig)
- [Slick Queries documentation](https://scala-slick.org/doc/3.0.0/queries.html?utm_source=chatgpt.com)

## Create EmployeeController
- [What Is `EmployeeController.scala`](./learning/what-is-employee-controller.md)

- [How I Wrote EmployeeController Without a Service Layer](./learning/employee-controller-no-service.md)
### Referenced from Play sample repo:
- [/play-scala-slick-example/Person](https://github.com/playframework/play-samples/blob/2.7.x/play-scala-slick-example/app/models/Person.scala)
- [/play-scala-slick-example/PersonController](https://github.com/playframework/play-samples/blob/2.7.x/play-scala-slick-example/app/controllers/PersonController.scala)
### Implicit JSON Formatter
To make Play convert Employee to JSON automatically, I added this inside the companion object:
```scala
import play.api.libs.json.{Json, OFormat}

object Employee {
  implicit val format: OFormat[Employee] = Json.format[Employee]
}
```
- Play uses implicit Writes or Format to serialize objects to JSON.
  Case classes don’t automatically provide this — but Json.format[T] generates it for you.
- Json.format[T] generates both a Reads and Writes for the Employee case class.
- OFormat[T] = Reads[T] + Writes[T], required by Play’s Json.toJson(...) method.
- This allows Play to serialize and deserialize Employee automatically without writing custom logic.
> Without this, Play will throw a compile error when trying to convert Employee to JSON.

## Update route
### Docs
- [play - HTTP routing](https://www.playframework.com/documentation/3.0.x/ScalaRouting)

## Handling `createdAt` and `updatedAt` Timestamps in Play + Slick
- [Full writeup](./learning/handling-timestamps-play-slick.md)

## Create EmployeeDtos
- [What Is `EmployeeDTO.scala`](./learning/what-is-employee-dto.md)
- [Timestamp to LocalDateTime Mapping in DTOs](./learning/timestamp_to_localDateTime_note.md)
- [Power of `fromModel`: Why It's More Than Just Copying](./learning/power_of_fromModel_note.md)

## Create EmployeeService
- [What Is `EmployeeService.scala`](./learning/what_is_employeeService.md)
- getAllEmployees()

## Create utils/ ApiError
- [Understanding `ApiError` in Scala Play Framework](./learning/understanding_apiError_note.md)

## Create EmployeeValidator
- [What Is `Validator.scala`](./learning/validator_note.md)

## Create an `Employee` via POST `/employees
- [Understanding the `def create` Insert Method in Slick](./learning/slick_insert_create_method.md)
- [Debug Note: SQLIntegrityConstraintViolationException on created_at](./learning/employee_create_timestamp_debug_note.md)

---
## Contract
- [Contract Rules – Employment Domain](./learning/contract-rules.md)
- [My Decision: From `SetNull` to `Cascade`, and Later to Archiving (Soft Delete)](./learning/contract-deletion-strategy.md)
- [Patch Update Notes – Contract vs Employee](./learning/patch-update-notes.md)

---
## DataSeeder and Startup
- [Learning Note: Understanding `DataSeeder` and `Startup` in Play Framework](./learning/data-seeder-startup-note.md)

---

## return 409 DuplicateEmail with field error on unique email violation
- [Learning Log: return 409 DuplicateEmail](./learning/log_return_409_DuplicateEmail.md)