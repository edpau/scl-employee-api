package contract

import employee.EmployeeResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import utils.ApiError

@Singleton
class ContractService @Inject()(contractRepository: ContractRepository)(implicit ec: ExecutionContext) {

  def getAllContracts(): Future[Seq[ContractResponse]] = {
    contractRepository.findAll().map { contracts =>
      contracts.map(ContractResponse.fromModel)
    }
  }

  def getContractById(id: Int): Future[Either[ApiError, ContractResponse]] = {
    contractRepository.findById(id).map {
      case Some(contract) => Right(ContractResponse.fromModel(contract))
      case None => Left(ApiError.NotFound(s"Contract with id $id not found"))
    }
  }

  def getContractsByEmployeeId(employeeId: Int): Future[Either[ApiError, Seq[ContractResponse]]] = {
    contractRepository.findByEmployeeId(employeeId).map { contracts =>
      if (contracts.nonEmpty) Right(contracts.map(ContractResponse.fromModel))
      else Left(ApiError.NotFound(s"No contracts found for employee with id $employeeId"))
    }
  }

  def createContract(data: CreateContractDto): Future[Either[ApiError, ContractResponse]] = {
    val errors = ContractValidator.validateCreate(data);
    if (errors.nonEmpty) {
      Future.successful(Left(ApiError.ValidationError(errors)))
    } else {
      val insert = InsertContract(
        employeeId = data.employeeId,
        contractType = data.contractType.trim,
        employmentType = data.employmentType.trim,
        startDate = data.startDate,
        endDate = data.endDate,
        hoursPerWeek = data.hoursPerWeek
      )
      contractRepository.create(insert).map(saved => Right(ContractResponse.fromModel(saved)))
    }
  }

  def updateContractById(id: Int, data: UpdateContractDto): Future[Either[ApiError, ContractResponse]] = {
    val errors = ContractValidator.validatePatch(data);
    if (errors.nonEmpty) {
      Future.successful(Left(ApiError.ValidationError(errors)))
    } else {
      contractRepository.findById(id).flatMap {
        case None => Future.successful(Left(ApiError.NotFound(s"Contract with id $id not found")))
        case Some(existing) =>

          val updated = existing.copy(
            employeeId = data.employeeId.getOrElse(existing.employeeId),
            contractType = data.contractType.map(_.trim).getOrElse(existing.contractType),
            employmentType = data.employmentType.map(_.trim).getOrElse(existing.employmentType),
            startDate = data.startDate.getOrElse(existing.startDate),
            endDate = data.endDate.orElse(existing.endDate),
            hoursPerWeek = data.hoursPerWeek.getOrElse(existing.hoursPerWeek)
          )

          contractRepository.update(updated).map(c => Right(ContractResponse.fromModel(c)))
      }
    }
  }

}
