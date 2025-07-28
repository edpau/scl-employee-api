# 🧠 Understanding the Three `Option[...] = None` Fields in Domain Model

```scala
case class Employee(
  id: Option[Long] = None,
  mobileNumber: Option[String] = None,
  createdAt: Option[LocalDateTime] = None
)
```

Each field uses `Option[...] = None`, but for **different reasons**. Here's what each one means and why we treat them differently.

---

## 🔑 1. `id: Option[Long] = None`

### ✅ Why `Option`
- The ID is **assigned by the database** (auto-increment).
- When you create a new `Employee`, you don't know the ID yet.
- So you represent it as `None`, and the DB will fill it in.

### ✅ Why `= None`
- Makes it easier to construct new employees:
```scala
Employee(firstName = "Alice", ...)
```
- After insert, Slick gives you back the `id`, and you can use `.copy(id = Some(...))`.

### ⚠️ Special Behavior
- Slick supports `.returning(...).into(...)` to **get the ID immediately** during insert.
- No need to reload from DB just to get the ID.

---

## 📱 2. `mobileNumber: Option[String] = None`

### ✅ Why `Option`
- In the DB, `mobile_number` is **nullable**.
- The employee might not provide one.

### ✅ Why `= None`
- Most employees won’t have a number at creation time.
- This allows simpler construction:
```scala
Employee(..., mobileNumber = None)
```
- Or just omit it:
```scala
Employee(firstName = "Alice", ...)
```

### 💡 When Should You Use `= None`?
Use `= None` if:
- The field is **truly optional**
- You want to **avoid boilerplate** when constructing
- It reflects how the data behaves in the DB

---

## 🕒 3. `createdAt: Option[LocalDateTime] = None`

### ✅ Why `Option`
- The timestamp is **set by the database**, not by the application.
- You don’t know it at the time of insert.

### ✅ Why `= None`
- During insert, you omit it and let MySQL assign the value.
- After insert, you can fetch it with an extra query.

### ⚠️ Different from `id`
- You **don’t get `createdAt` back immediately** after insert.
- You must **reload** the row from the DB to read it.

---

## ✅ Summary Table

| Field         | Why `Option`?         | Why `= None`?            | Auto-returned on insert? |
|---------------|------------------------|---------------------------|---------------------------|
| `id`          | Set by DB              | For easy construction     | ✅ Yes (via Slick)         |
| `mobileNumber`| Nullable field         | For clarity + simplicity  | ❌ No                      |
| `createdAt`   | Set by DB              | Must be omitted at insert | ❌ No (must reload)        |

---

## 🧠 Guideline: When to Use `= None` with `Option[...]`

| Use `= None` If...                              |
|-------------------------------------------------|
| You want to omit the field when constructing    |
| The value is DB-managed or truly optional       |
| The field defaults to `None` in most scenarios  |

| Avoid `= None` If...                            |
|-------------------------------------------------|
| The field is required input from a user or API  |
| You want to force the developer to be explicit  |

