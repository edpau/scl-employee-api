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
