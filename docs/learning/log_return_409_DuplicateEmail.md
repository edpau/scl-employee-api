# Learning Log: Return 409 DuplicateEmail with field error on unique email violation
## Summary
Translate MySQL duplicate-key violations on employees.email into a clean
HTTP 409 response with a field-specific error, instead of bubbling a 500 HTML
error to the client.

## Why
- Current behavior on duplicate email: JDBC exception → Play error handler → 500 HTML.
- Frontend then fails parsing JSON (`Unexpected token '<'`).
- Desired: stable JSON contract with status 409 and `validation_errors.email`.

## What changed
- utils/ApiError.scala
  - Add `case object DuplicateEmail` with `.toResult` → `409 Conflict`
    JSON shape:
    ```json
    { "error": "Email already in use.", "validation_errors": { "email": "Already in use" } }
    ```

- employee/EmployeeService.scala
  - In `createEmployee`, add `.recover { ... }` that:
    - Detects MySQL duplicate via helper `isMySqlDuplicateEmail(ex)`
      (vendor code 1062 or SQLSTATE 23000 AND constraint name match),
    - Returns `Left(ApiError.DuplicateEmail)` for clean 409 JSON,
    - Falls back to `Left(ApiError.InternalServerError("Unexpected error"))` otherwise.
  - Add private helpers:
    - `unwrapSql` (tail-recursive) to find the underlying `SQLException`
    - `isMySqlDuplicateEmail` using stable constraint name `uq_employees_email`

- employee/EmployeeRepository.scala
  - Add `.andThen { case Failure(ex) => ... }` logging of vendor code,
    SQLSTATE, and message (for learning/debugging only). No behavior change
    (failure still bubbles to Service).

- conf/evolutions/default/3.sql
  - Rename the auto-generated unique index to a stable name:
    - `DROP INDEX email ON employees;`
    - `ADD CONSTRAINT uq_employees_email UNIQUE (email);`
  - Provides reversible Downs.

## Behavior (before → after)
- **Before**: POST /employees with duplicate email → 500 HTML, frontend JSON parse error.
- **After**:  POST /employees with duplicate email → **409** JSON:
  ```json
  {
    "error": "Email already in use.",
    "validation_errors": { "email": "Already in use" }
  }
