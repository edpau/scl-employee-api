# Understanding the `def create` Insert Method in Slick

This note explains how the following `create` method in our EmployeeRepository works step by step.

```scala
def create(insertEmp: InsertEmployee): Future[Employee] = {
  val insertQuery = employees
    .map(e => (e.firstName, e.lastName, e.email, e.mobileNumber, e.address))
    .returning(employees.map(_.id))
    .into { case ((firstName, lastName, email, mobileNumber, address), id) =>
      Employee(
        id = Some(id),
        firstName = firstName,
        lastName = lastName,
        email = email,
        mobileNumber = mobileNumber,
        address = address,
        createdAt = None, // Let DB fill this
        updatedAt = None
      )
    }

  db.run(insertQuery += (insertEmp.firstName, insertEmp.lastName, insertEmp.email, insertEmp.mobileNumber, insertEmp.address))
}
```

---

## 🔹 Step-by-Step Breakdown

### 1. `.map(...)`
```scala
.map(e => (e.firstName, e.lastName, e.email, e.mobileNumber, e.address))
```
This selects only the columns that we want to insert. We skip `id`, `createdAt`, and `updatedAt` because:

- `id` is auto-generated.
- `createdAt` and `updatedAt` are managed by the database using `DEFAULT CURRENT_TIMESTAMP`.

### 2. `.returning(...)`
```scala
.returning(employees.map(_.id))
```
This tells Slick to return the **auto-generated `id`** after insert.

### 3. `.into(...)`
```scala
.into { case ((...), id) => ... }
```
This lets us combine:
- The inserted values (`firstName`, `lastName`, etc.)
- The returned `id`

...into a full `Employee` case class.

### 4. `db.run(...)`
```scala
db.run(insertQuery += (...))
```
This actually **runs the insert** query using the values from `insertEmp`.

---

## ✅ Why We Use This Approach

- **Separation of concerns:** `InsertEmployee` contains only the fields needed for insert.
- **Database-managed fields** like `createdAt` don’t need to be set manually.
- **Slick's `.into`** provides a clean way to construct a full model object from the insert.

---

## 🔁 Summary Table

| Step                          | Purpose                                  |
|------------------------------|------------------------------------------|
| `.map(...)`                  | Choose which fields to insert            |
| `.returning(...)`            | Ask DB to return the generated ID        |
| `.into(...)`                 | Combine insert data and ID into case class |
| `db.run(...)`                | Executes the insert                      |

This insert pattern is considered production-ready and gives you full control while remaining type-safe and clean.