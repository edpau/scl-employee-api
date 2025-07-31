package employee

import com.google.protobuf.Timestamp

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import utils.ApiError

@Singleton
class EmployeeService @Inject()(employeeRepository: EmployeeRepository)(implicit ec: ExecutionContext) {

  def getAllEmployees(): Future[Seq[EmployeeResponse]] = {
    employeeRepository.findAll().map { employees =>
      employees.map(EmployeeResponse.fromModel)
    }
  }

  def getEmployeeById(id: Int): Future[Either[ApiError, EmployeeResponse]] = {
    employeeRepository.findById(id).map {
      case Some(employee) => Right(EmployeeResponse.fromModel(employee))
      case None => Left(ApiError.NotFound(s"Employee with id $id not found"))
    }
  }

  def createEmployee(data: CreateEmployeeDto): Future[Either[ApiError, EmployeeResponse]] = {
    val errors = EmployeeValidator.validateCreate(data);
    if (errors.nonEmpty) {
      Future.successful(Left(ApiError.ValidationError(errors)))
    } else {
      val insert = InsertEmployee(
        firstName = data.firstName.trim,
        lastName = data.lastName.trim,
        email = data.email.trim,
        mobileNumber = data.mobileNumber.map(_.trim).filter(_.nonEmpty),
        address = data.address.trim
      )
      employeeRepository.create(insert).map(saved => Right(EmployeeResponse.fromModel(saved)))
    }
  }

}
