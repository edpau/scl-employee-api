# 🧱 Timestamp to LocalDateTime Mapping in DTOs

---

## ❓ The Problem I Faced

In my `Employee` domain model, I used:

```scala
createdAt: Option[Timestamp]
updatedAt: Option[Timestamp]
```

Then, while building the `EmployeeResponse` DTO, I wrote:

```scala
createdAt = model.createdAt.toLocalDateTime
```

⛔ This gave me the error: `Cannot resolve symbol toLocalDateTime`

---

## 🧠 Why That Didn't Work

- `model.createdAt` is an `Option[Timestamp]`
- But `Option` itself doesn’t have a `.toLocalDateTime` method
- So calling `.toLocalDateTime` directly caused the compiler error

---

## ✅ How I Solved It

### Step 1: Used `.map()` to access the value inside the `Option`

```scala
createdAt = model.createdAt.map(_.toLocalDateTime)
updatedAt = model.updatedAt.map(_.toLocalDateTime)
```

### Step 2: Updated the DTO type to match the optional mapping

Instead of:

```scala
createdAt: LocalDateTime
```

I changed it to:

```scala
createdAt: Option[LocalDateTime]
```

---

## 💡 Why This Was a Better Solution

- It avoids assigning fake default values like `LocalDateTime.MIN`
- It communicates clearly that timestamps might be missing
- It makes my API response more honest and predictable
- The code became simpler and more readable

---

## ✅ Final Working Code

### DTO

```scala
case class EmployeeResponse(
  id: Int,
  firstName: String,
  lastName: String,
  email: String,
  mobileNumber: Option[String],
  address: String,
  createdAt: Option[LocalDateTime],
  updatedAt: Option[LocalDateTime]
)
```

### Mapping from Domain

```scala
def fromModel(model: Employee): EmployeeResponse = {
  EmployeeResponse(
    id = model.id.getOrElse(0),
    firstName = model.firstName,
    lastName = model.lastName,
    email = model.email,
    mobileNumber = model.mobileNumber,
    address = model.address,
    createdAt = model.createdAt.map(_.toLocalDateTime),
    updatedAt = model.updatedAt.map(_.toLocalDateTime)
  )
}
```

---

## ✅ Summary of My Fix

| Issue I Hit                   | How I Solved It                     |
|------------------------------|-------------------------------------|
| `.toLocalDateTime` on Option | Used `.map(_.toLocalDateTime)`      |
| DTO expected non-optional    | Changed it to `Option[LocalDateTime]` |
| Avoiding default timestamps  | Let the JSON show `null` instead     |

This helped me understand how to handle SQL timestamps cleanly in a Play + Scala project.


---

## 🏗️ Why Some Timestamps Are `Option[...]` and Some Are Not

The decision to use `Option[Timestamp]` in a domain model often comes from how the database defines its timestamp columns.

For example:

```sql
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
```

In this setup:
- The database provides the timestamp **automatically**
- You **don't** supply `createdAt` or `updatedAt` during insertion
- But you **do** read them back later

So in Scala, we use:

```scala
createdAt: Option[Timestamp] = None
updatedAt: Option[Timestamp] = None
```

This tells the app:
- “I’m not sending this in manually, the DB will fill it.”
- “I may or may not have a value when reading — depends on when in the flow I access it.”

---

### What If the DB Does NOT Use Default Values?

Example:

```sql
created_at TIMESTAMP NOT NULL,
updated_at TIMESTAMP NOT NULL
```

In this case:
- The database expects **you to set those values yourself**
- So they must always be present when inserting a new record

And the domain model in Scala becomes:

```scala
createdAt: Timestamp
updatedAt: Timestamp
```

No `Option[...]` needed — because the DB doesn’t provide fallback behavior.

---

### ✅ Summary

| DB Column Style                                | Scala Type                    |
|------------------------------------------------|-------------------------------|
| `DEFAULT CURRENT_TIMESTAMP` (auto-filled)      | `Option[Timestamp] = None`    |
| `NOT NULL` without default                     | `Timestamp` (non-optional)    |

This distinction helped clarify when and why timestamp fields in a Scala model should be optional, based on how they're treated by the database.
