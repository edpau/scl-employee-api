# 🧭 How I Wrote `EmployeeController` Without a Service Layer

## ✅ Why I Skipped the Service (for Now)

In a typical domain-based backend, the controller delegates business logic to a service layer.  
But for learning purposes, I chose to skip the service temporarily and directly inject the `EmployeeRepository` into my `EmployeeController`.

> I wanted to:
> - Understand how Play's controller works with async actions
> - Get a working GET endpoint without building the full service yet
> - Build confidence incrementally before introducing DTOs, validation, and business logic

---

## 🔧 Controller Code

```scala
package employee

import play.api.libs.json.Json
import play.api.mvc._
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class EmployeeController @Inject()(
  repo: EmployeeRepository,
  cc: ControllerComponents
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getAllEmployees = Action.async { implicit request =>
    repo.findAll().map { people =>
      Ok(Json.toJson(people))
    }
  }
}
```

---

## 🔍 Code Breakdown

### 🔹 `@Singleton`
Ensures only one instance of the controller is created by Play’s dependency injection system.

### 🔹 `@Inject() (...)` Constructor
Play will automatically inject:
- The `EmployeeRepository`, which talks to the database
- `ControllerComponents`, which provides useful controller helpers

### 🔹 `implicit ec: ExecutionContext`
Required for handling asynchronous `.map` on Futures. Provided implicitly to Slick and Play's async operations.

---

### 🔹 `def getAllEmployees = Action.async { ... }`

- `Action.async` creates an asynchronous HTTP endpoint.
- `implicit request` gives access to the incoming HTTP request if needed.
- `repo.findAll()` returns a `Future[Seq[Employee]]`
- `.map { people => ... }` waits for the DB call to finish, then wraps the result in `Ok(...)`, returning an HTTP 200 with JSON.

---

### 🔹 JSON Serialization

To serialize `Employee` instances to JSON, I added this companion object:

```scala
object Employee {
  implicit val format: OFormat[Employee] = Json.format[Employee]
}
```

This enables Play’s `Json.toJson(...)` to convert the employee list automatically.

---

## 🚀 Why This Was a Useful First Step

- I tested that my repository, table, and domain model work correctly
- I confirmed I can hit `/employees` and see real data returned from MySQL
- I learned how to create a working API route without needing all layers upfront

---

## 🧠 Next Step

Once this works, I’ll gradually refactor:
- Move logic into `EmployeeService`
- Add `EmployeeDTO` for clean API contracts
- Add error handling and validation
