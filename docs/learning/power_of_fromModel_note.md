# 🧠 Power of `fromModel`: Why It's More Than Just Copying

In many early DTOs, `fromModel(...)` seems like it just copies fields over.  
But in practice, it's a powerful spot to filter, transform, and format your API response safely and intentionally.

---

## ✅ 1. Control What Gets Exposed to the API

### Employee Domain Model (includes sensitive/internal fields)

```scala
case class Employee(
  id: Option[Int],
  firstName: String,
  lastName: String,
  email: String,
  mobileNumber: Option[String],
  address: String,
  salary: Double,
  internalNote: Option[String]
)
```

### EmployeeResponse DTO (hides sensitive fields)

```scala
case class EmployeeResponse(
  id: Int,
  firstName: String,
  lastName: String,
  email: String,
  mobileNumber: Option[String],
  address: String
)

def fromModel(model: Employee): EmployeeResponse = {
  EmployeeResponse(
    id = model.id.getOrElse(0),
    firstName = model.firstName,
    lastName = model.lastName,
    email = model.email,
    mobileNumber = model.mobileNumber,
    address = model.address
  )
}
```

---

## ✅ 2. Transform or Combine Fields

### Example: Add `fullName` and `isActive` to DTO

```scala
case class EmployeeResponse(
  id: Int,
  fullName: String,
  email: String,
  isActive: Boolean
)

def fromModel(model: Employee): EmployeeResponse = {
  val now = java.time.LocalDateTime.now()

  EmployeeResponse(
    id = model.id.getOrElse(0),
    fullName = s"${model.firstName} ${model.lastName}",
    email = model.email,
    isActive = model.updatedAt.exists(_.toLocalDateTime.isAfter(now.minusDays(30)))
  )
}
```

---

## ✅ 3. Format Values for Frontend Readability

```scala
case class EmployeeResponse(
  id: Int,
  formattedCreatedAt: Option[String]
)

def fromModel(model: Employee): EmployeeResponse = {
  val formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")

  EmployeeResponse(
    id = model.id.getOrElse(0),
    formattedCreatedAt = model.createdAt.map(_.toLocalDateTime.format(formatter))
  )
}
```

---

## ✅ 4. Sanitize or Normalize Input

### Example: Trim and lowercase email

```scala
case class EmployeeResponse(
  id: Int,
  email: String
)

def fromModel(model: Employee): EmployeeResponse = {
  EmployeeResponse(
    id = model.id.getOrElse(0),
    email = model.email.trim.toLowerCase
  )
}
```

Or normalize phone numbers:

```scala
val normalizedPhone = model.mobileNumber.map(_.replaceAll("[^\d]", ""))
```

---

## ✅ 5. Add Contextual Logic (e.g. Based on Current User)

```scala
case class EmployeeResponse(
  id: Int,
  fullName: String,
  canEdit: Boolean
)

def fromModel(model: Employee, currentUser: User): EmployeeResponse = {
  val isAdmin = currentUser.role == "Admin"

  EmployeeResponse(
    id = model.id.getOrElse(0),
    fullName = s"${model.firstName} ${model.lastName}",
    canEdit = isAdmin
  )
}
```

> You can overload `fromModel` or pass additional context if needed.

---

## ✅ Summary

| Power Used In `fromModel`     | Benefit                        |
|-------------------------------|--------------------------------|
| Filtering fields              | Hide sensitive info            |
| Combining values              | Add things like `fullName`     |
| Formatting                    | Make timestamps user-friendly  |
| Sanitizing                    | Prevent weird inputs from leaking out |
| Contextual output             | Dynamic fields like `canEdit`  |

Your `fromModel(...)` is your **last line of control** before sending data into the world — treat it like an API gatekeeper.
