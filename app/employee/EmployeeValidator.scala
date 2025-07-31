package employee

import utils.validation.Validator

object EmployeeValidator extends Validator{
  def validateCreate(dto: CreateEmployeeDto): Map[String, String] = {
    List(
      isNotEmpty("firstName", dto.firstName),
      isNotEmpty("lastName", dto.lastName),
      isNotEmpty("email", dto.email),
      isNoneBlankIfDefined("mobileNumber", dto.mobileNumber),
      isNotEmpty("address", dto.address),
    ).flatten.toMap
  }
}
