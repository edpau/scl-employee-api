# 🔄 What Is `EmployeeRepository.scala`

---

## ✅ What Is a Repository?

`EmployeeRepository.scala` is the layer that encapsulates **all database access logic** for the `Employee` domain.

It acts as the **interface between your business logic and the database**, using Slick and the table mapping.

In general terms, it:

- Provides methods like `insert`, `findById`, `update`, `delete`, etc.
- Uses the Slick `EmployeeTable` to build SQL queries
- Uses `db.run(...)` to execute them
- Hides all the raw SQL/Slick complexity from your service layer

---

## 🧩 Why Do We Use It?

Repositories allow you to:
- Keep all DB access code in one place
- Make services and controllers simpler and more testable
- Avoid repeating Slick code across your app
- Swap out the database logic in one place (e.g. from MySQL to Postgres)

---

## 🧱 Example

```scala
class EmployeeRepository @Inject()(db: Database)(implicit ec: ExecutionContext) {

  private val employees = TableQuery[EmployeeTable]

  def findById(id: Long): Future[Option[Employee]] =
    db.run(employees.filter(_.id === id).result.headOption)

  def insert(emp: Employee): Future[Employee] = {
    val insertQuery = (employees returning employees.map(_.id)
      into ((emp, id) => emp.copy(id = Some(id))))
    db.run(insertQuery += emp)
  }
}
```

---

## 🧭 Why It Comes After Table

The repository:
- **Depends on the table** (`EmployeeTable.scala`) to create queries
- But it **doesn't care** about DTOs or HTTP logic

So:
- ✅ Step 1: Domain model (`Employee.scala`)
- ✅ Step 2: Table mapping (`EmployeeTable.scala`)
- ✅ Step 3: Repository (`EmployeeRepository.scala`)
- 🔜 Step 4: Service layer
- 🔜 Step 5: Controller layer

---

## 🧠 Typical Responsibilities

| Responsibility            | In Repository? | Notes                            |
|---------------------------|----------------|----------------------------------|
| Find employee by ID       | ✅ Yes         | Reads from DB                    |
| Insert new employee       | ✅ Yes         | Uses Slick to insert             |
| Update employee           | ✅ Yes         | Optional: can be handled here    |
| Validate business rules   | ❌ No          | Should go in the service layer   |

---

## ✅ Summary

| Purpose             | `EmployeeRepository.scala`                          |
|---------------------|-----------------------------------------------------|
| Reads/writes DB     | Yes, using Slick                                    |
| Depends on          | `EmployeeTable.scala`                               |
| Used by             | `EmployeeService.scala`                             |
| Should contain      | Only DB-related logic (not business rules)          |
| Helps with          | Separation of concerns, testability, reuse          |

Repositories are where your **low-level DB logic lives** — so your services and controllers stay clean and focused.

