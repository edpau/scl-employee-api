# Returning 409 DuplicateEmail (with field error) on unique email violations

## 0) What I had **before** (and why it hurt)

**Flow (happy path):**
- **Controller** (`Action.async(parse.json)`) parsed JSON → `CreateEmployeeDto`.
- **Service** returned `Future[Either[ApiError, EmployeeResponse]]`.
- **Repository** ran a Slick insert → DB generated `id` → success bubbled back.
- **Controller** mapped `Right(response)` → `201 Created` JSON.

**Flow (duplicate email):**
- **DB** (MySQL) enforced a unique index on `employees.email`.
- On a duplicate, MySQL raised an error (1062 / SQLSTATE 23000).
- **JDBC driver** exposed that as `SQLIntegrityConstraintViolationException`.
- Slick returned a **failed Future**.
- My **Service** only had `.map(...)`; it didn’t have `.recover(...)`, so the failure **skipped the map** and bubbled up.
- My **Controller** also had `.map { case Right/Left }`, which is **success-only**, so it **never ran** either.
- Play got a **failed `Future[Result]`** from the action and called the **default error handler**, which produced a **500 HTML** page.
- My frontend did `await response.json()` → **“Unexpected token '<'”** (because the body was HTML).

**Pain:** ugly 500s, broken JSON parsing on the client, and no field-level message the UI could use.

---

## 1) Why I chose **409 Conflict** here

- **409 Conflict** literally means a request conflicts with the current state of the resource. A typical example is a **unique constraint violation**.
- Using 409 tells clients: “The shape of your JSON was fine, but this **value clashes** with existing data.”
- It’s semantically better than 400 (validation/shape) or 500 (server crash).
- For my **internal** employee-admin app, it’s OK to say “Email already in use.” (user enumeration risk is not a concern here).

---

## 2) What each layer **takes and returns** (the types)

- **Controller**  
  - **Takes:** `Request[JsValue]` (because of `parse.json`)  
  - **Returns:** `Future[Result]` (Play sends the `Result` when the future completes)
- **Service**  
  - **Takes:** `CreateEmployeeDto`  
  - **Returns:** `Future[Either[ApiError, EmployeeResponse]]`  
    - `Right(EmployeeResponse)` = success  
    - `Left(ApiError)` = domain/API error (not an HTTP `Result`)
- **Repository**  
  - **Takes:** `InsertEmployee`  
  - **Returns:** `Future[Employee]` (domain model)  
  - On DB problems, the `Future` can **fail** with a `Throwable` (e.g., `SQLException`)
- **DB via JDBC**  
  - Enforces unique constraints; on duplicates emits 1062 / "23000"; JDBC driver throws a `SQLIntegrityConstraintViolationException`.

**Key idea:** `map` only runs on **success**. To turn failures into values, I must use **`.recover`**.

---

## 3) What I changed in code (the small, targeted bits)

### 3.1 I named the unique constraint (so I can detect it reliably)

**Evolution `3.sql`:**
```sql
# --- !Ups
ALTER TABLE employees
  DROP INDEX email,   -- my auto-generated unique index name was 'email'
  ADD CONSTRAINT uq_employees_email UNIQUE (email);

# --- !Downs
ALTER TABLE employees
  DROP INDEX uq_employees_email,
  ADD CONSTRAINT email UNIQUE (email);
```

Why: MySQL includes the **constraint/index name** in error messages. A **stable, explicit** name (`uq_employees_email`) makes detection robust.

---

### 3.2 I added a specific API error (409 + field error)

**`utils/ApiError.scala`**
```scala
case object DuplicateEmail extends ApiError {
  val message: String = "Email already in use."
  def toResult: Result =
    Results.Conflict(
      Json.obj(
        "error" -> message,
        "validation_errors" -> Json.obj("email" -> "Already in use")
      )
    )
}
```

This gives the frontend:
```json
{
  "error": "Email already in use.",
  "validation_errors": { "email": "Already in use" }
}
```

---

### 3.3 I translated the JDBC exception → `ApiError.DuplicateEmail` in the **Service**

**`employee/EmployeeService.scala`** (excerpt)
```scala
def createEmployee(data: CreateEmployeeDto): Future[Either[ApiError, EmployeeResponse]] = {
  val errors = EmployeeValidator.validateCreate(data)
  if (errors.nonEmpty) {
    Future.successful(Left(ApiError.ValidationError(errors)))
  } else {
    val insert = InsertEmployee(
      firstName    = data.firstName.trim,
      lastName     = data.lastName.trim,
      email        = data.email.trim,
      mobileNumber = data.mobileNumber.map(_.trim).filter(_.nonEmpty),
      address      = data.address.trim
    )

    employeeRepository.create(insert)
      .map(saved => Right(EmployeeResponse.fromModel(saved)))
      .recover {
        case ex if isMySqlDuplicateEmail(ex) =>
          Left(ApiError.DuplicateEmail) // <- clean 409 path
        case _ =>
          Left(ApiError.InternalServerError("Unexpected error"))
      }
  }
}
```

