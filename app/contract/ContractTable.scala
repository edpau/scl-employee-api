package contract

import employee.Employees
import slick.jdbc.MySQLProfile.api._
import java.sql.Timestamp
import java.time.LocalDate

class Contracts(tag: Tag) extends Table[Contract](tag, "contracts") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def employeeId = column[Int]("employee_id")

  def contractType = column[String]("contract_type")

  def employmentType = column[String]("employment_type")

  def startDate = column[LocalDate]("start_date")

  def endDate = column[Option[LocalDate]]("end_date")

  def hoursPerWeek = column[Int]("hours_per_week")

  def createdAt = column[Option[Timestamp]]("created_at")

  def updatedAt = column[Option[Timestamp]]("updated_at")

  def contractFk = foreignKey(
    "fk_employee",
    employeeId,
    TableQuery[Employees]
  )(_.id, onDelete = ForeignKeyAction.Cascade)

  def * = (
    id.?,
    employeeId,
    contractType,
    employmentType,
    startDate,
    endDate,
    hoursPerWeek,
    createdAt,
    updatedAt
  ) <> ((Contract.apply _).tupled, Contract.unapply)

}

object ContractTable {
  val contracts = TableQuery[Contracts]
}
