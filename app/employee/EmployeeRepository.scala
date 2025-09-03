package employee

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import javax.inject.{Inject, Singleton}
import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

import org.slf4j.LoggerFactory

@Singleton
class EmployeeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import employee.EmployeeTable.employees

  private val log = LoggerFactory.getLogger(getClass)


  def findAll(): Future[Seq[Employee]] = {
    db.run(employees.result)
  }

  def findById(id: Int): Future[Option[Employee]] = {
    db.run(employees.filter(_.id === id).result.headOption)
  }

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
      // .andThen is added here for learning/debugging purposes only.
      // In production, the Future’s failure would propagate naturally
      // and be handled/logged at the service or controller level.
      .andThen { case scala.util.Failure(ex) =>
        unwrapSql(ex).foreach { sql =>
          log.error(s"DB error: vendorCode=${sql.getErrorCode} sqlState=${sql.getSQLState} msg=${sql.getMessage}", sql)
        }
      }
  }

  def update(employee: Employee): Future[Employee] = {
    val query = employees.filter(_.id === employee.id.get)
      .map(e => (e.firstName, e.lastName, e.email, e.mobileNumber, e.address))
      .update((employee.firstName, employee.lastName, employee.email, employee.mobileNumber, employee.address))

    db.run(query).map(_ => employee)
  }

  def delete(id: Int): Future[Int] = {
    db.run(employees.filter(_.id === id).delete)
  }


  // --- private helpers ---
  @tailrec
  private def unwrapSql(t: Throwable): Option[java.sql.SQLException] = t match {
    case sql: java.sql.SQLException => Some(sql)
    case null => None
    case other => unwrapSql(other.getCause)
  }

}
