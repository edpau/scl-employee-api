package employee
import slick.jdbc.MySQLProfile.api._
import java.time.LocalDateTime

class Employees(tag: Tag) extends Table[Employee](tag, "employees") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def firstName = column[String]("first_name")

  def lastName = column[String]("last_name")

  def email = column[String]("email")

  def mobileNumber = column[Option[String]]("mobile_number")

  def address = column[String]("address")

  def createdAt = column[Option[LocalDateTime]]("created_at")

  def updatedAt = column[Option[LocalDateTime]]("updated_at")

  def * = (
    id.?,
    firstName,
    lastName,
    email,
    mobileNumber,
    address,
    createdAt,
    updatedAt
  ) <> (Employee.tupled, Employee.unapply)
}

object EmployeeTable {
  val employees = TableQuery[Employees]
}