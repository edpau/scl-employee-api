package contract

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ContractRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import ContractTable.contracts

  def findAll(): Future[Seq[Contract]] = {
    db.run(contracts.result)
  }

  def findByEmployeeId(employeeId: Int): Future[Seq[Contract]] = {
    db.run(
      contracts
        .filter(_.employeeId === employeeId)
        .sortBy(_.startDate)
        .take(1000)
        .result
    )
  }

  def create(data: InsertContract): Future[Contract] = {
    val insertQuery = contracts
      .map(_.insertProjection) // Only the insertable columns
      .returning(contracts.map(_.id))
      .into((data, id) =>
        Contract(
          id = Some(id),
          employeeId = data.employeeId,
          contractType = data.contractType,
          employmentType = data.employmentType,
          startDate = data.startDate,
          endDate = data.endDate,
          hoursPerWeek = data.hoursPerWeek,
          createdAt = None, // DB will handle
          updatedAt = None
        )
      )

    db.run(insertQuery += data)
  }

}
