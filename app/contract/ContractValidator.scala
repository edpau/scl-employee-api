package contract

import utils.validation.Validator

object ContractValidator extends Validator {
  def validateCreate(dto: CreateContractDto): Map[String, String] = {
    List(
      isPositive("employeeId", dto.employeeId),
      isNotEmpty("contractType", dto.contractType),
      isNotEmpty("employmentType", dto.employmentType),
      isPositive("hoursPerWeek", dto.hoursPerWeek),
      isDateRangeValid(dto.startDate, dto.endDate)
    ).flatten.toMap
  }

  def validatePatch(dto: UpdateContractDto): Map[String, String] = {
    List(
      isPositiveIfDefined("employeeId", dto.employeeId),
      isNonBlankIfDefined("contractType", dto.contractType),
      isNonBlankIfDefined("employmentType", dto.employmentType),
      isPositiveIfDefined("hoursPerWeek", dto.hoursPerWeek),
      isDateRangeValidIfDefined(dto.startDate, dto.endDate)
    ).flatten.toMap
  }
}
