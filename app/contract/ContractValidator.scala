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
}