**Helpers in the service** (kept private to avoid leaking infra concerns elsewhere):
```scala
import scala.annotation.tailrec

@tailrec
private def unwrapSql(t: Throwable): Option[java.sql.SQLException] = t match {
  case sql: java.sql.SQLException => Some(sql)
  case null                       => None
  case other                      => unwrapSql(other.getCause)
}

private val EmailUniqueConstraintName = "uq_employees_email"

private def isMySqlDuplicateEmail(t: Throwable): Boolean =
  unwrapSql(t).exists { sql =>
    val vendor = sql.getErrorCode      // MySQL ER_DUP_ENTRY → 1062
    val state  = sql.getSQLState       // Integrity violation → "23000"
    val msg    = Option(sql.getMessage).getOrElse("")
    (vendor == 1062 || state == "23000") && msg.contains(EmailUniqueConstraintName)
  }
```

**Why service, not repo?** The **Service** is exactly where I translate **infrastructure exceptions** (JDBC/Slick) into **domain/API errors**. Repositories stay DB-only; Controllers stay HTTP-only.

---

### 3.4 (Optional) I logged DB vendor codes while learning

**`employee/EmployeeRepository.scala`** (excerpt)
```scala
db.run(insertQuery += (...))
  .andThen { case scala.util.Failure(ex) =>
    unwrapSql(ex).foreach { sql =>
      log.error(s"DB error: vendorCode=${sql.getErrorCode} sqlState=${sql.getSQLState} msg=${sql.getMessage}", sql)
    }
  }
```

This didn’t change behavior; it just helped me see **1062 / 23000** in logs.

---

## 4) How the **new** flow works (duplicate email)

1. **Repo**: `db.run(...)` fails → Future failed with `SQLException`.
2. **Service**: `.recover` catches it, `isMySqlDuplicateEmail(ex)` returns true → returns `Left(ApiError.DuplicateEmail)` **inside a successful future**.
3. **Controller**: `.map { case Left(err) => err.toResult }` runs (the future is **success**, with `Either`), returns **409 JSON**.
4. **Frontend**: safely reads JSON, can show a toast and set a field error on the `email` input.

**No Play default error handler involved** for this expected case, and **no HTML** body reaches the browser.

---

## 5) Why handling it in the **Service** is better

- **Separation of concerns**  
  - Repo = SQL only (no HTTP / no ApiError)  
  - Service = domain rules & **infra → domain error translation**  
  - Controller = **domain → HTTP** mapping
- **Testability**  
  - I can unit test the service by simulating a thrown `SQLException(1062,"23000")` and asserting `Left(DuplicateEmail)`.
- **Consistency**  
  - The Controller doesn’t need to know vendor codes; it just maps `ApiError` to `Result`.
- **Extensibility**  
  - If I later add more unique constraints (e.g., mobile number), I can add more helpers in the service, keep the repo untouched.

---

## 6) Extra notes I found useful

- **`Action.async(parse.json)`** short-circuits to **400** if the body isn’t JSON. My block only runs when `request.body` is a `JsValue`.
- **`map` vs `recover`**: `map` is success-only; `recover` converts failures into values (here, into `Left(ApiError)`).
- **Naming constraints**: `CONSTRAINT uq_<table>_<column> UNIQUE (...)` makes error detection and debugging predictable across environments.
- **Security note**: In public signup flows I might avoid revealing “email already in use” (user enumeration). In this internal CRUD app, clarity wins.

---

## 7) Frontend follow-up: showing the server-side field error

On the React side (with React Hook Form), when the API returns:

```json
{
  "error": "Email already in use.",
  "validation_errors": { "email": "Already in use" }
}
```

I can catch it and set the error directly on the `email` field:

```tsx
const onSubmit = async (data: AddEmployeeFormData) => {
  try {
    const response = await fetch("/employees", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      const error = await response.json();
      if (error.validation_errors?.email) {
        setError("email", { type: "server", message: error.validation_errors.email });
      } else {
        toastError(error.message || "Failed to create employee.");
      }
      return;
    }

    toastSuccess("Employee added successfully!");
    reset();
    fetchData();
    onClose();
  } catch (err) {
    toastError("Something went wrong. Please try again.");
  }
};
```

This way:
- The toast shows the generic error.  
- The input field itself shows **“Already in use”** under the email box, coming straight from the backend.
