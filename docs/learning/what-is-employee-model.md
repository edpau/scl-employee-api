# 🧱 What Is `Employee.scala` (Domain Model)

---

## ✅ What Is a Domain Model?

`Employee.scala` defines the **core business data structure** for an Employee.

In general terms, a **domain model** is:

- A pure Scala `case class` that represents **real-world concepts** in your application
- Independent from the database, HTTP layer, or external frameworks
- Focused on what the object **is**, not how it's stored or transferred

---

## 🧩 Why Do We Use It?

- It models your **business logic** clearly
- It's reusable across services, controllers, repositories, etc.
- It allows strong typing and expressive code
- It separates the core logic from external concerns (DB, web, etc.)

---

## 🧱 Example

```scala
case class Employee(
  id: Option[Long] = None,
  firstName: String,
  lastName: String,
  email: String,
  mobileNumber: Option[String] = None,
  address: String,
  createdAt: Option[LocalDateTime] = None,
  updatedAt: Option[LocalDateTime] = None
) {
  def fullName: String = s"$firstName $lastName"
}
```

---

## 🧭 Why It's the First Step in Domain Setup

The domain model defines:
- What your app is about (`Employee`)
- What fields are required and which are optional
- How other parts of the app should understand this object

Without a domain model:
- You can’t build table mappings (Slick)
- You can’t define DTOs or service logic
- You can’t write proper business logic

So:
- ✅ Step 1: Domain model — what the object *is*
- 🔜 Step 2: Table mapping — how to store it
- 🔜 Step 3: Repository — how to access it

---

## 🧠 Why We Use `Option[...] = None` for Some Fields

- `id`: Set by the DB, so `Option` and `= None` let us omit it before insert
- `mobileNumber`: Optional input from user — also safe to default
- `createdAt`: Set by DB — left blank on creation, populated after insert

---

## ✅ Summary

| Purpose           | `Employee.scala`                                       |
|-------------------|--------------------------------------------------------|
| Represents        | Core domain concept (Employee)                         |
| Contains          | Only fields and helper methods like `fullName`         |
| Used by           | Table, DTOs, services, controllers                     |
| Depends on        | Nothing external — pure Scala                          |
| Best practice     | Keep logic-free, but allow small helpers like `fullName` |

