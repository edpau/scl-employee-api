# 🧱 Understanding `ApiError` in Scala Play Framework

This note explains what `ApiError` is, why it's structured the way it is, and how to use it effectively for safe, consistent, and centralized error handling in your Play application.

---

## ✅ What Is `ApiError`?

`ApiError` is a **sealed trait** that represents different types of HTTP/API-related errors you might return to the frontend — like 400 (Bad Request), 404 (Not Found), 500 (Internal Server Error), and validation errors.

It’s used to:
- Centralize your error response structure
- Create reusable error types across your app
- Cleanly convert error states to `Result` objects (JSON + HTTP status)

---

## 💡 Why Use It?

- ✅ Avoid repetitive boilerplate when building `BadRequest(...)`, `NotFound(...)`, etc.
- ✅ Encourage **type-safe** error handling in services and controllers
- ✅ Improve code readability and maintainability
- ✅ Standardize how errors look in the API (same JSON structure)

---

## 🔧 How It’s Written

### 1. Define a `sealed trait`

```scala
sealed trait ApiError {
  def message: String
  def toResult: Result
}
```

This acts as the **base type**. All other error types will extend this.

---

### 2. Use a companion `object` to define concrete error types

```scala
object ApiError {
  case class BadRequest(message: String) extends ApiError {
    def toResult: Result = Results.BadRequest(Json.obj("error" -> message))
  }

  case class NotFound(message: String) extends ApiError {
    def toResult: Result = Results.NotFound(Json.obj("error" -> message))
  }

  case class InternalServerError(message: String) extends ApiError {
    def toResult: Result = Results.InternalServerError(Json.obj("error" -> message))
  }

  case class ValidationError(errors: Map[String, String]) extends ApiError {
    def message: String = "Validation failed"
    def toResult: Result = Results.BadRequest(Json.obj(
      "error" -> message,
      "validation_errors" -> errors
    ))
  }

  case class InvalidJson(errors: JsError) extends ApiError {
    def message: String = "Invalid JSON"
    def toResult: Result = {
      val errorDetails = JsError.toJson(errors)
      Results.BadRequest(Json.obj("error" -> message, "details" -> errorDetails))
    }
  }
}
```

---

## 🔍 Why Use `sealed trait ApiError`?

| Reason                         | Explanation |
|--------------------------------|-------------|
| ✅ Enforces structure          | Only known error types can extend it (in the same file) |
| ✅ Pattern matching safe       | Compiler can check if you handled all cases |
| ✅ Groups related logic        | All errors belong to a common hierarchy |

---

## 🧠 Why Use `object ApiError + case class ...`?

Using a companion object to hold all error case classes:

- 🔹 Makes usage simple and readable: `ApiError.BadRequest(...)`
- 🔹 Avoids name clashes with Play’s built-in `BadRequest` or `Results.BadRequest`
- 🔹 Keeps all error logic **scoped, organized, and grouped**

---

## 🛠 How You Use It in Code

### In a Service or Controller

```scala
def getCategoryById(id: Long): Future[Either[ApiError, CategoryResponse]] = {
  repository.findById(id).map {
    case Some(category) => Right(CategoryResponse.fromModel(category))
    case None => Left(ApiError.NotFound(s"Category $id not found"))
  }
}
```

### In Controller

```scala
def getCategoryById(id: Long) = Action.async {
  service.getCategoryById(id).map {
    case Right(response) => Ok(Json.toJson(response))
    case Left(error)     => error.toResult
  }
}
```

---

## ✅ Summary

| Concept                      | What It Means |
|-----------------------------|----------------|
| `sealed trait ApiError`     | Base type for all API error types |
| `case class ... extends ApiError` | Specific error types with custom behavior |
| `toResult`                  | Method that converts error into HTTP response |
| `object ApiError`           | Groups all error types in one place for clean usage |
| Usage                       | Call `Left(ApiError.BadRequest(...))` and return `error.toResult` in controller |

By using this pattern, you make your error handling:
- ✅ Consistent
- ✅ Centralized
- ✅ Extensible
- ✅ Safe
