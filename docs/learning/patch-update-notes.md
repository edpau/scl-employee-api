# 📝 Patch Update Notes – Contract vs Employee

## 🔍 What Happened

When writing `updateContractById`, I initially tried to build a patch system like this:

```scala
val updates = Map(
  "employeeId" -> data.employeeId,
  "contractType" -> data.contractType.map(_.trim),
  ...
)
```

Then I applied it using:

```scala
employeeId = updates.getOrElse("employeeId", existing.employeeId),
```

This broke at compile time because the `updates` map became:

```scala
Map[String, Any]
```

This caused the compiler to lose the original type information (`Int`, `String`, `LocalDate`, etc.) and returned values as `Any`, making it unsafe and unscalable.

---

## 👀 Why It Didn't Happen in `EmployeeService`

In `updateEmployeeById`, I used:

```scala
val updates = Map(
  "firstName" -> data.firstName.map(_.trim),
  ...
).collect { case (k, Some(v)) => k -> v }
```

All the fields were `String` types, so the map effectively became:

```scala
Map[String, String]
```

This kept the type information safe and avoided casting issues. That’s why this approach worked there.

---

## ✅ Correct Solution in `ContractService`

I rewrote the patch logic like this:

```scala
val updated = existing.copy(
  employeeId = data.employeeId.getOrElse(existing.employeeId),
  contractType = data.contractType.map(_.trim).getOrElse(existing.contractType),
  ...
)
```

This worked perfectly because:
- Each field is handled individually with its own type.
- I preserved full type safety.
- It allowed custom logic per field (like trimming strings).

---

## 🔮 What I Learned and Will Do Going Forward

- Avoid using `Map[String, Any]` when types vary — it creates type safety issues.
- Prefer handling each field directly using `.getOrElse(...)` or `.orElse(...)`.
- Use `.map(_.trim)` only on `String` fields.
- This approach is clean, readable, and robust for future maintenance.