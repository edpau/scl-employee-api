package contract

import play.api.libs.json.{Format, Json, OFormat, Reads, Writes}

import java.sql.Timestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class Contract(
  id: Option[Int] = None,
  employeeId: Int,
  contractType: String,
  employmentType: String,
  startDate: LocalDate,
  endDate: Option[LocalDate],
  hoursPerWeek: Int,
  createdAt: Option[Timestamp] = None,
  updatedAt: Option[Timestamp] = None
)

object Contract {
  implicit val timestampReads: Reads[Timestamp] = {
    implicitly[Reads[Long]].map(new Timestamp(_))
  }

  implicit val timestampWrites: Writes[Timestamp] = {
    implicitly[Writes[Long]].contramap(_.getTime)
  }

  implicit val localDateReads: Reads[LocalDate] = {
    Reads.localDateReads("yyyy-MM-dd")
  }

  implicit val localDateFormat: Format[LocalDate] =
    Format(
      Reads.localDateReads("yyyy-MM-dd"),
      Writes.temporalWrites[LocalDate, DateTimeFormatter](DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    )

  implicit val format: OFormat[Contract] = Json.format[Contract]
}

case class InsertContract(
  employeeId: Int,
  contractType: String,
  employmentType: String,
  startDate: LocalDate,
  endDate: Option[LocalDate],
  hoursPerWeek: Int
)