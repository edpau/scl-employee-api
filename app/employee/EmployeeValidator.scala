package employee

import utils.validation.Validator

object EmployeeValidator extends Validator{
  def validateCreate(dto: CreateEmployeeDto): Map[String, String] = {
    List(
      isNotEmpty("firstName", dto.firstName),
      isNotEmpty("lastName", dto.lastName),
      isNotEmpty("email", dto.email),
      isNonBlankIfDefined("mobileNumber", dto.mobileNumber),
      isNotEmpty("address", dto.address),
    ).flatten.toMap
  }

  def validatePatch(dto: UpdateEmployeeDto): Map[String, String] = {
    List(
      isNonBlankIfDefined("firstName", dto.firstName),
      isNonBlankIfDefined("lastName", dto.lastName),
      isNonBlankIfDefined("email", dto.email),
      isNonBlankIfDefined("mobileNumber", dto.mobileNumber),
      isNonBlankIfDefined("address", dto.address)
    ).flatten.toMap
  }
}
