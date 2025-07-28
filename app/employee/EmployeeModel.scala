package employee

import java.time.LocalDateTime

case class Employee(
  id: Option[Int] = None,
  firstName: String,
  lastName: String,
  email: String,
  mobileNumber: Option[String] = None,
  address: String,
  // Managed by the DB — not manually set during creation
  createdAt: Option[LocalDateTime] = None,
  updatedAt: Option[LocalDateTime] = None
) {
  def fullName: String = s"$firstName $lastName"
}