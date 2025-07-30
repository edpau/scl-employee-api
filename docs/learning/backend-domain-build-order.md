# 🧭 Backend Domain Layer: Step-by-Step Build Order

This guide explains the **recommended order** to build your domain-based backend layer. Each step builds on the one before it, forming a clean and testable architecture.

1️⃣ Model → 2️⃣ Table → 3️⃣ Repository → 4️⃣ DTO → 5️⃣ Controller
---

## ✅ Step 1: Domain Model (e.g. `Employee.scala`)

### Why it's first:

- Defines the **core concept** of your app (e.g. an Employee, Task, Product)
- Pure Scala — no framework or database dependencies
- Everything else depends on this shape

### Example:

```scala
case class Employee(
  id: Option[Long],
  firstName: String,
  lastName: String,
  ...
)
```

---

## ✅ Step 2: Slick Table Mapping (e.g. `EmployeeTable.scala`)

### Why now:

- Tells Slick how to map your domain model to a DB table
- Required to insert, read, or update records
- Used by the repository in the next step

### What it does:

- Maps each field to a column
- Defines primary key, auto-increment, nullable fields
- Enables Slick to read/write case class objects

---

## ✅ Step 3: Repository (e.g. `EmployeeRepository.scala`)

### Why here:

- Depends on the Slick table to query/insert data
- Encapsulates all DB logic
- Keeps service layer clean and testable

### What it does:

- `insert`, `findById`, `findAll`, `update`, `delete`, etc.
- Calls `db.run(...)` with Slick queries

---

## ✅ Step 4: DTOs (e.g. `EmployeeDTO.scala`)

### Why after core logic:

- Used only when sending/receiving data via HTTP
- Converts between raw JSON and your domain model
- Keeps your API contract separate from internal logic

### What they do:

- Input DTOs (e.g. `CreateEmployeeDto`) → convert to domain
- Output DTOs (e.g. `EmployeeResponseDto`) ← convert from domain

---

## ✅ Step 5: Controller (e.g. `EmployeeController.scala`)

### Why last:

- Depends on service and DTOs
- Handles HTTP, not business or DB logic
- Converts input to DTO → calls service → returns JSON result

### What it does:

- Accepts requests (e.g. `POST /employees`)
- Parses JSON into DTO
- Returns `Ok`, `Created`, `BadRequest`, etc.

---

## ✅ Summary Table

| Step                  | Order | Why                                         |
| --------------------- | ----- | ------------------------------------------- |
| `Employee.scala`      | 1st   | Core domain model                           |
| `EmployeeTable.scala` | 2nd   | Required to interact with DB                |
| `EmployeeRepository`  | 3rd   | Needs the table to query/insert             |
| `EmployeeDTO.scala`   | 4th   | Only needed when sending/receiving JSON     |
| `EmployeeController`  | 5th   | Needs service, which needs repository/table |

---

This order helps you build in clean layers, where each layer depends **only on the one below it**, making the system easier to reason about, test, and maintain.
