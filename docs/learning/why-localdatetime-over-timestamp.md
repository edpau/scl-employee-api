# 📘 Why Use `Option[LocalDateTime] = None` Instead of `Timestamp` in Scala Domain Models

---

## ✅ Why `LocalDateTime` is Better Than `Timestamp`

| Feature                     | `LocalDateTime` (recommended) | `Timestamp` (legacy)           |
|-----------------------------|-------------------------------|---------------------------------|
| ✅ Immutable                | Yes                           | No                              |
| ✅ Time zone aware          | Yes (`ZonedDateTime` supported)| No                              |
| ✅ Scala/Java 8+ standard   | Yes                           | No (legacy JDBC)                |
| ✅ Better formatting APIs   | Yes (`DateTimeFormatter`)      | No (manual parsing needed)      |
| ✅ Cleaner to reason about  | Yes                           | No (accidental mutations)       |
| ✅ Used by Play JSON macros | Yes                           | Needs more work                 |

---

## ✅ Why We Use `Option[...] = None`

```scala
createdAt: Option[LocalDateTime] = None
```

- Because this field is **set by the DB**, not by your application.
- During insert, you don't know the value yet → you leave it as `None`.
- After insert + reload, the DB fills it in.

---

## 🔧 Extra Work You Need to Do

1. **Add `.?` in Slick Table**

```scala
def createdAt = column[Option[LocalDateTime]]("created_at")
```

2. **Let MySQL set it**

In your SQL migration:

```sql
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
```

3. **Use `Option[...]` in your model**  
So you don’t have to pass a value when creating a new `Employee`.

---

## 🤔 Why Slick Can’t Automatically Return `createdAt`

MySQL will **auto-fill** `created_at`,  
but it won’t return it during insert.

So this **won’t** work:

```scala
val insertQuery = (employees returning employees.map(_.createdAt)) += emp // ❌
```

---

## ✅ Solution: Helper Function

Write a helper to:

1. Insert the employee
2. Read the full row back from the DB (with timestamps)

```scala
def insertAndReturnFull(emp: Employee): Future[Employee] = {
  val insertQuery = (employees returning employees.map(_.id)) += emp

  for {
    id <- db.run(insertQuery)
    fullRow <- db.run(employees.filter(_.id === id).result.head)
  } yield fullRow
}
```

Now you get the full object with:
- `id`
- `createdAt`
- `updatedAt`

---

## ✅ Final Recommendation

- Use `Option[LocalDateTime] = None` for DB-managed timestamps.
- Add a helper method to insert + reload to get the full row.
- Avoid `java.sql.Timestamp` unless you're forced to use low-level JDBC APIs.

