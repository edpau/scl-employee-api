package employee

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import employee.EmployeeTable.employees

  def findAll(): Future[Seq[Employee]] = {
    db.run(employees.result)
  }

  def findById(id: Int): Future[Option[Employee]] = {
    db.run(employees.filter(_.id === id).result.headOption)
  }

//  def create(employee: Employee): Future[Employee] = {
//    val insertQuery = employees returning employees.map(_.id) into ((employee, id) => employee.copy(id = Some(id)))
//    db.run(insertQuery += employee)
//  }

  def create(insertEmp: InsertEmployee): Future[Employee] = {
    val insertQuery = employees
      .map(e => (e.firstName, e.lastName, e.email, e.mobileNumber, e.address))
      .returning(employees.map(_.id))
      .into { case ((firstName, lastName, email, mobileNumber, address), id) =>
        Employee(
          id = Some(id),
          firstName = firstName,
          lastName = lastName,
          email = email,
          mobileNumber = mobileNumber,
          address = address,
          createdAt = None, // Let DB fill this
          updatedAt = None
        )
      }

    db.run(insertQuery += (insertEmp.firstName, insertEmp.lastName, insertEmp.email, insertEmp.mobileNumber, insertEmp.address))
  }


}
