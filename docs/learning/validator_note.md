# 🛡️ What Is `Validator.scala`

---

## ✅ What Is a Validator?

A **Validator** is a utility object or trait that helps **check input data for correctness** before your app processes it.

You use it to:

- Catch invalid or missing data early
- Prevent bad data from reaching your database
- Generate **structured, reusable validation logic**
- Return clear, consistent error messages for the frontend

---

## 🧩 Why Do We Use It?

| Without Validators              | With Validators                    |
|---------------------------------|------------------------------------|
| Duplicate validation code       | Centralized reusable logic         |
| Hidden validation in services   | Explicit and readable checks       |
| Inconsistent error format       | Predictable, map-based output      |
| Hard to test input failures     | Validators are easily testable     |

Validators make sure **only clean and valid data** gets through your service.

---

## 🔍 Code Example

### ✅ Validator Trait (Shared Rules)

```scala
package utils.validation

trait Validator {
  def isNotEmpty(fieldName: String, value: String): Option[(String, String)] =
    if (value.trim.isEmpty) Some(fieldName -> s"$fieldName cannot be empty")
    else None

  def isNoneBlankIfDefined(fieldName: String, value: Option[String]): Option[(String, String)] =
    value match {
      case Some(v) if v.trim.isEmpty => Some(fieldName -> s"$fieldName cannot be blank if provided")
      case _ => None
    }
}
```

This is a **base trait** that defines reusable validation helpers:

- `isNotEmpty`: checks a required string field
- `isNoneBlankIfDefined`: checks an optional field if it’s defined (but empty)

Each returns an `Option[(String, String)]`:
- `None` if valid
- `Some(fieldName -> errorMessage)` if invalid

---

### ✅ EmployeeValidator Example

```scala
package employee

import utils.validation.Validator

object EmployeeValidator extends Validator {
  def validateCreate(dto: CreateEmployeeDto): Map[String, String] = {
    List(
      isNotEmpty("firstName", dto.firstName),
      isNotEmpty("lastName", dto.lastName),
      isNotEmpty("email", dto.email),
      isNoneBlankIfDefined("mobileNumber", dto.mobileNumber),
      isNotEmpty("address", dto.address)
    ).flatten.toMap
  }
}
```

This takes a `CreateEmployeeDto` and **validates each field** using the shared helpers.

The result is a `Map[String, String]` where:
- Key = field name (e.g. "email")
- Value = error message (e.g. "email cannot be empty")

If the map is empty, it means **validation passed** ✅

---

## 🧭 Why It Comes After DTOs

Validators only make sense **after** you have defined a DTO structure.

The build order is:
1. ✅ Domain model
2. ✅ DTOs
3. ✅ Validator (uses DTOs)

You can then call validators inside your service or controller to **gate the flow**.

---

## 🧠 Typical Responsibilities

| Task                            | Belongs in Validator? | Notes                               |
|----------------------------------|------------------------|--------------------------------------|
| Required field check             | ✅ Yes                 | Use `isNotEmpty`                     |
| Optional but non-blank field     | ✅ Yes                 | Use `isNoneBlankIfDefined`           |
| Format checks (e.g. email regex) | ✅ Yes                 | Can add more helpers                 |
| Business rules (e.g. unique?)    | ❌ No                  | That belongs in the service layer    |
| Database queries                 | ❌ No                  | Keep validators pure and light       |

---

## ✅ Summary

| Purpose               | `Validator.scala`                                      |
|-----------------------|--------------------------------------------------------|
| Validates input data  | Yes (via DTO)                                          |
| Returns error map     | Yes (`Map[String, String]`)                            |
| Keeps logic reusable  | Yes (through shared trait)                             |
| Depends on            | DTO only, no Play or DB                                |
| Used in               | Services or controllers (before DB call)               |

Validators are your **first line of defense** against bad input. Simple, composable, testable.