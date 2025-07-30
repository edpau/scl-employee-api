# 🧱 What Is `EmployeeService.scala`

---

## ✅ What Is a Service Layer?

`EmployeeService.scala` sits between your **controller** and your **repository**.
It contains the **business logic** for working with employees — the "thinking" part of your app.

In general terms, a **service** is:

- A plain Scala class that handles **application rules and workflows**
- The place to **combine logic**, transform DTOs, validate conditions, and call your repository
- A layer that ensures your controller stays thin and your repository stays dumb

---

## 🧩 Why Do We Use a Service Layer?

| Without Service Layer                  | With Service Layer                   |
|----------------------------------------|--------------------------------------|
| Controllers contain too much logic     | Controllers stay simple              |
| Repositories contain business logic    | Repos only know how to access data   |
| Hard to test or change business rules  | Logic is modular and testable        |

- Keeps a clean **separation of concerns**
- Encourages **unit testing** without Play or DB
- Centralizes logic for `GET`, `POST`, `UPDATE`, etc.

---

## 🧱 Example: `EmployeeService.scala`

```scala
package services

import employee.{Employee, EmployeeResponse, CreateEmployeeDto}
import repositories.EmployeeRepository
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EmployeeService @Inject() (
  employeeRepo: EmployeeRepository
)(implicit ec: ExecutionContext) {

  def getEmployeeById(id: Long): Future[Option[EmployeeResponse]] = {
    employeeRepo.findById(id).map(_.map(EmployeeResponse.fromModel))
  }

  def createEmployee(dto: CreateEmployeeDto): Future[EmployeeResponse] = {
    val employee = Employee(
      id = None,
      firstName = dto.firstName,
      lastName = dto.lastName,
      email = dto.email,
      mobileNumber = dto.mobileNumber,
      address = dto.address
    )

    employeeRepo.insert(employee).map(EmployeeResponse.fromModel)
  }
}
```

---

## 🧭 Why It Comes After Repository

The service:
- Uses your repository to fetch/save domain objects (`Employee`)
- Converts to/from DTOs like `CreateEmployeeDto` and `EmployeeResponse`
- Often contains business rules (e.g. no duplicate emails)

### The Build Order:
1. ✅ Domain model (`Employee`)
2. ✅ Table mapping (`EmployeeTable`)
3. ✅ Repository (`EmployeeRepository`)
4. ✅ DTOs (`CreateEmployeeDto`, `EmployeeResponse`)
5. 🔜 Service (`EmployeeService`)
6. 🔜 Controller

---

## 🧠 Typical Responsibilities

| Task                          | Belongs in Service? | Notes                                |
|-------------------------------|----------------------|---------------------------------------|
| Input validation              | ✅ Yes                | Beyond shape (e.g. "email in use?")  |
| Convert DTO ↔ Domain          | ✅ Yes                | Safe and testable mapping             |
| Call repo methods             | ✅ Yes                | Encapsulates DB logic                 |
| Send HTTP responses           | ❌ No                 | That’s for the controller             |
| Format JSON                   | ❌ No                 | Done by DTOs / Play JSON              |

---

## ✅ Summary

| Purpose           | `EmployeeService.scala`                                 |
|-------------------|----------------------------------------------------------|
| Represents        | The business logic and use cases for employees          |
| Contains          | Methods for CRUD operations that use DTOs and repo      |
| Used by           | Controller                                               |
| Depends on        | Repository + DTOs + Domain model                        |
| Best practice     | Keep it pure, testable, and focused on logic only       |

Your service layer is what makes your app **smart**, not just a set of DB queries or HTTP routes.
