package utils

import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Result, Results}
import play.api.libs.json.JsError

sealed trait ApiError {
  def message: String

  def toResult: Result
}

object ApiError {
  case class BadRequest(message: String) extends ApiError {
    def toResult: Result = Results.BadRequest(Json.obj("error" -> message))
  }

  case class NotFound(message: String) extends ApiError {
    def toResult: Result = Results.NotFound(Json.obj("error" -> message))
  }

  case class InternalServerError(message: String) extends ApiError {
    def toResult: Result = Results.InternalServerError(Json.obj("error" -> message))
  }

  case class ValidationError(errors: Map[String, String]) extends ApiError {
    def message: String = "Validation failed"

    def toResult: Result = Results.BadRequest(Json.obj("error" -> message, "validation_errors" -> errors))
  }

  case class InvalidJson(errors: JsError) extends ApiError {

    def message: String = "Invalid JSON"

    def toResult: Result = {
      val errorDetails = JsError.toJson(errors)
      Results.BadRequest(Json.obj(
        "error" -> message,
        "details" -> errorDetails
      ))
    }
  }
}