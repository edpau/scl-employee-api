package employee

import play.api.libs.json._
import java.time.LocalDateTime

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

case class CreateEmployeeDto(
  firstName: String,
  lastName: String,
  email: String,
  mobileNumber: Option[String],
  address: String
)

case class UpdateEmployeeDto(
  firstName: Option[String],
  lastName: Option[String],
  email: Option[String],
  mobileNumber: Option[String],
  address: Option[String]
)

object EmployeeResponse {
  implicit val format: OFormat[EmployeeResponse] = Json.format[EmployeeResponse]

  def fromModel(model: Employee): EmployeeResponse = {
    EmployeeResponse(id = model.id.getOrElse(0), firstName = model.firstName, lastName = model.lastName, email = model.email, mobileNumber = model.mobileNumber, address = model.address, createdAt = model.createdAt.map(_.toLocalDateTime), updatedAt = model.updatedAt.map(_.toLocalDateTime))
  }
}

object CreateEmployeeDto {
  implicit val reads: Reads[CreateEmployeeDto] = Json.reads[CreateEmployeeDto]
}

object UpdateEmployeeDto {
  implicit val reads: Reads[UpdateEmployeeDto] = Json.reads[UpdateEmployeeDto]
}
