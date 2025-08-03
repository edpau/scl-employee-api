package contract

import play.api.libs.json._
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

case class ContractResponse(
  id: Int,
  employeeId: Int,
  contractType: String,
  employmentType: String,
  startDate: LocalDate,
  endDate: Option[LocalDate],
  hoursPerWeek: Int,
  createdAt: Option[LocalDateTime],
  updatedAt: Option[LocalDateTime]
)

case class CreateContractDto(
  employeeId: Int,
  contractType: String,
  employmentType: String,
  startDate: LocalDate,
  endDate: Option[LocalDate],
  hoursPerWeek: Int,
)

case class UpdateContractDto(
  employeeId: Option[Int],
  contractType: Option[String],
  employmentType: Option[String],
  startDate: Option[LocalDate],
  endDate: Option[LocalDate],
  hoursPerWeek: Option[Int],
)

object ContractResponse {

  implicit val localDateFormat: Format[LocalDate] = Format(
    Reads.localDateReads("yyyy-MM-dd"),
    Writes.temporalWrites[LocalDate, DateTimeFormatter](DateTimeFormatter.ofPattern("yyyy-MM-dd"))
  )

  implicit val format: OFormat[ContractResponse] = Json.format[ContractResponse]

  def fromModel(model: Contract): ContractResponse = {
    ContractResponse(
      id = model.id.getOrElse(0),
      employeeId = model.employeeId,
      contractType = model.contractType,
      employmentType = model.employmentType,
      startDate = model.startDate,
      endDate = model.endDate,
      hoursPerWeek = model.hoursPerWeek,
      createdAt = model.createdAt.map(_.toLocalDateTime),
      updatedAt = model.updatedAt.map(_.toLocalDateTime)
    )
  }
}

object CreateContractDto {
  implicit val reads: Reads[CreateContractDto] = Json.reads[CreateContractDto]
}

object UpdateContractDto {
  implicit val reads: Reads[UpdateContractDto] = Json.reads[UpdateContractDto]
}