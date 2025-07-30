# 🌍 What Is `EmployeeController.scala`

---

## ✅ What Is a Controller?

A **controller** in Play Framework is the **entry point for HTTP requests** (like `/employees`).

It receives requests from the browser or frontend, and connects them to your application logic.

In general terms, a controller:

- Accepts and parses incoming HTTP requests (e.g. JSON, form data)
- Calls the appropriate service methods
- Converts data into responses (e.g. JSON or HTML)
- Sends back `Ok(...)`, `BadRequest(...)`, `NotFound(...)`, etc.

---

## 🧩 Why Do We Use It?

The controller acts as the **boundary between the web and your app**:

| Role                          | Explanation                            |
|-------------------------------|----------------------------------------|
| Receives input                | HTTP `GET`, `POST`, etc.               |
| Parses data                   | Reads JSON and converts to DTO         |
| Delegates logic               | Calls services, doesn’t do logic itself|
| Sends response                | Returns HTTP response with status code |

---

## 🧱 Example

```scala
class EmployeeController @Inject() (
  cc: ControllerComponents,
  employeeService: EmployeeService
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getEmployee(id: Long): Action[AnyContent] = Action.async {
    employeeService.getById(id).map {
      case Some(emp) => Ok(Json.toJson(EmployeeResponseDto.fromDomain(emp)))
      case None => NotFound(s"Employee $id not found")
    }
  }

  def createEmployee: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreateEmployeeDto].fold(
      errors => Future.successful(BadRequest("Invalid JSON")),
      dto => employeeService.create(dto).map {
        case Right(emp) => Created(Json.toJson(EmployeeResponseDto.fromDomain(emp)))
        case Left(err)  => BadRequest(err.message)
      }
    )
  }
}
```

---

## 🧭 Why It Comes Last

The controller:
- Depends on the **service**, which uses the **repository**, which uses the **table**
- Uses **DTOs** for parsing and returning JSON

So:
- ✅ Step 1: Domain model
- ✅ Step 2: Table
- ✅ Step 3: Repository
- ✅ Step 4: Service
- ✅ Step 5: Controller (this file)

---

## 🧠 Typical Responsibilities

| Task                            | Controller? | Notes                            |
|----------------------------------|-------------|----------------------------------|
| Parse JSON input                | ✅ Yes      | Using Play’s `parse.json`        |
| Call service                    | ✅ Yes      | Pass DTO → service               |
| Return HTTP result              | ✅ Yes      | `Ok`, `Created`, `BadRequest`, etc. |
| Query DB directly               | ❌ No       | Use service → repository         |
| Apply business rules            | ❌ No       | That belongs in the service      |

---

## ✅ Summary

| Purpose             | `EmployeeController.scala`                          |
|---------------------|-----------------------------------------------------|
| Handles HTTP        | Yes (`GET`, `POST`, etc.)                           |
| Depends on          | `EmployeeService`, DTOs                             |
| Used by             | Play's router (`conf/routes`)                       |
| Contains            | No business logic — delegates to services           |
| Outputs             | `Result` with JSON or status code                   |

This is the **outermost layer** of your backend, directly facing the browser or client.

