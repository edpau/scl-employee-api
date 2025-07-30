package employee

import play.api.libs.json.Json
import play.api.mvc._
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext


@Singleton
class EmployeeController @Inject()(
  cc: ControllerComponents,
  employeeService: EmployeeService
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getAllEmployees = Action.async { implicit request =>
    employeeService.getAllEmployees().map { employees =>
      Ok(Json.toJson(employees))
    }
  }

}
