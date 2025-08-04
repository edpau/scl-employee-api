package utils.validation

import java.time.LocalDate

trait Validator {
  def isNotEmpty(fieldName: String, value: String): Option[(String, String)] =
    if (value.trim.isEmpty) Some(fieldName -> s"$fieldName cannot be empty")
    else None

  def isNonBlankIfDefined(fieldName: String, value: Option[String]): Option[(String, String)] =
    value match {
      case Some(v) if v.trim.isEmpty => Some(fieldName -> s"$fieldName cannot be blank if provided")
      case _ => None
    }

  def isPositive(fieldName: String, value: Int): Option[(String, String)] =
    if (value <= 0) Some(fieldName -> s"$fieldName must be greater than 0")
    else None

  def isPositiveIfDefined(fieldName: String, value: Option[Int]): Option[(String, String)] =
    value match {
      case Some(v) if (v <= 0) => Some(fieldName -> s"$fieldName must be greater than 0")
      case _ => None
    }

  def isDateRangeValid(startDate: LocalDate, endDate: Option[LocalDate]): Option[(String, String)] = {
    endDate match {
      case Some(end) if !startDate.isBefore(end) =>
        Some("endDate" -> "endDate must be after startDate")
      case _ => None
    }
  }

    def isDateRangeValidIfDefined(startDate: Option[LocalDate], endDate: Option[LocalDate]): Option[(String, String)] = {
      (startDate, endDate) match {
        case (Some(start), Some(end)) if !start.isBefore(end) =>
          Some("endDate" -> "endDate must be after startDate")
        case _ => None
      }
    }

}
