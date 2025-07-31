# 🐛 Debug Note: SQLIntegrityConstraintViolationException on created_at

---

## ❓ What Was the Error

When I tried to create an `Employee` via POST `/employees`, I got this error in the SBT console:

```
SQLIntegrityConstraintViolationException: Column 'created_at' cannot be null
```

Play wrapped this as an `InternalServerError`, and I saw the full stack trace — but none of the files pointed to my Scala code.

---

## 🧠 What I Learned from the Error

At first, I was confused why it failed, because I thought my `created_at` column had a default timestamp.

Then I looked at my migration:

```sql
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
```

This line says:
- ✅ Database will auto-fill the field with current time
- ❌ But only if I don’t send a NULL — it must be omitted

So the problem was: **Slick was inserting a NULL**, which violates `NOT NULL`.

---

## 🔍 How I Found the Cause

In my code, I had:

```scala
 def createEmployee(data: CreateEmployeeDto): Future[Either[ApiError, EmployeeResponse]] = {
...
  val preSaved = Employee(
..,
  createdAt = None,
  updatedAt = None
  )
  employeeRepository.create(preSaved).map(saved => Right(EmployeeResponse.fromModel(saved)))
}  
```

This seemed safe, but **Slick may treat `None` as NULL** unless explicitly excluded from the insert.

So I had two choices:
1. Remove `NOT NULL` — which weakens the schema
2. Refactor the insert to **omit `created_at` and `updated_at`**

I chose **Option 2**, which is the correct, clean solution.

---

## 🛠 How I Solved It

### ✅ Step 1: I Created a New Case Class

```scala
case class InsertEmployee(
  firstName: String,
  lastName: String,
  email: String,
  mobileNumber: Option[String],
  address: String
)
```

This lets me **separate user input** from full `Employee`.

---

### ✅ Step 2: I Added an Insert Projection in `EmployeeTable`

```scala
def insertProjection = (firstName, lastName, email, mobileNumber, address) <> (
  InsertEmployee.tupled,
  InsertEmployee.unapply
)
```

This excludes `created_at`, `updated_at`, and `id`.

---

### ✅ Step 3: I Updated My Repository

```scala
def create(insertEmp: InsertEmployee): Future[Employee] = {
  val insertQuery = employees
    .map(e => (e.firstName, e.lastName, e.email, e.mobileNumber, e.address))
    .returning(employees.map(_.id))
    .into { case ((firstName, lastName, email, mobileNumber, address), id) =>
      Employee(Some(id), firstName, lastName, email, mobileNumber, address, None, None)
    }

  db.run(insertQuery += (
    insertEmp.firstName,
    insertEmp.lastName,
    insertEmp.email,
    insertEmp.mobileNumber,
    insertEmp.address
  ))
}
```

Now the DB handles timestamps safely.

---

### ✅ Step 4: I Updated My Service Layer

```scala
val insert = InsertEmployee(
  firstName = data.firstName.trim,
  lastName = data.lastName.trim,
  email = data.email.trim,
  mobileNumber = data.mobileNumber.map(_.trim).filter(_.nonEmpty),
  address = data.address.trim
)

employeeRepository.create(insert).map(saved => Right(EmployeeResponse.fromModel(saved)))
```

---

## ✅ Why This Is Better

| Approach | Pros | Cons |
|---------|------|------|
| Letting DB auto-fill `created_at` | ✅ Safer, ✅ Consistent, ✅ No NULLs | — |
| Removing `NOT NULL` | ❌ May result in NULL values | 🔥 Risky |
| Using separate case class | ✅ Clear, ✅ Type-safe, ✅ Expressive | Slightly more setup |

---

## ✅ Summary

- The DB error told me `created_at` was NULL
- I realized I shouldn’t send `None`, I should **omit the field**
- I introduced `InsertEmployee` to exclude the timestamps
- Now my inserts are clean and the DB handles time automatically

