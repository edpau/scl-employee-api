# 📤 What Is `EmployeeDTO.scala`

---

## ✅ What Is a DTO?

A **DTO (Data Transfer Object)** is a class used to:
- Receive **input** from the frontend (like a form submission)
- Send **output** back to the frontend (like an API response)

It is *not* the domain model itself. It’s a wrapper that exists to:
- Control what data goes in and out of your API
- Prevent exposing sensitive or internal fields
- Allow format transformation (e.g., fullName, formatting timestamps)

---

## 🧩 Why Do We Use It?

| Without DTOs                 | With DTOs                          |
|-----------------------------|------------------------------------|
| Hard to control API shape   | You define exactly what you expose |
| Risk exposing internals     | Keeps internal logic hidden         |
| Repetitive mapping logic    | Centralizes field conversion        |
| No input validation boundary| Allows clean input handling         |

DTOs create a **safe, clear boundary** between your backend logic and the outside world.

---

## 🧱 Example

### ✅ Input DTO

```scala
case class CreateEmployeeDto(
  firstName: String,
  lastName: String,
  email: String,
  mobileNumber: Option[String],
  address: String
) {
  def toDomain: Employee = Employee(
    id = None,
    firstName = firstName,
    lastName = lastName,
    email = email,
    mobileNumber = mobileNumber,
    address = address
  )
}
```

### ✅ Output DTO

```scala
case class EmployeeResponseDto(
  id: Long,
  fullName: String,
  email: String
)

object EmployeeResponseDto {
  def fromDomain(emp: Employee): EmployeeResponseDto =
    EmployeeResponseDto(
      id = emp.id.getOrElse(0),
      fullName = emp.fullName,
      email = emp.email
    )
}
```

---

## 🧭 Why It Comes After Domain + Table

DTOs:
- Convert from/to your **domain model**
- Are used in the **controller layer**, not the DB

So:
- ✅ Step 1: Domain model
- ✅ Step 2: Table
- ✅ Step 3: Repository
- ✅ Step 4: Service
- ✅ Step 5: Controller
- ✅ DTOs come alongside controller or service logic

---

## 🧠 Typical Responsibilities

| Task                         | Belongs in DTO? | Notes                            |
|------------------------------|-----------------|----------------------------------|
| Input field validation       | ✅ Yes          | Can be added manually or via ZIO/Zod |
| Mapping to domain            | ✅ Yes          | via `.toDomain` helper            |
| Output formatting (e.g. fullName) | ✅ Yes     | Pull from domain helper           |
| Database query logic         | ❌ No           | That’s for the repository         |
| Business rules               | ❌ No           | That’s for the service layer      |

---

## ✅ Summary

| Purpose             | `EmployeeDTO.scala`                                  |
|---------------------|-------------------------------------------------------|
| Accept user input   | Yes (`CreateEmployeeDto`)                            |
| Send API response   | Yes (`EmployeeResponseDto`)                          |
| Converts to/from    | Domain model (`Employee`)                            |
| Depends on          | Only domain model, not DB or Slick                   |
| Used by             | Controller and sometimes service                     |

DTOs keep your API contracts clean, flexible, and decoupled from your internal logic.

