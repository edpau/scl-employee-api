package employee

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeService @Inject()(employeeRepository: EmployeeRepository)(implicit ec: ExecutionContext) {

  def getAllEmployees(): Future[Seq[EmployeeResponse]] = {
    employeeRepository.findAll().map { employees =>
      employees.map(EmployeeResponse.fromModel)
    }
  }

}
