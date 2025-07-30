package employee

import play.api.libs.json.{Json, OFormat}

import java.sql.Timestamp

import play.api.libs.json._

case class Employee(
  id: Option[Int] = None,
  firstName: String,
  lastName: String,
  email: String,
  mobileNumber: Option[String] = None,
  address: String,
  // Managed by the DB — not manually set during creation
  createdAt: Option[Timestamp] = None,
  updatedAt: Option[Timestamp] = None
) {
  def fullName: String = s"$firstName $lastName"
}

object Employee {
  implicit val timestampReads: Reads[Timestamp] = {
    implicitly[Reads[Long]].map(new Timestamp(_))
  }

  implicit val timestampWrites: Writes[Timestamp] = {
    implicitly[Writes[Long]].contramap(_.getTime)
  }

  implicit val format: OFormat[Employee] = Json.format[Employee]
}