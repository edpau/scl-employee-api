package contract

import java.sql.Timestamp
import java.time.LocalDate

case class Contract(
  id: Option[Int] = None,
  employeeId: Int,
  contractType: String,
  employmentType: String,
  startDate: LocalDate,
  endDate: Option[LocalDate],
  hoursPerWeek: Int,
  createdAt: Option[Timestamp] = None,
  updatedAt: Option[Timestamp] = None
)

