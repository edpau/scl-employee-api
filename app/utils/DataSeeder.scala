package utils

import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import employee.EmployeeTable.employees
import employee.{Employee, InsertEmployee}
import contract.ContractTable.contracts
import contract.{Contract, InsertContract}
import java.time.LocalDate

@Singleton
class DataSeeder @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit
  ec: ExecutionContext
) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def seed(): Future[Unit] = {
    val setup = for {
      employeesExist <- employees.exists.result
      contractsExist <- contracts.exists.result

      employeeRows <-
        if (!employeesExist) {
          val insertQuery =
            employees.map(_.insertProjection) returning employees.map(_.id) into {
              case (insertEmp, id) =>
                Employee(
                  id = Some(id),
                  firstName = insertEmp.firstName,
                  lastName = insertEmp.lastName,
                  email = insertEmp.email,
                  mobileNumber = insertEmp.mobileNumber,
                  address = insertEmp.address,
                  createdAt = None,
                  updatedAt = None
                )
            }

          val initialInsertEmployees = Seq(
            InsertEmployee("Alice", "Smith", "alice.smith@example.com", Some("07123456789"), "10 Downing St, London"),
            InsertEmployee("Bob", "Johnson", "bob.johnson@example.com", None, "42 Wallaby Way, Sydney"),
            InsertEmployee("Charlie", "Lee", "charlie.lee@example.com", Some("07987654321"), "123 Elm St, Springfield")
          )

          insertQuery ++= initialInsertEmployees
        } else {
          employees.result
        }

      empNameMap: Map[String, Int] = employeeRows.map(emp => emp.fullName -> emp.id.get).toMap

      _ <- {
        if (!contractsExist) {
          val insertQuery =
            contracts.map(_.insertProjection) returning contracts.map(_.id) into {
              case (insertContract, id) =>
                Contract(
                  id = Some(id),
                  employeeId = insertContract.employeeId,
                  contractType = insertContract.contractType,
                  employmentType = insertContract.employmentType,
                  startDate = insertContract.startDate,
                  endDate = insertContract.endDate,
                  hoursPerWeek = insertContract.hoursPerWeek,
                  createdAt = None,
                  updatedAt = None
                )
            }

          val initialContracts = Seq(
            InsertContract(
              empNameMap("Alice Smith"),
              "Fixed-term",
              "Part-time",
              LocalDate.now(),
              Some(LocalDate.now().plusYears(1)),
              20
            ),
            InsertContract(
              empNameMap("Bob Johnson"),
              "Permanent",
              "Full-time",
              LocalDate.now().minusYears(1),
              None,
              40
            )
          )

          insertQuery ++= initialContracts
        } else {
          DBIO.successful(())
        }
      }
    } yield ()

    db.run(setup.transactionally)
  }
}
