# 🕒 Handling `createdAt` and `updatedAt` Timestamps in Play + Slick

## ✅ Goal

I wanted to add `createdAt` and `updatedAt` fields to my `Employee` domain model and store them in MySQL using Slick, then expose them via JSON in the API.

---

## 🧪 Initial Attempt: `LocalDateTime`

### Why I Started with `LocalDateTime`

At first, I used `java.time.LocalDateTime` because it’s more idiomatic in Scala and Java 8+. It represents a clean, timezone-agnostic date-time object that's great for modeling business logic. It also avoids the legacy issues of `java.sql.Timestamp`.

> I thought
> 
> > "`LocalDateTime` is a better domain type — it's more modern and avoids database concerns leaking into the business layer."

So I started with:

```scala
import java.time.LocalDateTime

createdAt: Option[LocalDateTime] = None
updatedAt: Option[LocalDateTime] = None
```

And in the Slick table:

```scala
def createdAt = column[Option[LocalDateTime]]("created_at")
def updatedAt = column[Option[LocalDateTime]]("updated_at")
```

---

### 💥 What Went Wrong

After inserting rows via MySQL (which used default DB-generated timestamps), calling `GET /employees` crashed the app with:

```
DateTimeParseException: Text '2025-07-29 20:53:13' could not be parsed at index 10
```

> MySQL stores timestamps as:  
> `"YYYY-MM-DD HH:MM:SS"`  
> But `LocalDateTime.parse(...)` expects a `'T'` between date and time:  
> `"YYYY-MM-DDTHH:MM:SS"`

Because of this mismatch, Slick couldn’t read the timestamp value into `LocalDateTime`.

---

## 🔁 The Fix: Switch to `java.sql.Timestamp`

Instead of changing how MySQL stores the data or writing a custom parser, I updated my code to use `java.sql.Timestamp`, which is directly compatible with how MySQL formats `TIMESTAMP` fields.

```scala
import java.sql.Timestamp

createdAt: Option[Timestamp] = None
updatedAt: Option[Timestamp] = None
```

In the Slick table:

```scala
def createdAt = column[Option[Timestamp]]("created_at")
def updatedAt = column[Option[Timestamp]]("updated_at")
```

---

## 🧰 JSON Formatting Problem

After switching to `Timestamp`, I hit another issue:

```
No instance of play.api.libs.json.Format is available for scala.Option[java.sql.Timestamp]
```

> Play couldn’t automatically convert `Timestamp` into JSON.

---

## ✅ Two Working Solutions

### ✅ Option 1 (what I used): Epoch Millis (Stack Overflow fix)
- [No instance of play.api.libs.json.Format is available for scala.Option[java.sql.Timestamp] in the implicit scope](https://stackoverflow.com/questions/66045015/no-instance-of-play-api-libs-json-format-is-available-for-scala-optionjava-sql)
```scala
implicit val timestampReads: Reads[Timestamp] =
  implicitly[Reads[Long]].map(new Timestamp(_))

implicit val timestampWrites: Writes[Timestamp] =
  implicitly[Writes[Long]].contramap(_.getTime)
```

- Converts `Timestamp` ↔ `Long` (milliseconds since epoch)
- JSON Output:
  ```json
  "createdAt": 1722273600000
  ```

✅ Works  
✅ Simple  
❌ Not human-readable

---

### ✅ Option 2 (alternative): Human-readable String Format

```scala
implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def writes(ts: Timestamp): JsValue =
    JsString(ts.toLocalDateTime.format(formatter))

  def reads(json: JsValue): JsResult[Timestamp] =
    json.validate[String].map(str => Timestamp.valueOf(str))
}
```

- JSON Output:
  ```json
  "createdAt": "2025-07-29 20:53:13"
  ```

✅ Human-friendly  
✅ Good for frontend clients  
❌ Slightly more verbose

---

## 🔮 Future Plan: Use `LocalDateTime` in DTO

Eventually, I will:

1. Keep using `Timestamp` in the Slick model for DB compatibility
2. Convert to `LocalDateTime` in my DTO layer:
   ```scala
   createdAt = employee.createdAt.map(_.toLocalDateTime)
   ```
3. Format it into ISO strings like `"2025-07-29T20:53:13"` for clean API responses

---

## 🧠 Summary

| Decision                         | Why                                               |
|----------------------------------|----------------------------------------------------|
| Start with `LocalDateTime`       | Modern, clean domain type                         |
| Switch to `Timestamp`            | Matches MySQL timestamp format natively           |
| Add custom JSON formatters       | Play can’t serialize `Timestamp` automatically    |
| Use epoch millis for now         | Works well — easy and concise                     |
| Plan to refactor with DTOs       | Separate API format from DB format cleanly        |

---

## ✅ Current Status

- ✅ Using `Timestamp` in `Employee.scala` and `EmployeeTable.scala`
- ✅ Custom JSON formatter based on epoch milliseconds
- ✅ `GET /employees` works
- 🔜 Will switch to DTOs later for cleaner serialization
